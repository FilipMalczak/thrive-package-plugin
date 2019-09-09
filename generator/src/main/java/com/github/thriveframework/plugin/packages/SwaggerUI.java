package com.github.thriveframework.plugin.packages;

import com.github.thriveframework.plugin.model.Composition;
import com.github.thriveframework.plugin.model.Service;
import com.github.thriveframework.plugin.ThrivePackage;
import com.google.auto.service.AutoService;

import static com.github.thriveframework.plugin.model.ImageDefinition.image;
import static java.util.Arrays.asList;

@AutoService(ThrivePackage.class)
public class SwaggerUI implements ThrivePackage {
    @Override
    public String getName() {
        return "swaggerUi";
    }

    @Override
    public Composition getComposition() {
        return Composition.builder()
            .service(
                Service.builder()
                    .name("swaggerui")
                    .definition(image("swaggerapi/swagger-ui:v3.22.0"))
                    //todo that's redundant, there's a default for that in the impl
                    .env("API_URL", "/docs/v1/swagger")
                    .build()
            )
            .build();
    }
}
