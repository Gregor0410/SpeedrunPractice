package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MinecraftServer.class)
public interface MinecraftServerAccess {
    @Invoker("shouldKeepTicking")
    boolean invokeShouldKeepTicking();
}
