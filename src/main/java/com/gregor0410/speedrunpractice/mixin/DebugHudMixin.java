package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DebugHud.class)
@Environment(EnvType.CLIENT)
public class DebugHudMixin {
    @Redirect(method="getLeftText",at=@At(value="INVOKE",target="Lnet/minecraft/client/world/ClientWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
    private RegistryKey<World> resolveDebugWorldKey(ClientWorld world){
        return PracticeWorld.dimensionToVanillaWorldKey.get(world.getDimension());
    }
}
