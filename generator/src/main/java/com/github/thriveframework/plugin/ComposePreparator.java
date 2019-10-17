package com.github.thriveframework.plugin;

import com.github.thriveframework.plugin.facets.FacetSpec;
import com.github.thriveframework.plugin.model.Port;
import com.github.thriveframework.plugin.model.Service;
import com.github.thriveframework.plugin.model.compose.Root;
import com.github.thriveframework.plugin.model.compose.ServiceDef;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.*;

import static com.github.thriveframework.plugin.YmlDumper.toYaml;
import static com.github.thriveframework.plugin.facets.FacetSpec.facet;
import static com.github.thriveframework.plugin.facets.FacetSpec.mainFacet;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ComposePreparator implements Runnable {
    AppSetup setup;

    public void run(){
        System.out.println("Generating main docker-compose.yml");
        setup.save(setup.composeFilename(""), generateCompose(mainFacet()));
        for (String f: setup.getAllFacets()) {
            System.out.println("Generating docker-compose-"+f+".yml for facet "+f);
            setup.save(setup.composeFilename(f), generateCompose(facet(f)));
        }
    }

    private String generateCompose(FacetSpec facetSpec){
        Root.RootBuilder root = Root.builder()
            .version("3.5");
        //fixme I dont like forEach, and streams are pretty awkward here :/
        setup.getServices(facetSpec).forEach(s -> buildService(root, s));
        return toYaml(root.build());
    }

    private void buildService(Root.RootBuilder builder, Service service){
        ServiceDef.ServiceDefBuilder serviceBuilder = ServiceDef.builder()
            .imageDefinition(service.getDefinition())
            .environment(service.getEnvironment());
        for (Port p: service.getPorts())
            //fixme theres a quirk here, something with localhost or smth, that can break firewall config
            serviceBuilder.port(p.getExternal()+":"+p.getInternal());
        Set<String> allDeps = new HashSet<>();
        allDeps.addAll(service.getRuntimeDependencies());
        allDeps.addAll(service.getStartupDependencies());
        serviceBuilder.depends_on(new ArrayList<>(allDeps));
        serviceBuilder.links(new ArrayList<>(allDeps));
        ServiceDef s = serviceBuilder.build();
        builder.service(service.getName(), s);
    }


}
