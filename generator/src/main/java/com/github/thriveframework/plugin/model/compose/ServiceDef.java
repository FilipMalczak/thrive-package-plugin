package com.github.thriveframework.plugin.model.compose;

import com.github.thriveframework.plugin.model.ImageDefinition;
import lombok.*;

import java.util.List;
import java.util.Map;

@Builder
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceDef {
    ImageDefinition imageDefinition;
    Map<String, String> environment;
    @Singular
    List<String> ports;
    List<String> depends_on;
    List<String> links;
    String command;
    //todo add command
}
