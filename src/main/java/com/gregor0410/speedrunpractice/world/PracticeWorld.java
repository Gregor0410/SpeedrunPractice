package com.gregor0410.speedrunpractice.world;

import com.google.common.collect.ImmutableMap;
import com.gregor0410.speedrunpractice.mixin.DimensionTypeAccess;
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
import java.util.Map;
import java.util.concurrent.Executor;

public class PracticeWorld extends ServerWorld {
    private final long seed;
    public final Map<RegistryKey<World>,RegistryKey<World>> associatedWorlds;
    public static final Map<DimensionType,RegistryKey<World>> dimensionToVanillaWorldKey = ImmutableMap.of(DimensionType.getOverworldDimensionType(),World.OVERWORLD, DimensionTypeAccess.getNetherType(),World.NETHER,DimensionTypeAccess.getEndType(),World.END);

    public PracticeWorld(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> registryKey, RegistryKey<DimensionType> registryKey2, DimensionType dimensionType, WorldGenerationProgressListener generationProgressListener, ChunkGenerator chunkGenerator, boolean bl, long l, List<Spawner> list, boolean bl2,long seed,RegistryKey<World> associatedOverworld,RegistryKey<World> associatedNether,RegistryKey<World> associatedEnd) {
        super(server, workerExecutor, session, properties, registryKey, registryKey2, dimensionType, generationProgressListener, chunkGenerator, bl, l, list, bl2);
        this.seed = seed;
        this.associatedWorlds = ImmutableMap.of(World.OVERWORLD,associatedOverworld,World.NETHER,associatedNether,World.END,associatedEnd);
    }

    @Override
    public long getSeed() {
        //give the correct seed for the world instead of the overworld seed
        return seed;
    }

    public RegistryKey<World> getVanillaWorldKey(){
        //get the associated vanilla world key
        return dimensionToVanillaWorldKey.get(this.getDimension());
    }
}
