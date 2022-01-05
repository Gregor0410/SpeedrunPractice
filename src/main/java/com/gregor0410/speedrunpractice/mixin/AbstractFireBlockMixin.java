package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {
    @Redirect(method ="onBlockAdded",at=@At(value="INVOKE",target="Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
    private RegistryKey<World> resolveRegistryKey(World world){
        if(world instanceof PracticeWorld){
            return ((PracticeWorld)world).getVanillaWorldKey();
        }else{
            return world.getRegistryKey();
        }
    }
}
