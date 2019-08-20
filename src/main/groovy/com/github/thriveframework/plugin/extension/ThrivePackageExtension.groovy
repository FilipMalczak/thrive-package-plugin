package com.github.thriveframework.plugin.extension

import groovy.util.logging.Slf4j
import org.gradle.api.Project
import org.gradle.api.provider.Property

@Slf4j
class ThrivePackageExtension {
    final Property<String> group
    final Property<String> name

    ThrivePackageExtension(Project project){
        name = project.objects.property(String)
        group = project.objects.property(String)
        initDefaults(project)
    }

    private void initDefaults(Project project){
        setName normalizeName(project.name)
        setGroup normalizeGroup("${project.group}")
    }

    void setName(String name){
        this.name.set normalizeName(name)
    }

    void setGroup(String group){
        this.group.set normalizeGroup(group)
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