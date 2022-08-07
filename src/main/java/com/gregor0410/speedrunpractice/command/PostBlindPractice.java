package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Random;

public class PostBlindPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int maxDist;
        long seed;
        try {
            maxDist = ctx.getArgument("maxDist", Integer.class);
        }catch (IllegalArgumentException e){
            maxDist = SpeedrunPractice.config.defaultMaxDist;
        }
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        int finalMaxDist = maxDist;
        long finalSeed = seed;
        Practice.linkedPracticeWorldPractice(ctx,seed,SpeedrunPractice.config.postBlindSpawnChunks,false,true,(overworld)->getOverworldPos(overworld, finalMaxDist,new Random(finalSeed)),"postblind");
        ctx.getSource().getMinecraftServer().execute(()->{
            if(SpeedrunPractice.config.randomisePostBlindInventory) {
                try {
                    Practice.populatePostBlindInventory(ctx.getSource().getPlayer(), finalSeed);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        return 1;
    }

    private BlockPos getOverworldPos(ServerWorld overworld, int maxDist, Random random) {
        ChunkPos strongholdLoc = new ChunkPos(overworld.getChunkManager().getChunkGenerator().locateStructure(overworld, StructureFeature.STRONGHOLD,new BlockPos(0,0,0),100,false));
        double angle = random.nextDouble() * 2*Math.PI;
        int dist = maxDist >0 ?random.nextInt(maxDist) : 0;
        int x = strongholdLoc.getStartX()+8+(int)Math.round(Math.cos(angle) * dist);
        int z = strongholdLoc.getStartZ()+8+(int)Math.round(Math.sin(angle) * dist);
        int y = overworld.getChunk(x >> 4, z >> 4).sampleHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x & 15, z & 15);
        if(SpeedrunPractice.config.caveSpawns) {
            y = random.nextInt(y) + 20;
        }
        return new BlockPos(x,y,z);
    }
}
