package com.github.thriveframework.plugin.packages;

import com.github.thriveframework.plugin.Package;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Service;
import com.google.auto.service.AutoService;

import static java.util.Arrays.asList;

@AutoService(Package.class)
public class Zookeeper implements Package {
    @Override
    public String getName() {
        return "zookeeper";
    }

    @Override
    public Composition getComposition() {
        return new Composition(asList(
            Service.builder()
                .name("zookeeper")
                .image("confluentinc/cp-zookeeper") //todo add version
                //todo far future - some abstraction that will allow other services
                //use values as such in their own config
                .env("ZOOKEEPER_CLIENT_PORT", "2181")
                .env("ZOOKEEPER_TICK_TIME", "2000")
                .build()
        ));
    }
}