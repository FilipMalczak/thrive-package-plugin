package com.github.thriveframework.plugin.extension;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

@ToString
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class ThrivePackageSpec {
    final Property<String> packageName;
    final Property<String> packageGroup;
    final DirectoryProperty targetDir;

    @Inject
    public ThrivePackageSpec(ObjectFactory objects){
        packageName = objects.property(String.class);
        packageGroup = objects.property(String.class);
        targetDir = objects.directoryProperty();
    }

    public Provider<Directory> getMainDir(){
        return targetDir.map(d -> d.dir("thrivepackage"));
    }

    public Provider<Directory> getPackageDir(){
        return getMainDir().map(d -> d.dir(packageGroup.get() + "." + packageName.get()));
    }

    public Provider<RegularFile> getPropertiesFile(){
        return getPackageDir().map(d -> d.file("pkg.properties"));
    }

    public Provider<RegularFile> getYamlFile(){
        return getPackageDir().map(d -> d.file("pkg.yaml"));
    }
}
