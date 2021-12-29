package com.gregor0410.speedrunpractice;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.server.world.ServerWorld;

import java.io.IOException;
import java.util.List;

public interface IMinecraftServer {
    ServerWorld createEndPracticeWorld() throws IOException;
    List<PracticeWorld> getPracticeWorlds();
}
