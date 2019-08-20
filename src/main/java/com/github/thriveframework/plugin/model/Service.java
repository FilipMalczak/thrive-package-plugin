package com.github.thriveframework.plugin.model;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class Service {
    String name;
    String image;
    Map<String, String> environment;
    List<Port> ports;
    List<String> startupDependencies;
    List<String> runtimeDependencies;

}
