package com.github.thriveframework.plugin.utils

import groovy.transform.Canonical
import org.gradle.api.Project

@Canonical
class PackageFiles {
    final File root
    final File packageRoot
    final File packageSrc
    final File packageResources
    final File packageClasses
    final File packageLibs
    final File composeWorkspace
    final File composeYamls
    final File composeScripts

    PackageFiles(Project project) {
        root = new File(project.buildDir, "thrive")
        packageRoot = new File(root, "thrivePackage")
        packageSrc = new File(packageRoot, "sources/main/java")
        packageResources = new File(packageRoot, "sources/main/resources")
        packageClasses = new File(packageRoot, "classes/java/main")
        packageLibs = new File(packageRoot, "libs")

        composeWorkspace = new File(project.projectDir, "run")
        composeYamls = composeWorkspace
        composeScripts = composeWorkspace
    }
}
