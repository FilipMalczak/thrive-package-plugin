package com.github.thriveframework.plugin.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Value;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Composition {
    List<Service> services = new LinkedList<>();
    //todo introduce facets
//    Map<String, List<ServiceExtension>> facets
}
