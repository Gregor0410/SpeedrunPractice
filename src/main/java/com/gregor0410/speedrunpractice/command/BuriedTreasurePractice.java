package com.gregor0410.speedrunpractice.command;

import com.google.common.collect.ImmutableList;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.StructureFeature;

public class BuriedTreasurePractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed;
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        Practice.linkedPracticeWorldPractice(ctx,seed,false,false,false, BuriedTreasurePractice::getPos,"overworld");
        return 1;
    }

    public static BlockPos getPos(ServerWorld world){
        BlockPos spawn = world.getSpawnPos();
        BlockPos bt = world.locateStructure(StructureFeature.BURIED_TREASURE,spawn,100,false);
        if(bt!=null){
             BlockPos pos = world.getChunkManager().getChunkGenerator().getBiomeSource().locateBiome(bt.getX(),bt.getY(),bt.getZ(),64, ImmutableList.of(Biomes.BEACH,Biomes.SNOWY_BEACH),SpeedrunPractice.random);
             if(pos==null) pos=bt;
             Chunk chunk = world.getChunkManager().getChunk(pos.getX()>>4, pos.getZ()>>4,ChunkStatus.HEIGHTMAPS,true);
             if(chunk==null) return spawn;
             int y = chunk.sampleHeightmap(Heightmap.Type.MOTION_BLOCKING,pos.getX(),pos.getZ());
             return new BlockPos(pos.getX(),y+1,pos.getZ());
        }
        return spawn;
    }

}

