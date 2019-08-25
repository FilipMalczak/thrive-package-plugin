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
import org.gradle.api.component.AdhocComponentWithVariants
import org.gradle.api.component.SoftwareComponentFactory
import org.gradle.api.plugins.internal.JavaConfigurationVariantMapping
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.util.GradleVersion

import javax.inject.Inject

import static com.github.thriveframework.plugin.utils.Projects.fullName

@Slf4j
class ThrivePackagePlugin implements Plugin<Project> {
    private PackageFiles packageFiles
    private ThrivePackageExtension extension
    private final static String taskGroup = "thrive (package)"
    private SoftwareComponentFactory componentFactory

    @Inject
    ThrivePackagePlugin(SoftwareComponentFactory componentFactory) {
        this.componentFactory = componentFactory
    }

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
        configurePublishing(target)
        logAvailablePackages()
    }

    private void verifyGradleVersion() {
        if (GradleVersion.current().compareTo(GradleVersion.version("5.5")) < 0) {
            throw new GradleException("Thrive plugin requires Gradle 5.5 or later. The current version is "
                + GradleVersion.current());
        }
    }

    private void applyPluginIfNeeded(Project project, plugin){
        String nameToLog;
        if (plugin instanceof Class)
            nameToLog = plugin.canonicalName
        else if (plugin instanceof String)
            nameToLog = plugin
        else
            log.warn("$plugin is neither a class nor String, but rather ${plugin.class}; prepare for possible trouble")
        log.info("Trying to apply plugin with implementation $nameToLog to project ${fullName(project)}")
        if (!project.plugins.findPlugin(plugin)) {
            log.info("Applying $nameToLog")
            project.apply plugin: plugin
        } else {
            log.info("$nameToLog already applied")
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

        AdhocComponentWithVariants component = componentFactory.adhoc("thrive")
        component.addVariantsFromConfiguration(project.configurations.thrivePackage, new JavaConfigurationVariantMapping("compile", false))
        project.components.add(component)
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
            constructorArgs: [project.name, packageFiles]
        )
    }

    private void bindTasks(Project project){
        project.compilePackage.dependsOn project.writePackage
        project.packageJar.dependsOn project.compilePackage
        project.packageJar.dependsOn project.writePackageServiceProviderDescriptor

        project.tasks.findByName("build")?.dependsOn project.packageJar

        project.tasks.findByName("clean")?.doLast {
            packageFiles.root.deleteDir()
        }
    }

    private void configurePublishing(Project project){
        applyPluginIfNeeded(project, "maven-publish")
        project.publishing.publications.create("thrivePackage", MavenPublication){
            artifactId = "${project.name}-package"
            from project.components.thrive
            artifact project.packageJar
        }
        project.publishThrivePackagePublicationToMavenLocal.dependsOn project.packageJar
    }

    private void logAvailablePackages(){
        ServiceLoader<Package> packages = ServiceLoader.load(Package)
        packages.each {
            println "${it.name}, ${it.class}"
        }
    }
}
