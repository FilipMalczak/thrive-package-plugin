package com.github.thriveframework.plugin

import groovy.util.logging.Slf4j
import org.gradle.api.Plugin
import org.gradle.api.Project

@Slf4j
class ThrivePackagePlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        log.info("Project has just been bootstrapped, hold on to your shoes, more's to come")
    }
}
