package com.github.thriveframework.plugin.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.*;
import java.util.stream.Stream;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    String name;
    String image;
    Map<String, String> environment = new HashMap<>();
    Set<Port> ports = new HashSet<>();
    Set<String> startupDependencies = new HashSet<>();
    Set<String> runtimeDependencies = new HashSet<>();

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

    //todo fluent API
}
