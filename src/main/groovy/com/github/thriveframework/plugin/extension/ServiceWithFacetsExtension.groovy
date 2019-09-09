package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.Service
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class ServiceWithFacetsExtension extends Service {
    final NamedDomainObjectContainer<Service> facets;

    @Inject
    ServiceWithFacetsExtension(ObjectFactory objects){
        super()
        facets = objects.domainObjectContainer(Service)
        this.extensions.add("facets", facets)
    }

    ServiceWithFacetsExtension(String name, ObjectFactory objects){
        super(name)
        facets = objects.domainObjectContainer(Service)
        this.extensions.add("facets", facets)
    }
}
