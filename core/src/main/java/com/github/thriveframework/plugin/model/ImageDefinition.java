package com.github.thriveframework.plugin.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public final class ImageDefinition {
    String composeKey;
    String imageSpec;

    public static ImageDefinition build(String path){
        return new ImageDefinition("build", path);
    }

    public static ImageDefinition image(String image){
        return new ImageDefinition("image", image);
    }
}
