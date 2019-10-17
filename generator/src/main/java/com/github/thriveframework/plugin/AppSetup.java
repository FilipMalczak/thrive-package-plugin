package com.github.thriveframework.plugin;

import com.beust.jcommander.Parameter;
import com.github.thriveframework.plugin.facets.FacetSpec;
import com.github.thriveframework.plugin.impl.DirectoryPackage;
import com.github.thriveframework.plugin.model.Service;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.github.thriveframework.plugin.facets.FacetSpec.facet;
import static com.github.thriveframework.plugin.facets.FacetSpec.mainFacet;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class AppSetup {
    //todo help
    @Parameter(names = {"-w", "--workspace"}, description = "", required = true, arity = 1)
    File workspace;

    @Parameter(names = {"-p", "--profile"}, description = "")
    List<String> profiles = new LinkedList<>();

    @Parameter(names = {"-n", "--name"}, description = "", required = true, arity = 1)
    String projectName;

    @Parameter(names = {"-m", "--main"}, description = "")
    String mainName;

    @Parameter
    List<File> packageDirs = new LinkedList<>();

    Map<String, List<String>> profileToFacets = new HashMap<>();
    Set<String> allFacets = new HashSet<>();

    public void init(){
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
                throw new IllegalArgumentException("Profiles has to come in format 'profileName:facet1(+facet2)*'! Gotten '"+profile+"' instead!");
            }
        }
        System.out.println("Found following profile config: "+profileToFacets);

        profileToFacets.values().forEach(allFacets::addAll);
        System.out.println("Found following used facets: "+ allFacets);
    }

    public List<String> getAllProfiles(){
        return new ArrayList<>(profileToFacets.keySet());
    }

    public Stream<ThrivePackage> getAllPackages(){
        return Stream.concat(getPackagesFromClasspath(), getPackagesFromFilesystem());
    }

    private Stream<ThrivePackage> getPackagesFromClasspath(){
        ServiceLoader<ThrivePackage> loader = ServiceLoader.load(ThrivePackage.class);
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(loader.iterator(), Spliterator.ORDERED),
            false
        );
    }

    private Stream<ThrivePackage> getPackagesFromFilesystem(){
        return packageDirs.stream().map(DirectoryPackage::new);
    }

    public Stream<Service> getServices(FacetSpec facetSpec){
        return getAllPackages().flatMap(p -> facetSpec.apply(p.getComposition()).stream());
    }

    public FacetSpec forProfileStartup(String profile){
        FacetSpec spec = mainFacet();
        //todo assert profile exists
        for (String f: profileToFacets.get(profile))
            spec = spec.mergedWith(facet(f));
        return spec;
    }

    @SneakyThrows
    public void save(String filename, String content){
        File f = new File(workspace, filename);
        f.getParentFile().mkdirs();
        try (
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw  = new BufferedWriter(fw);
        ){
            //todo cleanup? I cannot remember Java basics oO
            bw.write(content);
        }
    }

    public String composeFilename(String suffix){
        return composeFilename(suffix, "-");
    }
    public String composeFilename(String suffix, String joiner){
        if (suffix != null && !suffix.trim().isEmpty())
            suffix = joiner+suffix;
        return "config/docker-compose"+suffix+".yml";
    }

    public String scriptFilename(String profile, String name){
        return scriptFilename(profile, name, "sh");
    }

    public String scriptFilename(String profile, String name, String ext){
        return "scripts/"+profile+"/"+name+"."+ext;
    }
}
