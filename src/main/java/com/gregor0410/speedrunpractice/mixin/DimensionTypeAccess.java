package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccess {
    @Accessor("THE_END")
    public static DimensionType getEndType() {
        throw new AssertionError();
    }


}
