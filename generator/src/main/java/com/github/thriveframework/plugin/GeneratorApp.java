package com.github.thriveframework.plugin;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.github.thriveframework.plugin.impl.DirectoryPackage;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Arrays.asList;

public class GeneratorApp {
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    public static class Generator {
        //todo help
        @Parameter(names = {"-w", "--workspace"}, description = "", required = true, arity = 1)
        File workspace;

        @Parameter
        List<File> packageDirs = new LinkedList<>();

        public void generate(){
            saveCompose("docker-compose.yml", generateMainCompose());
        }

        @SneakyThrows
        private void saveCompose(String filename, String content){
            try (
                FileWriter fw = new FileWriter(new File(workspace, filename));
                BufferedWriter bw  = new BufferedWriter(fw);
            ){
                //todo cleanup? I cannot remember Java basics oO
                bw.write(content);
            }
        }

        private String generateMainCompose(){
            Root.RootBuilder root = Root.builder()
                .version("3.5");
            //fixme I dont like forEach, and streams are pretty awkward here :/
            getAllPackages().forEach(tp ->
                tp.getComposition().getServices().forEach(s -> buildService(root, s))
            );
            return toYaml(root.build());
        }

        private void buildService(Root.RootBuilder builder, Service service){
            ServiceDef.ServiceDefBuilder serviceBuilder = ServiceDef.builder()
                .image(service.getImage())
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
                s.put("image", service.getImage());
                s.put("environment", service.getEnvironment());
                s.put("ports", service.getPorts());
                s.put("depends_on", service.getDepends_on());
                s.put("links", service.getLinks());
                services.put(serviceName, s);
            }
            result.put("services", services);
            return result;
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
