package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Inject(method="createEndSpawnPlatform",at=@At("TAIL"))
    private static void eliminateCageSpawns(ServerWorld world, CallbackInfo ci){
        if(!SpeedrunPractice.config.eliminateCageSpawns) return;
        int x = ServerWorld.END_SPAWN_POS.getX();
        int z = ServerWorld.END_SPAWN_POS.getZ();
        int y = ServerWorld.END_SPAWN_POS.getY();
        BlockPos.iterate(x-4,y-1,z-4,x+4,y+50,z+4).forEach(blockPos -> {
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState());
        });
    }
}
