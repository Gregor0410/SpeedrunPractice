package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.Unit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.util.Map;

public class OverworldPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed;
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        MinecraftServer server = ctx.getSource().getMinecraftServer();
        Map<RegistryKey<DimensionType>, PracticeWorld> linkedPracticeWorld = null;
        try {
            linkedPracticeWorld = ((IMinecraftServer) server).createLinkedPracticeWorld(seed);
        } catch (IOException e) {
            return 0;
        }
        player.inventory.clear();
        server.getCommandManager().execute(server.getCommandSource().withSilent(),"/advancement revoke @a everything");
        PracticeWorld overworld = linkedPracticeWorld.get(DimensionType.OVERWORLD_REGISTRY_KEY);
        overworld.getChunkManager().addTicket(ChunkTicketType.START,new ChunkPos(overworld.getSpawnPos()),11, Unit.INSTANCE);
        Practice.setSpawnPos(overworld,player);
        player.teleport(overworld,overworld.getSpawnPos().getX(),overworld.getSpawnPos().getY(),overworld.getSpawnPos().getZ(),90,0);
        Practice.resetPlayer(player);
        Practice.getInventory(player, "overworld");
        Practice.startSpeedrunIGTTimer();
        return 1;
    }

}

