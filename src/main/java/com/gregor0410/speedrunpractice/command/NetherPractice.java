package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.util.Map;

public class NetherPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed;
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        MinecraftServer server = ctx.getSource().getMinecraftServer();
        Map<RegistryKey<DimensionType>, PracticeWorld> linkedPracticeWorld = null;
        try {
            linkedPracticeWorld = ((IMinecraftServer) server).createLinkedPracticeWorld(seed);
        } catch (IOException e) {
            return 0;
        }
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        PracticeWorld overworld = linkedPracticeWorld.get(DimensionType.OVERWORLD_REGISTRY_KEY);
        PracticeWorld nether = linkedPracticeWorld.get(DimensionType.THE_NETHER_REGISTRY_KEY);
        Practice.setSpawnPos(overworld,player);
        overworld.getChunkManager().addTicket(ChunkTicketType.START,new ChunkPos(overworld.getSpawnPos()),11, Unit.INSTANCE);
        BlockPos overworldPos = overworld.getSpawnPos();
        BlockPos netherPos = new BlockPos(overworldPos.getX()/8D,overworldPos.getY(),overworldPos.getZ()/8D);
        ((ServerPlayerEntityAccess)player).setEnteredNetherPos(Vec3d.ofCenter(netherPos));
        Practice.createPortals(linkedPracticeWorld, player, overworld, overworldPos);
        server.getCommandManager().execute(server.getCommandSource().withSilent(),"/advancement revoke @a everything");
        //this needs to be a server task so the portal gets added to poi storage before the changeDimension call
        server.execute(()-> {
            player.refreshPositionAndAngles(netherPos,90,0);
            Practice.resetPlayer(player);
            Practice.getInventory(player, "nether");
            player.changeDimension(nether);
            player.setVelocity(Vec3d.ZERO);
            Practice.startSpeedrunIGTTimer();
        });
        return 1;
    }

}
