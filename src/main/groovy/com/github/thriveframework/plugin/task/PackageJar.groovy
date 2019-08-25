package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.utils.PackageFiles
import org.gradle.jvm.tasks.Jar

import javax.inject.Inject

class PackageJar extends Jar {
    @Inject
    PackageJar(String projectName, PackageFiles packageFiles){
        from(packageFiles.packageClasses, packageFiles.packageResources)
        destinationDirectory.set packageFiles.packageLibs
        include "**/*.class"
        include "META-INF/services/*"
        archiveBaseName.set "${projectName}-package"
    }
}
