package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.mixin.ServerPlayerEntityAccess;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class NetherPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed = new Random().nextLong();
        MinecraftServer server = ctx.getSource().getMinecraftServer();
        Map<RegistryKey<DimensionType>, PracticeWorld> linkedPracticeWorld = null;
        try {
            linkedPracticeWorld = ((IMinecraftServer) server).createLinkedPracticeWorld(seed);
        } catch (IOException e) {
            return 0;
        }
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        PracticeWorld overworld = linkedPracticeWorld.get(DimensionType.OVERWORLD_REGISTRY_KEY);
        server.getCommandManager().execute(server.getCommandSource(),"/advancement revoke @a everything");
        Practice.resetPlayer(player);
        ServerPlayerEntityAccess playerAccess = (ServerPlayerEntityAccess) player;
        playerAccess.setSpawnPointDimension(overworld.getRegistryKey());
        playerAccess.setSpawnPointPosition(overworld.getSpawnPos());
        playerAccess.setSpawnPointSet(true);
        overworld.getChunkManager().addTicket(ChunkTicketType.START,new ChunkPos(overworld.getSpawnPos()),11, Unit.INSTANCE);
        player.teleport(overworld,overworld.getSpawnPos().getX()/8d,overworld.getSpawnPos().getY(),overworld.getSpawnPos().getZ()/8d,90,0);
        player.setVelocity(Vec3d.ZERO);
        player.setInNetherPortal(overworld.getSpawnPos());
        PracticeWorld nether = linkedPracticeWorld.get(DimensionType.THE_NETHER_REGISTRY_KEY);
        nether.getPortalForcer().createPortal(player);
        player.refreshPositionAndAngles(overworld.getSpawnPos(),90,0);
        overworld.getPortalForcer().createPortal(player);
        player.netherPortalCooldown = player.getDefaultNetherPortalCooldown();
        Practice.getInventory(player, "nether");
        //this needs to be a server task so the portal gets added to poi storage before the changeDimension call
        server.execute(()-> {
            player.changeDimension(nether);
        });
        return 1;
    }
}
