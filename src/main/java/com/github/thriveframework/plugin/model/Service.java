package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    @NonNull String name;
    String image;
    @NonNull Map<String, String> environment = new HashMap<>();
    @NonNull Set<Port> ports = new HashSet<>();
    @NonNull Set<String> startupDependencies = new HashSet<>();
    @NonNull Set<String> runtimeDependencies = new HashSet<>();

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
