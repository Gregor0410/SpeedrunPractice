package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(PortalForcer.class)
public class PortalForcerMixin {
    @Shadow @Final private ServerWorld world;
    private BlockPos.Mutable mutable;

    @ModifyVariable(method = "createPortal",at=@At("STORE"),ordinal = 0)
    private BlockPos.Mutable getMutable(BlockPos.Mutable mutable){
        this.mutable = mutable;
        return mutable;
    }

    @Inject(method="createPortal",at=@At("TAIL"))
    private void savePortalChunk(Entity entity, CallbackInfoReturnable<Boolean> cir){
        ChunkHolder chunkHolder = ((ThreadedAnvilChunkStorageAccess) world.getChunkManager().threadedAnvilChunkStorage).getChunkHolders().get(new ChunkPos(this.mutable).toLong());
        CompletableFuture<Chunk> completableFuture = chunkHolder.getFuture();
        ((ThreadedAnvilChunkStorageAccess) world.getChunkManager().threadedAnvilChunkStorage).getMainThreadExecutor().runTasks(completableFuture::isDone);
        completableFuture.join();
        world.getChunkManager().save(false);
    }
}
