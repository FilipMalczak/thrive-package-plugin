package com.github.thriveframework.plugin;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.thriveframework.plugin.impl.DirectoryPackage;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Port;
import com.github.thriveframework.plugin.model.Service;
import com.github.thriveframework.plugin.model.compose.Root;
import com.github.thriveframework.plugin.model.compose.ServiceDef;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.toList;

public class GeneratorApp {
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Generator {
        //todo help
        @Parameter(names = {"-w", "--workspace"}, description = "", required = true, arity = 1)
        File workspace;

        @Parameter(names = {"-p", "--profile"}, description = "")
        List<String> profiles = new LinkedList<>();

        @Parameter
        List<File> packageDirs = new LinkedList<>();

        Map<String, List<String>> profileToFacets = new HashMap<>();
        Set<String> usedFacets = new HashSet<>();

        void init(){
            for (String profile: profiles){
                String[] byColon = profile.split("[:]");
                if (byColon.length == 1){
                    profileToFacets.put(byColon[0], emptyList());
                } else if (byColon.length == 2){
                    String name = byColon[0];
                    String joinedFacets = byColon[1];
                    String[] singleFacets = joinedFacets.split("[+]");
                    profileToFacets.put(name, Stream.of(singleFacets).filter(s -> !s.isEmpty()).collect(toList()));
                } else {
                    throw new IllegalArgumentException("Profiles has to of form <name>:facet1,facet2! Gotten '"+profile+"' instead!");
                }
            }
            System.out.println("Found following profile config: "+profileToFacets);

            profileToFacets.values().forEach(usedFacets::addAll);
            System.out.println("Found following used facets: "+usedFacets);
        }

        public void generate(){
            init();
            System.out.println("Generating main docker-compose.yml");
            save(workspace, "docker-compose.yml", generateCompose(mainFacet()));
            for (String f: usedFacets) {
                System.out.println("Generating docker-compose-"+f+".yml for facet "+f);
                save(workspace, "docker-compose-" + f + ".yml", generateCompose(facet(f)));
            }
        }



        @SneakyThrows
        private void save(File root, String filename, String content){
            root.mkdirs();
            try (
                FileWriter fw = new FileWriter(new File(root, filename));
                BufferedWriter bw  = new BufferedWriter(fw);
            ){
                //todo cleanup? I cannot remember Java basics oO
                bw.write(content);
            }
        }

        @FunctionalInterface
        private interface FacetSpec extends Function<Composition, List<Service>> {}

        private FacetSpec mainFacet(){
            return c -> c.getServices();
        }

        private FacetSpec facet(String name){
            return c -> c.getFacets().getOrDefault(name, emptyList());
        }

        private String generateCompose(FacetSpec facetSpec){
            Root.RootBuilder root = Root.builder()
                .version("3.5");
            //fixme I dont like forEach, and streams are pretty awkward here :/
            getAllPackages().forEach(tp ->
                facetSpec.apply(tp.getComposition()).forEach(s -> buildService(root, s))
            );
            return toYaml(root.build());
        }

        private void buildService(Root.RootBuilder builder, Service service){
            ServiceDef.ServiceDefBuilder serviceBuilder = ServiceDef.builder()
                .imageDefinition(service.getDefinition())
                .environment(service.getEnvironment());
            for (Port p: service.getPorts())
                //fixme theres a quirk here, something with localhost or smth, that can break firewall config
                serviceBuilder.port(p.getExternal()+":"+p.getInternal());
            Set<String> allDeps = new HashSet<>();
            allDeps.addAll(service.getRuntimeDependencies());
            allDeps.addAll(service.getStartupDependencies());
            serviceBuilder.depends_on(new ArrayList<>(allDeps));
            serviceBuilder.links(new ArrayList<>(allDeps));
            ServiceDef s = serviceBuilder.build();
            builder.service(service.getName(), s);
        }

        private String toYaml(Root root){
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Map<String, Object> asMap = toMap(root);
            Yaml yaml = new Yaml(options);
            return yaml.dump(asMap);
        }

        private Map<String, Object> toMap(Root root){
            Map<String, Object> result = new HashMap<>();
            result.put("version", root.getVersion());
            Map<String, Object> services = new HashMap<>();
            for (String serviceName: root.getServices().keySet()) {
                ServiceDef service = root.getServices().get(serviceName);
                Map<String, Object> s = new HashMap<>();
                if (service.getImageDefinition() != null)
                    s.put(
                        service.getImageDefinition().getComposeKey(),
                        service.getImageDefinition().getImageSpec()
                    );
                s.put("environment", service.getEnvironment());
                s.put("ports", service.getPorts());
                s.put("depends_on", service.getDepends_on());
                s.put("links", service.getLinks());
                removeEmpty(s);
                services.put(serviceName, s);
            }
            result.put("services", services);
            return result;
        }

        private void removeEmpty(Map<String, Object> map){
            List<String> toRemove = new LinkedList<>();
            for (String key: map.keySet()){
                Object val = map.get(key);
                if (val == null) {
                    toRemove.add(key);
                    continue;
                }
                if (val instanceof Collection && ((Collection) val).isEmpty()) {
                    toRemove.add(key);
                    continue;
                }
                if (val instanceof Map && ((Map) val).isEmpty()) {
                    toRemove.add(key);
                    continue;
                }
                if (val instanceof String && ((String) val).isEmpty()) {
                    toRemove.add(key);
                    continue;
                }
            }
            for (String key: toRemove)
                map.remove(key);
        }

        private Stream<ThrivePackage> getAllPackages(){
            return Stream.concat(getPackagesFromClasspath(), getPackagesFromFilesystem());
        }

        private Stream<ThrivePackage> getPackagesFromClasspath(){
            ServiceLoader<ThrivePackage> loader = ServiceLoader.load(ThrivePackage.class);
            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(loader.iterator(), Spliterator.ORDERED),
                false
            )
                .peek(dp -> System.out.println("CpPkg "+dp)).map(dp -> (ThrivePackage) dp);
        }

        private Stream<ThrivePackage> getPackagesFromFilesystem(){
            return packageDirs.stream().map(DirectoryPackage::new)
                .peek(dp -> System.out.println("DirPkg "+dp)).map(dp -> (ThrivePackage) dp);
        }
    }

    public static void main(String[] args){
        System.out.println("ARGS "+asList(args));
        Generator generator = new Generator();
        JCommander.newBuilder().addObject(generator).build().parse(args);
        generator.generate();
    }

}
