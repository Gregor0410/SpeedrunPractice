package com.gregor0410.speedrunpractice.world;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Spawner;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;

import java.util.List;
import java.util.concurrent.Executor;

public class PracticeWorld extends ServerWorld {
    private final long seed;

    public PracticeWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2) {
        super(server, workerExecutor, session, properties, registryKey, registryKey2, dimensionType, generationProgressListener, chunkGenerator, bl, l, list, bl2);
        this.seed = l;
    }

    @Override
    public long getSeed() {
        //give the correct seed for the world instead of the overworld seed
        return seed;
    }
}
