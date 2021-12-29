package com.gregor0410.speedrunpractice.mixin;

import com.google.common.collect.ImmutableList;
import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.SurfaceChunkGenerator;
import net.minecraft.world.level.UnmodifiableLevelProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.apache.commons.io.FileUtils;
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

    @Shadow protected abstract boolean shouldKeepTicking();

    @Shadow public abstract ServerWorld getOverworld();

    @Shadow public abstract @Nullable ServerWorld getWorld(RegistryKey<World> key);

    @Shadow public abstract PlayerManager getPlayerManager();

    private List<PracticeWorld> practiceWorlds = new ArrayList<>();

    @Override
    public ServerWorld createEndPracticeWorld() throws IOException {
        //reset dragon fight data
        this.saveProperties.method_29037(new CompoundTag());
        long seed = new Random().nextLong();
        RegistryKey<World> worldRegistryKey = RegistryKey.of(Registry.DIMENSION,new Identifier("speedrun_practice", UUID.randomUUID().toString()));
        ChunkGenerator chunkGenerator = new SurfaceChunkGenerator(new TheEndBiomeSource(seed),seed,ChunkGeneratorType.Preset.END.getChunkGeneratorType());

        PracticeWorld endPracticeWorld = new PracticeWorld((MinecraftServer)(Object)this,
                this.workerExecutor,
                this.session,
                new UnmodifiableLevelProperties(this.saveProperties,this.saveProperties.getMainWorldProperties()),
                worldRegistryKey,
                DimensionType.THE_END_REGISTRY_KEY,
                DimensionTypeAccess.getEndType(),
                worldGenerationProgressListenerFactory.create(11),
                chunkGenerator,
                false,
                BiomeAccess.hashSeed(seed),
                ImmutableList.of(),
                false);
        endPracticeWorld.savingDisabled = true;
        this.worlds.put(worldRegistryKey,endPracticeWorld);
        this.practiceWorlds.add(endPracticeWorld);
        while(practiceWorlds.size()>2){
            //remove previous practice worlds
            PracticeWorld world = practiceWorlds.remove(0);
            removePracticeWorld(world);
        }
        return endPracticeWorld;
    }

    private void removePracticeWorld(PracticeWorld world) throws IOException {
        world.disconnect();
        File worldFolder = this.session.getWorldDirectory(world.getRegistryKey());
        this.worlds.remove(world.getRegistryKey());
        FileUtils.deleteDirectory(worldFolder);
    }

    @Inject(method="shutdown",at=@At("HEAD"))
    private void removePracticeWorlds(CallbackInfo ci) throws IOException {
        for(PracticeWorld practiceWorld : this.practiceWorlds){
            removePracticeWorld(practiceWorld);
        }
    }

    public List<PracticeWorld> getPracticeWorlds() {
        return practiceWorlds;
    }
}
