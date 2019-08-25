package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.AbstractPluginPackage
import com.github.thriveframework.plugin.model.Composition
import groovy.util.logging.Slf4j
import org.gradle.api.Task
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.specs.Spec
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject

@Slf4j
class WritePackage extends Echo {
    //todo assert that it's a valid package name
    final Property<String> packageName
    final Property<String> packageGroup
    final Property<Composition> composition
    final DirectoryProperty targetDir

    @Inject
    WritePackage(ObjectFactory objects, ProviderFactory providers) {
        super(objects)
        packageName = objects.property(String)
        packageGroup = objects.property(String)
        composition = objects.property(Composition)
        targetDir = objects.directoryProperty()
        super.content.set(prepareSource())
        super.target.set(providers.provider({
            log.info("WRITE PACKAGE PROVIDER")
            log.info("TARGET DIR ${targetDir.get().asFile.absolutePath}")
            def result = targetDir.get().file(
                "./" +
                    (
                        packageGroup.get().replace(".", "/") +
                            "/" +
                            packageName.get() +
                            ".java"
                    )
            )
            log.info("Result $result.asFile.absolutePath")
            result
        }))
    }

    private Provider<String> prepareSource(){
        composition.map({ Composition c ->
            def yaml = new Yaml().dump(c)
            def escaped = yaml.replace("\"", "\\\"").replace("\n", "\\n")
            """
package ${packageGroup.get()};

import ${AbstractPluginPackage.canonicalName};

public class ${packageName.get()} extends ${AbstractPluginPackage.simpleName} {
    public ${packageName.get()}(){
        super("${packageGroup.get()}:${packageName.get()}", "${escaped}");
    }
} 
"""
        })
    }
}
