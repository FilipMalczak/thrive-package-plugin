package com.github.thriveframework.plugin.packages;

import com.github.thriveframework.plugin.ThrivePackage;
import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Port;
import com.github.thriveframework.plugin.model.Service;
import com.google.auto.service.AutoService;

import static com.github.thriveframework.plugin.model.ImageDefinition.image;
import static java.util.Arrays.asList;

@AutoService(ThrivePackage.class)
public class Kafka implements ThrivePackage {
    @Override
    public String getName() {
        return "kafka";
    }

    @Override
    public Composition getComposition() {
        return Composition.builder()
            .service(
                Service.builder()
                    .name("kafka")
                    .definition(image("confluentinc/cp-kafka")) //todo add version
                    .env("KAFKA_ZOOKEEPER_CONNECT", "zookeeper:2181")
                    .env("KAFKA_AUTO_CREATE_TOPICS_ENABLE", "true")
                    .env("KAFKA_ADVERTISED_LISTENERS", "INTERNAL://kafka:9092")
                    .env("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "INTERNAL:PLAINTEXT")
                    .env("KAFKA_INTER_BROKER_LISTENER_NAME", "INTERNAL")
                    .env("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1")
                    .expose(9092)
                    //todo or maybe runtime?
                    .startupDependency("zookeeper")
                    .build()
            )
            .facet(
                "local",
                asList(
                    Service.builder()
                        .name("kafka")
                        .env("KAFKA_ADVERTISED_LISTENERS", "INTERNAL://kafka:9092,EXTERNAL://localhost:29092")
                        .env("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "INTERNAL:PLAINTEXT,EXTERNAL:PLAINTEXT")
                        .port(Port.just(29092))
                        .expose(29092)
                        .build()
                )
            )
            .build();
    }
}
