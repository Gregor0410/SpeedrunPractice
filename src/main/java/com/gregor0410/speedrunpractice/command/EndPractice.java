package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.io.IOException;

public class EndPractice implements Command<ServerCommandSource> {

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        MinecraftServer server = context.getSource().getMinecraftServer();
        ServerWorld world = null;
        //reset dragon fight data
        server.getSaveProperties().method_29037(new CompoundTag());
        ServerPlayerEntity player = context.getSource().getPlayer();
        long seed;
        try {
            seed = context.getArgument("seed", long.class);
        }catch (IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        try {
            world = ((IMinecraftServer)server).createEndPracticeWorld(seed);
            player.setSpawnPoint(World.OVERWORLD,null,false,false);
            ServerWorld.createEndSpawnPlatform(world);
            Practice.resetPlayer(player);
            Practice.getInventory(player,"end");
            player.teleport(world,100,49,0,90,0);
            Practice.startSpeedrunIGTTimer();
            return 1;
        } catch (IOException e) {
            return 0;
        }
    }

}
