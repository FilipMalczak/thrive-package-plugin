package com.github.thriveframework.plugin.impl;

import com.github.thriveframework.plugin.ThrivePackage;
import com.github.thriveframework.plugin.model.Composition;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data
public class YamlBasedPackage implements ThrivePackage {
    private String name;
    private Composition composition;

    protected YamlBasedPackage(String name, String yaml) {
        this.name = name;
        Yaml parser = new Yaml();
        composition = parser.load(yaml);
    }
}
