package com.github.thriveframework.plugin.model.compose;

import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceDef {
    String image;
    Map<String, String> environment;
    @Singular
    List<String> ports;
    List<String> depends_on;
    List<String> links;
    //todo add command
}
