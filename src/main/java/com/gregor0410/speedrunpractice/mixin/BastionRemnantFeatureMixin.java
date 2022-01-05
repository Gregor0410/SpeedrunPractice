package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BastionRemnantFeature.class)
public class BastionRemnantFeatureMixin {
    @Inject(method="shouldStartAt(Lnet/minecraft/world/gen/chunk/ChunkGenerator;Lnet/minecraft/world/biome/source/BiomeSource;JLnet/minecraft/world/gen/ChunkRandom;IILnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/gen/feature/BastionRemnantFeatureConfig;)Z",cancellable = true,at=@At("HEAD"))
    private void shouldStartAt(ChunkGenerator chunkGenerator, BiomeSource biomeSource, long l, ChunkRandom chunkRandom, int i, int j, Biome biome, ChunkPos chunkPos, BastionRemnantFeatureConfig bastionRemnantFeatureConfig,CallbackInfoReturnable<Boolean> cir){
        if(!(SpeedrunPractice.config.bridge||SpeedrunPractice.config.housing||SpeedrunPractice.config.treasure||SpeedrunPractice.config.stables)){
            cir.setReturnValue(false);
        }else if(SpeedrunPractice.config.bastionRarity == 60){
            cir.setReturnValue(chunkRandom.nextInt(5)>=2);
        }
        else{
            cir.setReturnValue(chunkRandom.nextInt(100) >= (100-SpeedrunPractice.config.bastionRarity));
        }
    }
}
