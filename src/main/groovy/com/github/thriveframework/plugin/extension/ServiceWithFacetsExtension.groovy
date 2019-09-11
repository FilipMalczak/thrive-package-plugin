package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.ImageDefinition
import com.github.thriveframework.plugin.model.Port
import com.github.thriveframework.plugin.model.Service
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

import javax.inject.Inject

class ServiceWithFacetsExtension extends ServiceExtension {
//    final Property<String> name
//    final Property<ImageDefinition> definition
//    final MapProperty<String, String> environment
//    final SetProperty<Port> ports
//    final SetProperty<String> startupDependencies
//    final SetProperty<String> runtimeDependencies
    final Property<String> command
    final NamedDomainObjectContainer<ServiceExtension> facets;

    @Inject
    ServiceWithFacetsExtension(ObjectFactory objects){
//        name = objects.property(String)
//        definition = objects.property(ImageDefinition)
//        environment = objects.mapProperty(String, String)
//        ports = objects.setProperty(Port)
//        startupDependencies = objects.setProperty(String)
//        runtimeDependencies = objects.setProperty(String)
//        command = objects.property(String)
        super(objects)
        facets = objects.domainObjectContainer(ServiceExtension)
        this.extensions.add("facets", facets)
    }

    ServiceWithFacetsExtension(String name, ObjectFactory objects){
        this(objects)
        if (name)
            this.name.set(name)
    }

//    Service asService(){
//        return new Service(
//            name.getOrElse(null),
//            definition.getOrElse(null),
//            environment.getOrElse(emptyMap()),
//            ports.getOrElse(emptySet()),
//            startupDependencies.getOrElse(emptySet()),
//            runtimeDependencies.getOrElse(emptySet()),
//            command.getOrElse(null)
//        )
//    }
//
//    Service asService(String name){
//        def out = asService()
//        out.name = name
//        out
//    }
}
