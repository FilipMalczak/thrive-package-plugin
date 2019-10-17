package com.github.thriveframework.plugin.facets;

import com.github.thriveframework.plugin.GeneratorApp;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.Collections.emptyList;

@FunctionalInterface
public interface FacetSpec extends Function<Composition, List<Service>> {
    /**
     * Does something similiar as docker-compose does when loading more than one file.
     */
    default FacetSpec mergedWith(FacetSpec other){
        return c -> {
            Map<String, Service> services = new HashMap<>();
            for (Service service: this.apply(c)){
                services.put(service.getName(), service);
            }
            for (Service service: other.apply(c)){
                if (services.containsKey(service.getName()))
                    services.put(service.getName(), services.get(service.getName()).overwrittenBy(service));
                else {
                    services.put(service.getName(), service);
                }
            }
            return new ArrayList<>(services.values());
        };
    }

    static FacetSpec mainFacet(){
        return c -> c.getServices();
    }

    static FacetSpec facet(String name){
        return c -> c.getFacets().getOrDefault(name, emptyList());
    }
}
