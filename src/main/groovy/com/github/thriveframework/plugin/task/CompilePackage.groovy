package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.utils.PackageFiles
import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile

import javax.inject.Inject

class CompilePackage extends JavaCompile {
    //dirty hack for Groovy resolver, see plugin impl, adding this task
    final Property<PackageFiles> packageFilesHelper

    //fixme ugly mixture of props and args; normalize
    @Inject
    CompilePackage(ObjectFactory objects, WritePackage task, FileCollection classpath) {
        super()
        packageFilesHelper = objects.property(PackageFiles)
        super.sourceCompatibility = "1.8"
        super.targetCompatibility = "1.8"

        source(task)
        this.classpath = classpath
        include "**/*.java"
        setDestinationDir(packageFilesHelper.map({ pf -> pf.packageClasses}))
    }
}
