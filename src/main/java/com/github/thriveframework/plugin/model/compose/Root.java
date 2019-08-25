package com.github.thriveframework.plugin.model.compose;

import lombok.*;

import java.util.Map;

@Builder
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Root {
    String version;
    @Singular
    Map<String, ServiceDef> services;
}
