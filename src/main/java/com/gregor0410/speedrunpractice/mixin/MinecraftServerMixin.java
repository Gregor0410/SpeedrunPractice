package com.gregor0410.speedrunpractice.mixin;

import com.google.common.collect.ImmutableList;
import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.VanillaLayeredBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {
    @Shadow @Final private Executor workerExecutor;

    @Shadow @Final protected LevelStorage.Session session;

    @Shadow @Final protected SaveProperties saveProperties;

    @Shadow @Final private WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory;

    @Shadow @Final private Map<RegistryKey<World>, ServerWorld> worlds;

    @Shadow public abstract ServerWorld getOverworld();

    @Shadow public abstract @Nullable ServerWorld getWorld(RegistryKey<World> key);

    @Shadow
    protected static void setupSpawn(ServerWorld serverWorld, ServerWorldProperties serverWorldProperties, boolean bl, boolean bl2, boolean bl3) {
    }

    private final List<PracticeWorld> endPracticeWorlds = new ArrayList<>();
    private final List<Map<RegistryKey<DimensionType>,PracticeWorld>> linkedPracticeWorlds = new ArrayList<>();

    @Override
    public ServerWorld createEndPracticeWorld() throws IOException {
        //reset dragon fight data
        this.saveProperties.method_29037(new CompoundTag());
        long seed = new Random().nextLong();
        RegistryKey<World> worldRegistryKey = createWorldKey();
        PracticeWorld endPracticeWorld = createPracticeWorld(seed, worldRegistryKey, DimensionType.THE_END_REGISTRY_KEY,World.OVERWORLD,World.NETHER,World.END);
        this.worlds.put(worldRegistryKey,endPracticeWorld);
        this.endPracticeWorlds.add(endPracticeWorld);
        while(endPracticeWorlds.size()>2){
            //remove previous practice worlds
            PracticeWorld world = endPracticeWorlds.remove(0);
            removePracticeWorld(world);
        }
        return endPracticeWorld;
    }

    private RegistryKey<World> createWorldKey() {
        return RegistryKey.of(Registry.DIMENSION, new Identifier("speedrun_practice", UUID.randomUUID().toString()));
    }

    @Override
    public Map<RegistryKey<DimensionType>, PracticeWorld> createLinkedPracticeWorld(long seed) throws IOException {
        Map<RegistryKey<DimensionType>,PracticeWorld> linkedWorlds = new HashMap<>();
        RegistryKey<World> overworldKey = createWorldKey();
        RegistryKey<World> netherKey = createWorldKey();
        RegistryKey<World> endKey = createWorldKey();
        PracticeWorld overworld = createPracticeWorld(seed, overworldKey, DimensionType.OVERWORLD_REGISTRY_KEY, overworldKey, netherKey, endKey);
        PracticeWorld nether = createPracticeWorld(seed, netherKey, DimensionType.THE_NETHER_REGISTRY_KEY, overworldKey, netherKey, endKey);
        PracticeWorld end = createPracticeWorld(seed, endKey, DimensionType.THE_END_REGISTRY_KEY, overworldKey, netherKey, endKey);
        linkedWorlds.put(DimensionType.OVERWORLD_REGISTRY_KEY, overworld);
        linkedWorlds.put(DimensionType.THE_NETHER_REGISTRY_KEY, nether);
        linkedWorlds.put(DimensionType.THE_END_REGISTRY_KEY, end);
        this.worlds.put(overworldKey,overworld);
        this.worlds.put(netherKey,nether);
        this.worlds.put(endKey,end);
        //setup overworld spawn
        setupSpawn(overworld,((ServerWorldAccess) overworld).getWorldProperties(),false,false,true);
        linkedPracticeWorlds.add(linkedWorlds);
        while(linkedPracticeWorlds.size()>2){
            //remove previous practice worlds
            Map<RegistryKey<DimensionType>, PracticeWorld> world = linkedPracticeWorlds.remove(0);
            removeLinkedPracticeWorld(world);
        }
        return linkedWorlds;
    }

    @NotNull
    private PracticeWorld createPracticeWorld(long seed, RegistryKey<World> worldRegistryKey, RegistryKey<DimensionType> dimensionRegistryKey, RegistryKey<World> associatedOverworld, RegistryKey<World> associatedNether, RegistryKey<World> associatedEnd) {
        ChunkGenerator chunkGenerator;
        DimensionType dimensionType;
        if(Objects.equals(dimensionRegistryKey.getValue().getPath(), "overworld")){
            dimensionType = DimensionType.getOverworldDimensionType();
            chunkGenerator = new SurfaceChunkGenerator(new VanillaLayeredBiomeSource(seed,false,false),seed,ChunkGeneratorType.Preset.OVERWORLD.getChunkGeneratorType());
        }else if(Objects.equals(dimensionRegistryKey.getValue().getPath(), "the_nether")){
            dimensionType = DimensionTypeAccess.getNetherType();
            chunkGenerator = DimensionTypeAccess.invokeCreateNetherGenerator(seed);
        }else{
            dimensionType = DimensionTypeAccess.getEndType();
            chunkGenerator = DimensionTypeAccess.invokeCreateEndGenerator(seed);
        }
        ServerWorldProperties mainWorldProperties = this.saveProperties.getMainWorldProperties();
        ServerWorldProperties serverWorldProperties = new LevelProperties(((LevelPropertiesAccess)mainWorldProperties).getLevelInfo(),((LevelPropertiesAccess)mainWorldProperties).getGeneratorOptions(),((LevelPropertiesAccess)mainWorldProperties).getLifecycle());
        return new PracticeWorld((MinecraftServer)(Object) this,
                this.workerExecutor,
                this.session,
                serverWorldProperties,
                worldRegistryKey,
                dimensionRegistryKey,
                dimensionType,
                worldGenerationProgressListenerFactory.create(11),
                chunkGenerator,
                false,
                BiomeAccess.hashSeed(seed),
                ImmutableList.of(),
                false,
                associatedOverworld,
                associatedNether,
                associatedEnd);
    }

    private void removePracticeWorld(PracticeWorld world) throws IOException {
        world.disconnect();
        File worldFolder = this.session.getWorldDirectory(world.getRegistryKey());
        this.worlds.remove(world.getRegistryKey());
        FileUtils.deleteDirectory(worldFolder);
    }

    private void removeLinkedPracticeWorld(Map<RegistryKey<DimensionType>,PracticeWorld> linkedPracticeWorld) throws IOException {
        for(PracticeWorld practiceWorld : linkedPracticeWorld.values()){
            removePracticeWorld(practiceWorld);
        }
    }

    @Inject(method="shutdown",at=@At("HEAD"))
    private void removePracticeWorlds(CallbackInfo ci) throws IOException {
        for(PracticeWorld practiceWorld : this.endPracticeWorlds){
            removePracticeWorld(practiceWorld);
        }
        for(Map<RegistryKey<DimensionType>,PracticeWorld> linkedPracticeWorld : this.linkedPracticeWorlds){
            removeLinkedPracticeWorld(linkedPracticeWorld);
        }
    }

    public List<PracticeWorld> getEndPracticeWorlds() {
        return endPracticeWorlds;
    }
}
