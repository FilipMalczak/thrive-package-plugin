package com.github.thriveframework.plugin


import com.github.thriveframework.plugin.extension.ThrivePackageExtension
import com.github.thriveframework.plugin.task.CompilePackage
import com.github.thriveframework.plugin.task.PackageJar
import com.github.thriveframework.plugin.task.WritePackage
import com.github.thriveframework.plugin.task.WritePackageProviderConfiguration
import com.github.thriveframework.plugin.utils.PackageFiles

import groovy.util.logging.Slf4j
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

@Slf4j
class ThrivePackagePlugin implements Plugin<Project> {
    private PackageFiles packageFiles
    private ThrivePackageExtension extension
    private final static String taskGroup = "thrive (package)"

    @Override
    void apply(Project target) {
        //todo another "common" candidate
        verifyGradleVersion()
        prepare(target)
        addWritePackageTask(target)
        addCompilePackageTask(target)
        addWritePackageServiceProviderDescriptorTask(target)
        addPackageJarTask(target)
        bindTasks(target)
        addArtifact(target)
    }

    private void verifyGradleVersion() {
        if (GradleVersion.current().compareTo(GradleVersion.version("5.5")) < 0) {
            throw new GradleException("Thrive plugin requires Gradle 5.5 or later. The current version is "
                + GradleVersion.current());
        }
    }

    private void prepare(Project project){
        packageFiles = new PackageFiles(project)
        packageFiles.packageSrc.mkdirs()
        packageFiles.packageClasses.mkdirs()

        extension = project.extensions.create("thrivePackage", ThrivePackageExtension, project)

        //todo maybe create source set?

        project.configurations {
            thrivePackage
        }

        project.dependencies {
            //fixme jitpack repo may be missing!; besides, it now requires Yaml that is on central
            //require currently used version - how do I do that?
            thrivePackage "com.github.thrive-framework:thrive-package-plugin:0.1.0-SNAPSHOT"
        }
    }

    private void addWritePackageTask(Project project){
        //todo extract createTask(...) method to plugin-common
        project.tasks.create(
            name: "writePackage",
            type: WritePackage,
            group: taskGroup,
            description: ""//todo
        ) {
            packageGroup = extension.group
            packageName = extension.name
            composition = project.provider { extension.composition }
            targetDir = packageFiles.packageSrc
        }
    }

    private void addCompilePackageTask(Project project){
        def pf = packageFiles
        project.tasks.create(
            name: "compilePackage",
            type: CompilePackage,
            group: taskGroup,
            description: "", //todo
            constructorArgs: [project.tasks.writePackage, project.configurations.thrivePackage]
        ) {
            packageFilesHelper = pf

        }
    }

    private void addWritePackageServiceProviderDescriptorTask(Project project){
        project.tasks.create(
            name: "writePackageServiceProviderDescriptor",
            type: WritePackageProviderConfiguration,
            group: taskGroup,
            description: ""//todo
        ) {
            packageGroup = extension.group
            packageName = extension.name
            composition = project.provider { extension.composition }
            targetDir = packageFiles.packageResources
        }
    }

    private void addPackageJarTask(Project project){
        project.tasks.create(
            name: "packageJar",
            type: PackageJar,
            group: taskGroup,
            description: "",//todo
            constructorArgs: [packageFiles]
        )
    }

    private void bindTasks(Project project){
        project.compilePackage.dependsOn project.writePackage
        project.packageJar.dependsOn project.compilePackage
        project.packageJar.dependsOn project.writePackageServiceProviderDescriptor

        project.build.dependsOn project.packageJar

        project.clean.doLast {
            packageFiles.root.deleteDir()
        }
    }

    private void addArtifact(Project project){
        //todo
//        project.artfacts {
//
//        }
    }
}
