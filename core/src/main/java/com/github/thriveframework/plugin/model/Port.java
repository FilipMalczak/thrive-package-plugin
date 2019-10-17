package com.github.thriveframework.plugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@FieldDefaults(level = PRIVATE)
public class Port {
    int external;
    int internal;

    public static Port between(int external, int internal){
        return new Port(external, internal);
    }

    public static Port just(int justExposed){
        return between(justExposed, justExposed);
    }
}
