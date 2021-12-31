package com.gregor0410.speedrunpractice.mixin;

import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DimensionType.class)
public interface DimensionTypeAccess {
    @Accessor("THE_END")
    static DimensionType getEndType() {
        throw new AssertionError();
    }
    @Accessor("THE_NETHER")
    static DimensionType getNetherType(){
        throw new AssertionError();
    }
    @Invoker("createEndGenerator")
    static ChunkGenerator invokeCreateEndGenerator(long seed){
        throw new AssertionError();
    }
    @Invoker("createNetherGenerator")
    static ChunkGenerator invokeCreateNetherGenerator(long seed){
        throw new AssertionError();
    }
}
