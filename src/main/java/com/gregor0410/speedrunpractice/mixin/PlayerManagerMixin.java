package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Shadow @Final private MinecraftServer server;

    @ModifyVariable(method="respawnPlayer",at=@At(value="STORE"),ordinal =1)
    private ServerWorld modifySpawnWorld(ServerWorld serverWorld, ServerPlayerEntity player,boolean alive){
        ServerWorld serverWorld1 = player.getServerWorld();
        if(serverWorld1 instanceof PracticeWorld && serverWorld ==server.getOverworld()){
            return server.getWorld(((PracticeWorld) serverWorld1).associatedWorlds.get(World.OVERWORLD));
        }else{
            return serverWorld;
        }
    }

    @Inject(method = "onPlayerConnect",at=@At("TAIL"))
    private void showWelcomeMessage(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) throws IOException, VersionParsingException {
        if(!SpeedrunPractice.welcomeShown)
            SpeedrunPractice.sendWelcomeMessage(player);
        SpeedrunPractice.welcomeShown = true;
    }
}
