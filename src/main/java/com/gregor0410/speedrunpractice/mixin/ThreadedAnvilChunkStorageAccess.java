package com.gregor0410.speedrunpractice.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.util.thread.ThreadExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface ThreadedAnvilChunkStorageAccess {
    @Accessor()
    Long2ObjectLinkedOpenHashMap<ChunkHolder> getChunkHolders();
    @Accessor()
    ThreadExecutor<Runnable> getMainThreadExecutor();
}
