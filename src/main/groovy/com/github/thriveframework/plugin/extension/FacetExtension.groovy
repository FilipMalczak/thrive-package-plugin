package com.github.thriveframework.plugin.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class FacetExtension {
    final Property<String> name
    final ServiceExtension main;
    final NamedDomainObjectContainer<ServiceExtension> services;

    @Inject
    FacetExtension(String nameVal=null, ObjectFactory objects){
        name = objects.property(String)
        main = this.extensions.create("main", ServiceExtension)
        services = objects.domainObjectContainer(ServiceExtension)
        this.extensions.add("services", services)
        if (nameVal)
            this.name.set(nameVal)
    }


    List<ServiceExtension> allServices(){
        return [main] + (services as List)
    }
}
