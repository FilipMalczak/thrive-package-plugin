package com.github.thriveframework.plugin.task


import groovy.util.logging.Slf4j
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

//todo caching
@Slf4j
class WriteDockerCompose extends DefaultTask {

    final static String MAIN_NAME = "com.github.thriveframework.plugin.GeneratorApp"

    final Property<String> execConfigName
    final Property<String> packageConfigName
    final ListProperty<String> packageDirs
    final MapProperty<String, List<String>> profiles
    //todo support for package dirs
    final DirectoryProperty targetDir

    @Inject
    WriteDockerCompose(Project project) {
        execConfigName = project.objects.property(String)
        packageConfigName = project.objects.property(String)
        packageDirs = project.objects.listProperty(String)
        profiles = project.objects.mapProperty(String, List)
        targetDir = project.objects.directoryProperty()
    }

    @TaskAction
    void run(){
        def javaExe = "${System.getProperty("java.home")}/bin/java"
        def classpath = project.configurations.findByName(execConfigName.get()).resolve().collect({
            it.absolutePath
        }).join(":")
        def main = MAIN_NAME
        def pkgDirs = packageDirs.get()
        def profilesSpec = profiles.get().collect { k, v -> [ "-p", "${k}:${v.join("+")}" ]}.flatten()
        def workspace = ["-w", "${targetDir.asFile.get().absolutePath}"]
        def args = (pkgDirs + profilesSpec + workspace).join(" ")
        def command = "${javaExe} -cp ${classpath} $main $args"
        def process = command.execute()
        def t1 = process.consumeProcessOutputStream(System.out)
        def t2 = process.consumeProcessErrorStream(System.err)
        //todo add timeout
        def exitCode = process.waitFor()
        t1.join()
        t2.join()
        assert exitCode == 0, "Generator process has stopped with exit code $exitCode"
    }
}
