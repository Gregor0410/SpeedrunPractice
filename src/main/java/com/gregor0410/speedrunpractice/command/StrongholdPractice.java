package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.Objects;

public class StrongholdPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed;
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        Practice.linkedPracticeWorldPractice(ctx,seed,SpeedrunPractice.config.postBlindSpawnChunks,false,false,this::getOverworldPos,"stronghold");
        return 1;
    }

    private BlockPos getOverworldPos(ServerWorld overworld) {
        ChunkPos strongholdLoc = new ChunkPos(Objects.requireNonNull(overworld.getChunkManager().getChunkGenerator().locateStructure(overworld, StructureFeature.STRONGHOLD, new BlockPos(0, 0, 0), 100, false)));
        Chunk chunk = overworld.getChunk(strongholdLoc.getCenterBlockPos());
        int y = 0;
        StructureStart<?> strongholdStart;
        do {
            strongholdStart = overworld.getStructureAccessor().getStructureStart(ChunkSectionPos.from(strongholdLoc,y),StructureFeature.STRONGHOLD,chunk);
            y++;
        }while(strongholdStart==null && y<16);
        if (strongholdStart==null) return strongholdLoc.getCenterBlockPos();
        StructurePiece startPiece = strongholdStart.getChildren().get(0);
        return new BlockPos(startPiece.getBoundingBox().getCenter().offset(Direction.UP,3).offset(startPiece.getFacing(),-1));
    }
}
