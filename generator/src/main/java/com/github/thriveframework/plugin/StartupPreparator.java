package com.github.thriveframework.plugin;

import com.github.thriveframework.plugin.facets.FacetSpec;
import com.github.thriveframework.plugin.model.Service;
import com.github.thriveframework.plugin.model.compose.Root;
import com.github.thriveframework.plugin.model.compose.ServiceDef;
import com.github.thriveframework.plugin.model.graph.Graph;
import com.github.thriveframework.plugin.model.graph.Node;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.thriveframework.plugin.YmlDumper.toYaml;
import static com.github.thriveframework.plugin.model.ImageDefinition.image;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartupPreparator implements Runnable {
    AppSetup options;

    @Override
    public void run() {
        for (String profile: options.getAllProfiles()){
            generateProfile(profile);
        }
    }

    private void generateProfile(String profile){
        System.out.println("Preparing profile "+profile);
        generateExecScript(profile);
        generateStartDependencies(profile);
        generateStartScript(profile);
    }

    private void generateExecScript(String profile){
        options.save(options.scriptFilename(profile, "exec"), prepareExecScript(profile));
    }

    private void generateStartDependencies(String profile){
        options.save(options.scriptFilename(profile, "start-dependencies", "yml"), prepareStartDependencies(profile));
    }

    private void generateStartScript(String profile){
        options.save(options.scriptFilename(profile, "start"), prepareStartScript(profile));
    }

    private String prepareExecScript(String profile){
        StringWriter writer = new StringWriter();
        //todo configurable shebang
        Stream.of(
                "#!/bin/bash",
                "",
                "HERE=\"$(realpath $(dirname \"$0\"))\"",
                "WORKSPACE=$(realpath $HERE/../..)",
                "",
                "FILES_FLAGS=\""+prepareFilesFlags(profile)+"\"",
                //todo project name should be configurable
                "PROJECT_FLAG=\"-p '"+options.getProjectName()+"'\"",
                "",
                "COMPOSE_FLAGS=\"$FILES_FLAGS $PROJECT_FLAG\"",
                "",
                "docker-compose $COMPOSE_FLAGS ${@:1}"
            )
            .map(s -> s+"\n")
            .forEach(writer::write);
        return writer.toString();
    }

    private String prepareFilesFlags(String profile){
        return Stream.concat(
                Stream.of(""),
                options
                    .getProfileToFacets()
                    .get(profile) //todo is it safe to assume that a profile exists?
                    .stream()
            )
            .map(f -> "-f $WORKSPACE/"+options.composeFilename(f))
            .collect(joining(" "));
    }

    private String prepareStartDependencies(String profile){
        Graph graph = new Graph();
        FacetSpec spec = options.forProfileStartup(profile);
        for (Service service: (Iterable<Service>) () -> options.getServices(spec).iterator()){
            graph.node(service.getName()).getExposedPorts().addAll(service.getExposed());
            for (String dep: service.getStartupDependencies())
                graph.connect(service.getName(), dep);
        }
        int waveIdx = 0;
        Map<String, ServiceDef> services = new HashMap<>();
        while (!graph.isEmpty()) {
            List<Node> wave = graph.pop(n -> n.getDegree() == 0);
            String cmd = wave.stream()
                .flatMap(n ->
                    n.getExposedPorts().stream()
                        .map(p ->

                            n.getName()+":"+p
                        )
                ).collect(joining(" "));
            services.put(
                "wave-"+waveIdx,
                ServiceDef.builder()
                    .imageDefinition(image("dadarek/wait-for-dependencies"))
                    .command(
                        cmd
                    )
                    .links(wave.stream().map(Node::getName).collect(toList()))
                    .depends_on(wave.stream().map(Node::getName).collect(toList()))
                    //todo these should be somehow configurable
                    .env("SLEEP_LENGTH", "1")
                    .env("TIMEOUT_LENGTH", "60")
                    .build()
            );
            waveIdx++;
        }
        Root root = Root.builder()
            .version("3.5")
            .services(services)
            .build();
        return toYaml(root);
    }

    private String prepareStartScript(String profile){
        Graph graph = new Graph();
        FacetSpec spec = options.forProfileStartup(profile);
        for (Service service: (Iterable< Service>) () -> options.getServices(spec).iterator()){
            graph.node(service.getName());
            for (String dep: service.getStartupDependencies())
                graph.connect(service.getName(), dep);
        }
        int waveIdx = 0;
        StringWriter writer = new StringWriter();
        //todo configurable shebang
        Stream.of(
            "#!/bin/bash",
            "",
            "RM_MAIN=0",
            "if [ \"$#\" -eq 1 ]; then",
            "    if [ \"$1\" = \"--rm-main\" ]; then",
            "        RM_MAIN=1",
            "    elif [ \"$1\" = \"--help\" ]; then",
            "        echo 'Usage:'",
            "        echo '  start.sh [--rm-main | --help]'",
            "        echo ''",
            "        echo 'Options:'",
            "        echo '--rm-main    After starting all the services in order, does'",
            "        echo '             `docker-compose rm -sf <main>` (if the project'",
            "        echo '             has main service configured; else - no-op)'",
            "        echo '--help       Shows this message and exit gracefully'",
            "        exit 0",
            "    else",
            "        echo \"Unrecognized flag $1! Try --help\"",
            "        exit 1",
            "    fi",
            "else",
            "    if [ \"$#\" -gt 1 ]; then",
            "        echo \"At most 1 script parameter supported! Found $# instead! Try --help\"",
            "        exit 1",
            "    fi",
            "fi",
            "",
            "HERE=\"$(realpath $(dirname \"$0\"))\""
        )
            .map(s -> s+"\n")
            .forEach(writer::write);
        while (!graph.isEmpty()){
            List<Node> wave = graph.pop(n -> n.getDegree() == 0);
            writer.write("$HERE/exec.sh -f "+options.scriptFilename(profile, "start-dependencies", "yml")+" up wave-"+(waveIdx++)+"\n");
            //todo maybe at this point we could manually remove service wave-i ? maybe it should be controllabel via script param
        }
        writer.write("$HERE/exec.sh up --remove-orphans -d\n");
        Stream.of(
            "if [ \"$RM_MAIN\" -eq 1 ]; then",
            options.getMainName() != null && !options.getMainName().trim().isEmpty() ?
                "    $HERE/exec.sh rm -sf "+options.getMainName().trim() :
                "    echo 'This project has no main service; --rm-main flag takes no effect'"
            ,
            "fi"
        )
            .map(s -> s+"\n")
            .forEach(writer::write);
        return writer.toString();
    }
}
