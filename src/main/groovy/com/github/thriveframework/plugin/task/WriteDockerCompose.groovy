package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.Package
import com.github.thriveframework.plugin.model.Port
import com.github.thriveframework.plugin.model.Service
import com.github.thriveframework.plugin.model.compose.Root
import com.github.thriveframework.plugin.model.compose.ServiceDef
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.CacheableTask
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject

//todo caching
class WriteDockerCompose extends Echo {
    final ListProperty<Package> packages
    final ListProperty<Service> declaredServices
    final DirectoryProperty targetDir

    @Inject
    WriteDockerCompose(Project project) {
        super(project.objects)
        packages = project.objects.listProperty(Package)
        declaredServices = project.objects.listProperty(Service)
        targetDir = project.objects.directoryProperty()
        super.target.set(project.provider { targetDir.get().file("./docker-compose.yml") })
        super.content.set prepareComposeContent(project.providers)
    }

    private Provider<String> prepareComposeContent(ProviderFactory providers){
        providers.provider {
            def ps = this.packages.get()
            println "PACKAGES $ps"
            Root.RootBuilder rootBuilder = Root.builder().version("3.5");
            for (Package pkg: ps){
                for (Service service: pkg.composition.services){
                    addService(rootBuilder, service);
                }
            }
            println "SERVICES ${declaredServices.get()}"
            for (Service service: declaredServices.get())
                addService(rootBuilder, service)
            Root root = rootBuilder.build();
            def asMap = castToMap(root)
            new Yaml().dump(asMap)
        }
    }

    //todo weird visibility
    void addService(Root.RootBuilder rootBuilder, Service service){
        ServiceDef.ServiceDefBuilder builder = ServiceDef.builder()
            .image(service.image)
            .environment(service.environment);
        for (Port p: service.ports)
            //fixme theres a quirk here, something with localhost or smth, that can break firewall config
            builder.port("${p.external}:${p.internal}")
        Set<String> allDeps = [] as Set
        allDeps.addAll(service.runtimeDependencies)
        allDeps.addAll(service.startupDependencies)
        builder.depends_on(allDeps as List)
        builder.links(allDeps as List)
        def s = builder.build()
        rootBuilder.service(service.name, s)
    }

    //todo weird visibility
    Map castToMap(Root root){
        def out = [
            version: root.version
        ]
        def services = [:]
        root.services.each { k, v ->
            services[k] = [
                image: v.image,
                environment: v.environment,
                ports: v.ports,
                depends_on: v.depends_on,
                links: v.links
            ]
        }
        out.services = services
        out
    }


}
