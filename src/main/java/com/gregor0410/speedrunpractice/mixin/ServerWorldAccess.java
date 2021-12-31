package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerWorld.class)
public interface ServerWorldAccess {
    @Accessor
    ServerWorldProperties getWorldProperties();
}
