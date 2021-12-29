package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Shadow public abstract ServerWorld getServerWorld();

    @Shadow @Final public MinecraftServer server;

    @Shadow @Nullable public abstract BlockPos getSpawnPointPosition();

    @Shadow public abstract void teleport(ServerWorld targetWorld, double x, double y, double z, float yaw, float pitch);

    @Shadow public abstract RegistryKey<World> getSpawnPointDimension();

    @Inject(method="onDisconnect",at=@At("HEAD"))
    private void removeFromPracticeWorld(CallbackInfo ci){
        if(this.getServerWorld() instanceof PracticeWorld){
            BlockPos spawn = this.getSpawnPointPosition();
            if(spawn!=null){
                this.teleport(this.server.getWorld(this.getSpawnPointDimension()),spawn.getX(),spawn.getY(), spawn.getZ(), 90,0);
            }else{
                spawn = this.server.getOverworld().getSpawnPos();
                this.teleport(this.server.getOverworld(),spawn.getX(),spawn.getY(),spawn.getZ(),90,0);
            }
        }
    }
}
