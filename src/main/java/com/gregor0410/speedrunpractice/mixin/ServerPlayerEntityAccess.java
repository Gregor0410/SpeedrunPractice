package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerPlayerEntity.class)
public interface ServerPlayerEntityAccess {
    @Accessor
    void setSpawnPointDimension(RegistryKey<World> dimension);
    @Accessor
    void setSpawnPointSet(boolean bl);
    @Accessor
    void setSpawnPointPosition(@Nullable BlockPos pos);
}
