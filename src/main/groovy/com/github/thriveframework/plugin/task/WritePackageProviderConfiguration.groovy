package com.github.thriveframework.plugin.task

import com.github.thriveframework.plugin.Package
import Composition
import com.github.thriveframework.plugin.utils.Echo
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property

import javax.inject.Inject

class WritePackageProviderConfiguration extends Echo {
    final Property<String> packageName
    final Property<String> packageGroup
    final Property<Composition> composition
    final DirectoryProperty targetDir

    @Inject
    WritePackageProviderConfiguration(ObjectFactory objects) {
        super(objects)
        packageName = objects.property(String)
        packageGroup = objects.property(String)
        composition = objects.property(Composition)
        targetDir = objects.directoryProperty()
        super.content.set(packageName.map({n -> packageGroup.get()+"."+n}));
        super.target.set(targetDir.map({d -> d.file("META-INF/services/${Package.canonicalName}")}))
    }
}
