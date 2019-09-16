package com.github.thriveframework.plugin.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public final class ImageDefinition {
    @NonNull String composeKey;
    @NonNull String imageSpec;

    public static ImageDefinition build(String path){
        return new ImageDefinition("build", path);
    }

    public static ImageDefinition image(String image){
        return new ImageDefinition("image", image);
    }
}
