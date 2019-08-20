package com.github.thriveframework.plugin.model;

import lombok.Value;

@Value(staticConstructor = "between")
public class Port {
    int internal;
    int external;

    public static Port between(int justExposed){
        return between(justExposed, justExposed);
    }
}
