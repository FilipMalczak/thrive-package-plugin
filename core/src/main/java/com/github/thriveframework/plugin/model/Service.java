package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
    String name;
    ImageDefinition definition;
    @Singular("env")
    Map<String, String> environment = new HashMap<>();
    @Singular
    Set<Port> ports = new HashSet<>();
    @Singular("startupDependency")
    Set<String> startupDependencies = new HashSet<>();
    @Singular("runtimeDependency")
    Set<String> runtimeDependencies = new HashSet<>();
    String command;

    //required so that Gradle can create named domain objects (see ThrivePackageExtension)
    public Service(String name){
        this(name,
            null,
            new HashMap<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            null
        );
    }

    public Service(Service example){
        this(
            example.name,
            example.definition != null ?
                new ImageDefinition(example.definition.getComposeKey(), example.definition.getImageSpec()) :
                null,
            new HashMap<>(example.environment),
            example.ports.stream().map( p -> Port.between(p.getExternal(), p.getInternal()) ).collect(toSet()),
            new HashSet<>(example.startupDependencies),
            new HashSet<>(example.runtimeDependencies),
            example.command
        );
    }

    public Service(String name, Service example){
        this(example);
        this.name = name;
    }

    public void setImage(String image){
        this.definition = ImageDefinition.image(image);
    }

    public void setBuild(String path){
        this.definition = ImageDefinition.build(path);
    }

    //todo String-based overloads
    public void port(int external, int internal){
        ports.add(Port.between(external, internal));
    }

    public void port(int p){
        ports.add(Port.exposed(p));
    }

    //todo fluent, type-agnostic ports(Object... p)

    //todo maybe something shorter? startup=require, runtime=use?
    public void startupDependency(String dep){
        startupDependencies.add(dep);
    }

    public void runtimeDependency(String dep){
        runtimeDependencies.add(dep);
    }

    public void env(Map<String, String> val){
        environment.putAll(val);
    }
}
