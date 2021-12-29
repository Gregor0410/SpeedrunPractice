package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BastionRemnantFeatureConfig.class)
public class BastionRemnantFeatureConfigMixin {
    @Mutable
    @Shadow @Final private List<StructurePoolFeatureConfig> possibleConfigs;

    @Inject(method = "<init>(Ljava/util/List;)V",at=@At("TAIL"))
    private void modifyPossibleConfigs(CallbackInfo ci){
        this.possibleConfigs= SpeedrunPractice.possibleBastionConfigs;
    }
}
