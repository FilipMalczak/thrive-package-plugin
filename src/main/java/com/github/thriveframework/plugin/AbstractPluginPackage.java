package com.github.thriveframework.plugin;

import com.github.thriveframework.plugin.model.Composition;
import lombok.Data;
import org.yaml.snakeyaml.Yaml;

@Data
public class AbstractPluginPackage implements Package {
    private String name;
    private Composition composition;

    protected AbstractPluginPackage(String name, String yaml) {
        this.name = name;
        Yaml parser = new Yaml();
        composition = parser.load(yaml);
    }
}
