package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.Composition
import org.gradle.api.Project
import org.gradle.api.provider.MapProperty

import javax.inject.Inject


class ServiceLayoutExtension {
    final FacetExtension core
    final Map<String, FacetExtension> facets
    //todo should be elsewhere in the API
    final MapProperty<String, List<String>> profiles

    @Inject
    ServiceLayoutExtension(Project project){
        core = this.extensions.create("core", FacetExtension)
        facets = [:]
        profiles = project.objects.mapProperty(String, List)
        this.extensions.add("profiles", profiles)
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
        def builder = Composition.builder()
        //todo add service merging with configurable strategies
        builder.services(core.allServices().collect { it.asService() }.findAll { !it.empty() }.unique())
        facets.each { n, f ->
            builder.facet(
                n,
                f.allServices().collect { it.asService() }.findAll { !it.empty() }.unique()
            )
        }
        builder.build()
    }

    void facet(String name, Closure config={}){
        if (!this.extensions.findByName(name)) {
            FacetExtension f = this.extensions.create(name, FacetExtension)
            facets[name] = f
            f.mainService.name.set(core.mainService.name)
        }
        this."$name"(config)
    }
}
