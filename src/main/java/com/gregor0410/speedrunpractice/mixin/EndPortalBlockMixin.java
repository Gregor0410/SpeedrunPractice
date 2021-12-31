package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.world.PracticeWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @ModifyVariable(method="onEntityCollision",at=@At("STORE"),ordinal = 0)
    private RegistryKey<World> resolveEndPortalTargetWorld(RegistryKey<World> registryKey,BlockState state, World world, BlockPos pos, Entity entity){
        if(world instanceof PracticeWorld){
            if(world.getDimension().equals(DimensionTypeAccess.getEndType())){
                return ((PracticeWorld)world).associatedWorlds.get(World.OVERWORLD);
            }else{
                return ((PracticeWorld)world).associatedWorlds.get(World.END);
            }
        }else{
            return registryKey;
        }
    }
}
