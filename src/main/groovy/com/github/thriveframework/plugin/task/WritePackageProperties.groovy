package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.extension.ThrivePackageSpec
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.WriteProperties

import javax.inject.Inject

//todo caching
class WritePackageProperties extends WriteProperties {
    final ThrivePackageSpec pkg

    @Inject
    WritePackageProperties(ObjectFactory objects){
        pkg = this.extensions.create("pkg", ThrivePackageSpec, objects)
        this.outputFile = pkg.getPropertiesFile()
    }

    @TaskAction
    void run(){
        Properties props = new Properties()
        props["name"] = pkg.packageName.get()
        props["group"] = pkg.packageGroup.get()
        this.properties = props
        writeProperties() //fixme is this called twice?
    }
}
