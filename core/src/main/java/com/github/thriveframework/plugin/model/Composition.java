package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.*;

import static java.util.Collections.emptyMap;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Composition {
    @Singular
    @NonNull List<Service> services;
    @Singular
    @NonNull Map<String, List<Service>> facets;
}
