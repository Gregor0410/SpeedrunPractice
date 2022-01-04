package com.gregor0410.speedrunpractice;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IMinecraftServer {
    ServerWorld createEndPracticeWorld(long seed) throws IOException;
    Map<RegistryKey<DimensionType>, PracticeWorld> createLinkedPracticeWorld(long seed) throws IOException;
    List<PracticeWorld> getEndPracticeWorlds();
}
