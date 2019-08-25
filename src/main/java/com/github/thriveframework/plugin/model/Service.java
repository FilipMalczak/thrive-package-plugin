package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service {
    String name;
    String image;
    @Singular("env")
    Map<String, String> environment;
    @Singular
    Set<Port> ports;
    @Singular("startupDependency")
    Set<String> startupDependencies;
    @Singular("runtimeDependency")
    Set<String> runtimeDependencies;

    //required so that Gradle can create named domain objects (see ThrivePackageExtension)
    Service(String name){
        this(name,
            null,
            new HashMap<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>()
        );
    }

    //todo String-based overloads
    void port(int external, int internal){
        ports.add(Port.between(external, internal));
    }

    void port(int p){
        ports.add(Port.exposed(p));
    }

    //todo fluent, type-agnostic ports(Object... p)

    //todo maybe something shorter? startup=require, runtime=use?
    void startupDependency(String dep){
        startupDependencies.add(dep);
    }

    void runtimeDependency(String dep){
        runtimeDependencies.add(dep);
    }

    void env(Map<String, String> val){
        environment.putAll(val);
    }
}
