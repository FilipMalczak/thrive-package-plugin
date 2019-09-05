package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.Composition
import groovy.util.logging.Slf4j
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.Property

import com.github.thriveframework.plugin.model.Service
import org.gradle.api.provider.Provider

@Slf4j
class ThrivePackageExtension {
    final Property<String> group
    final Property<String> name
    final NamedDomainObjectContainer<Service> services
    final DependencyPackages deps

    ThrivePackageExtension(Project project){
        name = project.objects.property(String)
        group = project.objects.property(String)
        services = project.objects.domainObjectContainer(Service)
        //todo refactor all plugins; this is the way to do nested extensions
        this.extensions.add("services", services)
        deps = this.extensions.create("deps", DependencyPackages, project)

        //todo defaults can be set with "convention" https://docs.gradle.org/current/userguide/lazy_configuration.html#sec:applying_conventions
        initDefaults(project)
    }

    private void initDefaults(Project project){
        name.convention(project.provider({ project.name }).map({ n -> normalizeName(n)}))
        group.convention(project.provider({ project.group }).map({ g -> normalizeGroup("$g")}))
    }

    void setName(String name){
        this.name.set normalizeName(name)
    }

    void setGroup(String group){
        this.group.set normalizeGroup(group)
    }

    Composition getComposition(){
        return new Composition(new ArrayList<Service>(services))
    }

    //fixme should be private
    String normalizeGroup(String pkg){
        pkg.split(/[.]/).collect(this.&normalize).join(".").toLowerCase()
    }

    //fixme ditto
    String normalize(String txt){
        txt.replaceAll(/[^\w]/, "")
    }

    String normalizeName(String n){
        n.split("-").collect { it.capitalize() }.join("")
    }
}