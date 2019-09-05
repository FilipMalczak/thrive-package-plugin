package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.extension.ThrivePackageSpec
import com.github.thriveframework.plugin.model.Composition
import com.github.thriveframework.utils.plugin.task.Echo
import groovy.util.logging.Slf4j
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.yaml.snakeyaml.Yaml

import javax.inject.Inject

//todo caching - annotation on class, @input, @output
@Slf4j
class WritePackageYaml extends Echo {
    final ThrivePackageSpec pkg
    final Property<Composition> composition

    @Inject
    WritePackageYaml(ObjectFactory objects, ProviderFactory providers) {
        super(objects)
        pkg = this.extensions.create("pkg", ThrivePackageSpec, objects)
        composition = objects.property(Composition)
        super.content.set(prepareSource())
        super.target.set pkg.yamlFile
    }

    private Provider<String> prepareSource(){
        composition.map({ Composition c ->
            new Yaml().dump(c)
        })
    }
}
