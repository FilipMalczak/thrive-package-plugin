package com.github.thriveframework.plugin;

import com.beust.jcommander.JCommander;

import static java.util.Arrays.asList;

public class GeneratorApp {

    public static void main(String[] args){
        System.out.println("ARGS "+asList(args));
        AppSetup options = new AppSetup();
        JCommander.newBuilder().addObject(options).build().parse(args);
        options.init();
        ComposePreparator composePreparator = new ComposePreparator(options);
        composePreparator.run();
        StartupPreparator startupPreparator = new StartupPreparator(options);
        startupPreparator.run();
    }

}
