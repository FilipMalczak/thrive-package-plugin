package com.github.thriveframework.plugin.extension

import com.github.thriveframework.plugin.model.Composition
import groovy.transform.ToString
import groovy.util.logging.Slf4j
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.provider.Property

import com.github.thriveframework.plugin.model.Service
import org.gradle.api.provider.Provider

import static com.github.thriveframework.utils.plugin.Projects.fullName

@Slf4j
class ThrivePackageExtension {
    final Property<String> group
    final Property<String> name
    final ServiceLayoutExtension layout

    ThrivePackageExtension(Project project){
        name = project.objects.property(String)
        group = project.objects.property(String)
        layout = this.extensions.create("layout", ServiceLayoutExtension, project)
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
        return new Composition(new ArrayList<Service>(services), new HashMap<String, List<Service>>())
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


    @Override
    public String toString() {
        return "ThrivePackageExtension{" +
            "group=" + group.getOrNull() +
            ", name=" + name.getOrNull() +
            ", layout=" + layout.getOrNull() +
            '}';
    }
}