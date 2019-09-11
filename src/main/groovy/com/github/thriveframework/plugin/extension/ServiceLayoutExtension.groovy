package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.Composition
import com.github.thriveframework.plugin.model.Service
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty

import javax.inject.Inject

class ServiceLayoutExtension {
    final ServiceWithFacetsExtension main
    final NamedDomainObjectContainer<ServiceWithFacetsExtension> services
    final MapProperty<String, List<String>> profiles

    @Inject
    ServiceLayoutExtension(Project project){
        main = this.extensions.create("main", ServiceWithFacetsExtension)
        services = project.objects.domainObjectContainer(ServiceWithFacetsExtension, {n -> new ServiceWithFacetsExtension(n, project.objects)})
        this.extensions.add("services", services)
        profiles = project.objects.mapProperty(String, List)
        initDefaults(project)
    }

    private void initDefaults(Project project){
        profiles.putAll( //todo convention? better utility methods?
            core: [],
            local: ["local", "ui"]
        )
        //todo ?
    }

    Composition toComposition(){
        Map<String, Map<String, Service>> facetToService = [:].withDefault {[:]}
        facetToService["_"][main.name] = main.asService()
        for (Service service: services)
            facetToService["_"][service.name] = service.asService()
        for (Service mainFacet: main.facets){
            facetToService[mainFacet.name][main.name] = mainFacet.asService(main.name)
        }
        for (ServiceWithFacetsExtension service: services)
            for (Service facet: service.facets)
                facetToService[facet.name][service.name] = facet.asService(service.name)
        def builder = Composition.builder()
        for (String facet: facetToService.keySet()) {
            Set<Service> facetContent = facetToService[facet].values()
            if (facet == "_")
                builder.services(facetContent)
            else
                builder.facet(facet, facetContent as List)
        }
        builder.build()
    }
}
