package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(StructuresConfig.class)
public class StructuresConfigMixin {
    @Redirect(method = "<init>",at=@At(value="FIELD",target ="Lnet/minecraft/world/gen/chunk/StructuresConfig;structures:Ljava/util/Map;",opcode = Opcodes.PUTFIELD))
    private void modifyStructures(StructuresConfig sc, Map<StructureFeature<?>, StructureConfig> structures){
        ((StructuresConfigAccess)sc).setStructures(SpeedrunPractice.structures);
    }
}
