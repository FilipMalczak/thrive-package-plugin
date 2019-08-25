package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PRIVATE)
@Setter(PRIVATE)
@FieldDefaults(level = PRIVATE)
public class Port {
    int external;
    int internal;

    public static Port between(int external, int internal){
        return new Port(external, internal);
    }

    public static Port exposed(int justExposed){
        return between(justExposed, justExposed);
    }
}
