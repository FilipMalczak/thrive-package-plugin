package com.github.thriveframework.plugin.impl;

import lombok.SneakyThrows;

import java.io.*;
import java.util.Properties;

import static java.util.stream.Collectors.joining;

public class DirectoryPackage extends YamlBasedPackage {
    public DirectoryPackage(File packageDir) {
        super(retrieveName(packageDir), retrieveYaml(packageDir));
    }

    @SneakyThrows
    private static String retrieveName(File packageDir){
        Properties props = new Properties();
        try (InputStream s = new FileInputStream(new File(packageDir, "pkg.properties"))){
            props.load(s);
        }
        String name = props.getProperty("name");
        String group = props.getProperty("group");
        return group+":"+name;
    }

    @SneakyThrows
    private static String retrieveYaml(File packageDir){
        try (
            InputStream s = new FileInputStream(new File(packageDir, "pkg.yaml"));
            InputStreamReader isr = new InputStreamReader(s);
            BufferedReader reader = new BufferedReader(isr)
        ){
            return reader.lines().collect(joining(System.lineSeparator()));
        }
    }
}
