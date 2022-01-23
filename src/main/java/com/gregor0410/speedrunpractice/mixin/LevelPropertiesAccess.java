package com.gregor0410.speedrunpractice.mixin;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelProperties.class)
public interface LevelPropertiesAccess {
    @Accessor("field_25030")
    LevelInfo getLevelInfo();
    @Accessor("field_25030")
    void setLevelInfo(LevelInfo levelInfo);
    @Accessor("field_25425")
    GeneratorOptions getGeneratorOptions();
    @Accessor("field_25426")
    Lifecycle getLifecycle();
}
