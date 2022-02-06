package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Entity.class)
public class EntityMixin{
    @Shadow public World world;

    @ModifyVariable(method="tickNetherPortal",at=@At("STORE"),ordinal=0)
    public RegistryKey<World> resolveNetherPortalTargetWorld(RegistryKey<World> registryKey){
        if(this.world instanceof PracticeWorld){
            return this.world.getDimension().equals(DimensionTypeAccess.getNetherType()) ? ((PracticeWorld) this.world).associatedWorlds.get(World.OVERWORLD):((PracticeWorld) this.world).associatedWorlds.get(World.NETHER);
        }else{
            return registryKey;
        }
    }
    @Redirect(method="changeDimension",at=@At(value="INVOKE",target="Lnet/minecraft/world/World;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
    private RegistryKey<World> resolveDimension(World world){
        return getWorldRegistryKey(world);
    }
    @Redirect(method="changeDimension",at=@At(value="INVOKE",target="Lnet/minecraft/server/world/ServerWorld;getRegistryKey()Lnet/minecraft/util/registry/RegistryKey;"))
    private RegistryKey<World> resolveDimension(ServerWorld world){
        return getWorldRegistryKey(world);
    }

    private RegistryKey<World> getWorldRegistryKey(World world) {
        if(world instanceof PracticeWorld){
            return ((PracticeWorld) world).getVanillaWorldKey();
        }else{
            return world.getRegistryKey();
        }
    }
}
