package com.github.thriveframework.plugin.packages;

import com.github.thriveframework.plugin.ThrivePackage;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Service;
import com.google.auto.service.AutoService;

import static java.util.Arrays.asList;

@AutoService(ThrivePackage.class)
public class Kafka implements ThrivePackage {
    @Override
    public String getName() {
        return "kafka";
    }

    @Override
    public Composition getComposition() {
        return new Composition(asList(
            Service.builder()
                .name("kafka")
                .image("confluentinc/cp-kafka") //todo add version             
                .env("KAFKA_ZOOKEEPER_CONNECT", "zookeeper:2181")
                .env("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                .env("KAFKA_ADVERTISED_LISTENERS", "INTERNAL://kafka:9092")
                .env("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "INTERNAL:PLAINTEXT")
                .env("KAFKA_INTER_BROKER_LISTENER_NAME", "INTERNAL")
                .env("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                //todo or maybe runtime?
                .startupDependency("zookeeper")
            .build()
        ));
    }
}
