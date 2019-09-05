package com.github.thriveframework.plugin.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.LinkedList;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class Composition {
    List<Service> services = new LinkedList<>();
    //todo introduce facets
//    Map<String, List<ServiceExtension>> facets
}
