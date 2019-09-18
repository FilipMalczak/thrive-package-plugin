package com.github.thriveframework.plugin.extension

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class FacetExtension {
    final Property<String> name
    final Property<Boolean> hasMainService
    final ServiceExtension mainService;
    final NamedDomainObjectContainer<ServiceExtension> services;

    @Inject
    FacetExtension(String nameVal=null, ObjectFactory objects){
        name = objects.property(String)
        hasMainService = objects.property(Boolean)
        mainService = this.extensions.create("mainService", ServiceExtension)
        services = objects.domainObjectContainer(ServiceExtension)
        this.extensions.add("services", services)
        hasMainService.convention true
        if (nameVal)
            this.name.set(nameVal)
    }


    List<ServiceExtension> allServices(){
        // this implementation makes it possible to configure main service via name in services closure
        // seems like a beature (a bug and a feature at once)
        return ( hasMainService.get() ? [mainService] : [] )+ (services as List)
    }
}
