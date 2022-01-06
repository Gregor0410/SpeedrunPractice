package com.gregor0410.speedrunpractice.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
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
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Shadow public abstract PlayerManager getPlayerManager();

    private final List<PracticeWorld> endPracticeWorlds = new ArrayList<>();
    private final List<Map<RegistryKey<DimensionType>,PracticeWorld>> linkedPracticeWorlds = new ArrayList<>();

    public PracticeWorld createEndPracticeWorld(long seed,RegistryKey<World> worldRegistryKey) throws IOException {
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
    @Override
    public PracticeWorld createEndPracticeWorld(long seed) throws IOException {
        return createEndPracticeWorld(seed,createWorldKey(seed));
    }

    private RegistryKey<World> createWorldKey(long seed) {
        return RegistryKey.of(Registry.DIMENSION, new Identifier("speedrun_practice", seed+"_"+UUID.randomUUID().toString()+"_3"));
    }

    private Map<String,RegistryKey<World>> createLinkedWorldKeys(long seed){
        String uuid = UUID.randomUUID().toString();
        return ImmutableMap.of(
                "overworld",RegistryKey.of(Registry.DIMENSION,new Identifier("speedrun_practice",seed+"_"+uuid+"_0")),
                "nether",RegistryKey.of(Registry.DIMENSION,new Identifier("speedrun_practice",seed+"_"+uuid+"_1")),
                "end",RegistryKey.of(Registry.DIMENSION,new Identifier("speedrun_practice",seed+"_"+uuid+"_2"))
        );
    }

    @Inject(method="createWorlds",at=@At("TAIL"))
    private void createPracticeWorlds(CallbackInfo ci) throws IOException {
        Path worldDir = this.session.getDirectory(WorldSavePath.ROOT).resolve("dimensions/speedrun_practice");
        String[] dimensions = {"overworld","nether","end"};
        Map<String,Map<String,RegistryKey<World>>> linkedKeys = new HashMap<>();
        Map<String,Long> seeds = new HashMap<>();
        if(worldDir.toFile().isDirectory()) {
            File[] worldFiles = worldDir.toFile().listFiles();
            if (worldFiles != null) {
                for (File worldFile : worldFiles) {
                    Pattern pattern = Pattern.compile("(-?\\d*)_(.*)_(\\d)");
                    Matcher matcher = pattern.matcher(worldFile.getName());
                    if (matcher.find()) {
                        long seed = Long.parseLong(matcher.group(1));
                        String uuid = matcher.group(2);
                        int dimNo = Integer.parseInt(matcher.group(3));
                        if (dimNo < 3) {
                            Map<String, RegistryKey<World>> map = linkedKeys.get(uuid) != null ? linkedKeys.get(uuid) : new HashMap<>();
                            linkedKeys.put(uuid, map);
                            map.put(dimensions[dimNo], RegistryKey.of(Registry.DIMENSION, new Identifier("speedrun_practice", worldFile.getName())));
                            seeds.put(uuid, seed);
                        } else {
                            //dimNo of 3 is an end practice world
                            endPracticeWorlds.add(createEndPracticeWorld(seed, RegistryKey.of(Registry.DIMENSION, new Identifier("speedrun_practice", worldFile.getName()))));
                        }
                    }
                }
                for (String uuid : linkedKeys.keySet()) {
                    Map<String, RegistryKey<World>> linkedKey = linkedKeys.get(uuid);
                    RegistryKey<World> overworldKey = linkedKey.get("overworld");
                    RegistryKey<World> netherKey = linkedKey.get("nether");
                    RegistryKey<World> endKey = linkedKey.get("end");
                    linkedPracticeWorlds.add(createLinkedPracticeWorld(seeds.get(uuid), linkedKey));
                }
            }
        }
    }
    @Override
    public Map<RegistryKey<DimensionType>, PracticeWorld> createLinkedPracticeWorld(long seed) throws IOException {
        return createLinkedPracticeWorld(seed,createLinkedWorldKeys(seed));
    }

    private Map<RegistryKey<DimensionType>, PracticeWorld> createLinkedPracticeWorld(long seed, Map<String,RegistryKey<World>> linkedKeys) throws IOException {
        this.saveProperties.method_29037(new CompoundTag());
        Map<RegistryKey<DimensionType>,PracticeWorld> linkedWorlds = new HashMap<>();
        RegistryKey<World> overworldKey = linkedKeys.get("overworld");
        RegistryKey<World> netherKey = linkedKeys.get("nether");
        RegistryKey<World> endKey = linkedKeys.get("end");
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
                true,
                seed,
                associatedOverworld,
                associatedNether,
                associatedEnd);
    }

    private void removePracticeWorld(PracticeWorld world) throws IOException {
        EnderDragonFight enderDragonFight = world.getEnderDragonFight();
        world.disconnect();
        File worldFolder = this.session.getWorldDirectory(world.getRegistryKey());
        this.worlds.remove(world.getRegistryKey());
        if(enderDragonFight!=null){
            ((EnderDragonFightAccess)enderDragonFight).getBossBar().clearPlayers();
        }
        if(SpeedrunPractice.config.deletePracticeWorlds) {
            FileUtils.deleteDirectory(worldFolder);
        }
    }

    private void removeLinkedPracticeWorld(Map<RegistryKey<DimensionType>,PracticeWorld> linkedPracticeWorld) throws IOException {
        for(PracticeWorld practiceWorld : linkedPracticeWorld.values()){
            removePracticeWorld(practiceWorld);
        }
    }

    @Inject(method="shutdown",at=@At("HEAD"))
    private void removePracticeWorlds(CallbackInfo ci) throws IOException {
        for(ServerPlayerEntity player : this.getPlayerManager().getPlayerList()){
            //reset spawn point to overworld if spawn point is in a PracticeWorld
            if(Objects.equals(player.getSpawnPointDimension().getValue().getNamespace(), "speedrun_practice")){
                player.setSpawnPoint(World.OVERWORLD,null,false,false);
                this.getPlayerManager().respawnPlayer(player,true);
            }
        }
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
