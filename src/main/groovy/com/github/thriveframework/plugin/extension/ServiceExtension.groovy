package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.ImageDefinition
import com.github.thriveframework.plugin.model.Port
import com.github.thriveframework.plugin.model.Service
import groovy.transform.ToString
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty

import javax.inject.Inject

class ServiceExtension {
    final Property<String> name
    final Property<ImageDefinition> definition
    final MapProperty<String, String> environment
    final SetProperty<Port> ports
    final SetProperty<String> startupDependencies
    final SetProperty<String> runtimeDependencies
    final Property<String> command

    @Inject
    ServiceExtension(String nameVal=null, ObjectFactory objects){
        name = objects.property(String)
        if (nameVal)
            name.set(nameVal)
        definition = objects.property(ImageDefinition)
        environment = objects.mapProperty(String, String)
        ports = objects.setProperty(Port)
        startupDependencies = objects.setProperty(String)
        runtimeDependencies = objects.setProperty(String)
        command = objects.property(String)
        initDefaults()
    }

    private void initDefaults(){
        //todo default thrive dependencies (kafka, zookeeper)
    }


    void setImage(String image){
        this.definition.set ImageDefinition.image(image)
    }

    void setBuild(String path){
        this.definition.set ImageDefinition.build(path)
    }

    //todo String-based overloads
    void port(int external, int internal){
        ports.add(Port.between(external, internal))
    }

    void port(int p){
        ports.add(Port.exposed(p))
    }

    //todo fluent, type-agnostic ports(Object... p)

    //todo maybe something shorter? startup=require, runtime=use?
    void startupDependency(String dep){
        startupDependencies.add(dep)
    }

    void runtimeDependency(String dep){
        runtimeDependencies.add(dep)
    }

    void env(Map<String, String> val){
        environment.putAll(val)
    }

    Service asService(){
        return new Service(
            name.getOrElse(null),
            definition.getOrElse(null),
            environment.getOrElse([:]),
            ports.getOrElse([] as Set),
            startupDependencies.getOrElse([] as Set),
            runtimeDependencies.getOrElse([] as Set),
            command.getOrElse(null)
        )
    }

    Service asService(String name){
        def out = asService()
        out.name.set name
        out
    }


    @Override
    public String toString() {
        return "ServiceExtension{" +
            "name=" + name.getOrNull() +
            ", definition=" + definition.getOrNull() +
            ", environment=" + environment.getOrNull() +
            ", ports=" + ports.getOrNull() +
            ", startupDependencies=" + startupDependencies.getOrNull() +
            ", runtimeDependencies=" + runtimeDependencies.getOrNull() +
            ", command=" + command.getOrNull() +
            '}';
    }
}
