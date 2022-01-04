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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Heightmap;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.StructureFeature;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class PostBlindPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int maxDist;
        try {
            maxDist = ctx.getArgument("maxDist", Integer.class);
        }catch (IllegalArgumentException e){
            maxDist = SpeedrunPractice.config.defaultMaxDist;
        }
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
        server.getCommandManager().execute(server.getCommandSource().withSilent(),"/advancement revoke @a everything");
        PracticeWorld overworld = linkedPracticeWorld.get(DimensionType.OVERWORLD_REGISTRY_KEY);
        overworld.getChunkManager().addTicket(ChunkTicketType.START,new ChunkPos(overworld.getSpawnPos()),11, Unit.INSTANCE);
        Practice.setSpawnPos(overworld,player);
        BlockPos overworldPos = getOverworldPos(overworld,maxDist,new Random(seed));
        Practice.createPortals(linkedPracticeWorld, player, overworld, overworldPos);
        player.teleport(overworld,overworld.getSpawnPos().getX(),overworld.getSpawnPos().getY(),overworld.getSpawnPos().getZ(),90,0);
        //this needs to be a server task so the portal gets added to poi storage before the changeDimension call
        server.execute(()-> {
            Practice.resetPlayer(player);
            player.refreshPositionAndAngles(overworldPos,90,0);
            Practice.getInventory(player, "postblind");
            player.changeDimension(overworld);
            Practice.startSpeedrunIGTTimer();
        });
        return 1;
    }

    private BlockPos getOverworldPos(PracticeWorld overworld,int maxDist, Random random) {
        ChunkPos strongholdLoc = new ChunkPos(overworld.getChunkManager().getChunkGenerator().locateStructure(overworld, StructureFeature.STRONGHOLD,new BlockPos(0,0,0),100,false));
        double angle = random.nextDouble() * 2*Math.PI;
        int dist = maxDist >0 ?random.nextInt(maxDist) : 0;
        int x = strongholdLoc.getStartX()+8+(int)Math.round(Math.cos(angle) * dist);
        int z = strongholdLoc.getStartZ()+8+(int)Math.round(Math.sin(angle) * dist);
        int y = overworld.getChunk(x >> 4, z >> 4).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x & 15, z & 15);
        y = random.nextInt(y)+20;
        return new BlockPos(x,y,z);
    }
}
