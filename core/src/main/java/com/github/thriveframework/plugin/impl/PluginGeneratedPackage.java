package com.github.thriveframework.plugin.impl;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.util.stream.Collectors.joining;

public class PluginGeneratedPackage extends YamlBasedPackage {
    protected PluginGeneratedPackage(Class thisClass) {
        super(retrieveName(thisClass), retrieveYaml(thisClass));
    }

    @SneakyThrows
    private static String retrieveName(Class clazz){
        Properties props = new Properties();
        try (InputStream s = clazz.getClassLoader().getResourceAsStream("thrivepackage/"+clazz.getCanonicalName()+"/pkg.properties")){
            props.load(s);
        }
        String name = props.getProperty("name");
        String group = props.getProperty("group");
        return group+":"+name;
    }

    @SneakyThrows
    private static String retrieveYaml(Class clazz){
        try (
            InputStream s = clazz.getClassLoader().getResourceAsStream("thrivepackage/"+clazz.getCanonicalName()+"/pkg.yaml");
            InputStreamReader isr = new InputStreamReader(s);
            BufferedReader reader = new BufferedReader(isr)
        ){
            return reader.lines().collect(joining(System.lineSeparator()));
        }
    }
}
