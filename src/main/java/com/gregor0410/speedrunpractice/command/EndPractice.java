package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.io.IOException;

public class EndPractice implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        Practice.getInventory(player,"end");
        MinecraftServer server = context.getSource().getMinecraftServer();
        ServerWorld world = null;
        try {
            world = ((IMinecraftServer)server).createEndPracticeWorld();
            ServerWorld.createEndSpawnPlatform(world);
            Practice.resetPlayer(player);
            player.teleport(world,100,49,0,90,0);
            return 1;
        } catch (IOException e) {
            return 0;
        }
    }

}
