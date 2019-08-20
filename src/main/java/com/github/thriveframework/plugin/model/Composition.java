package com.github.thriveframework.plugin.model;

import lombok.Value;

import java.util.List;

import static java.util.Arrays.asList;

@Value(staticConstructor = "of")
public class Composition {
    List<Service> services;
    //todo introduce facets
//    Map<String, List<Service>> facets

    public static Composition of(Service... services){
        return of(asList(services));
    }
}
