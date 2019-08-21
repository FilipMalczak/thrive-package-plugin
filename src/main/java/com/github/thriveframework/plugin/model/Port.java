package com.github.thriveframework.plugin.model;

import lombok.Value;

@Value(staticConstructor = "between")
public class Port {
    int external;
    int internal;

    public static Port exposed(int justExposed){
        return between(justExposed, justExposed);
    }
}
