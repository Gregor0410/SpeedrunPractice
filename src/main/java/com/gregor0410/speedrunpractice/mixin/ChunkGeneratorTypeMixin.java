package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.world.gen.chunk.ChunkGeneratorType;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("UnresolvedMixinReference")
@Mixin(ChunkGeneratorType.Preset.class)
public class ChunkGeneratorTypeMixin {
    @Redirect(method="createCavesType",at=@At(value="INVOKE",target = "Lcom/google/common/collect/Maps;newHashMap(Ljava/util/Map;)Ljava/util/HashMap;"))
    private static HashMap<StructureFeature<?>, StructureConfig> modifyNetherStructuresConfig(Map<StructureFeature<?>, StructureConfig> value){
        return (HashMap<StructureFeature<?>, StructureConfig>) SpeedrunPractice.netherStructures;
    }
}
