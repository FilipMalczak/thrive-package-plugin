package com.github.thriveframework.plugin;

import com.github.thriveframework.plugin.model.Composition;

public interface ThrivePackage {
    //fixme is this needed? maybe its enough to use Composition instead of package?
    String getName();

    Composition getComposition();
}
