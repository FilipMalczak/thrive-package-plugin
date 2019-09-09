package com.github.thriveframework.plugin.packages;

import com.github.thriveframework.plugin.ThrivePackage;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Service;
import com.google.auto.service.AutoService;

import static com.github.thriveframework.plugin.model.ImageDefinition.image;
import static java.util.Arrays.asList;

@AutoService(ThrivePackage.class)
public class Zookeeper implements ThrivePackage {
    @Override
    public String getName() {
        return "zookeeper";
    }

    @Override
    public Composition getComposition() {
        return Composition.builder()
            .service(
                Service.builder()
                    .name("zookeeper")
                    .definition(image("confluentinc/cp-zookeeper")) //todo add version
                    //todo far future - some abstraction that will allow other services
                    //use values as such in their own config
                    .env("ZOOKEEPER_CLIENT_PORT", "2181")
                    .env("ZOOKEEPER_TICK_TIME", "2000")
                    .build()
            )
            .build();
    }
}