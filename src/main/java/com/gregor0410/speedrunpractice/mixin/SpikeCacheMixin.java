package com.gregor0410.speedrunpractice.mixin;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(targets ="net.minecraft.world.gen.feature.EndSpikeFeature$SpikeCache")
public class SpikeCacheMixin {
    @Redirect(method="load(Ljava/lang/Long;)Ljava/util/List;",at=@At(value="INVOKE",target="Ljava/util/Collections;shuffle(Ljava/util/List;Ljava/util/Random;)V"))
    private void modifyTowerOrder(List<Integer> towers, Random random){
        Collections.shuffle(towers,random);
        SpeedrunPractice.DragonType dragonType = SpeedrunPractice.config.dragonType.equals(SpeedrunPractice.DragonType.BOTH) ? getDragonType(towers) : SpeedrunPractice.config.dragonType;
        if(SpeedrunPractice.config.endTowers.stream().anyMatch(a-> a)) {
            int towerIndex = dragonType.equals(SpeedrunPractice.DragonType.FRONT) ? 9 : 4;
            if (!SpeedrunPractice.config.endTowers.get(towers.get(towerIndex))) {
                int i = 0;
                while (!SpeedrunPractice.config.endTowers.get(towers.get(i))) i++;
                int temp = towers.get(i);
                towers.set(i, towers.get(towerIndex));
                towers.set(towerIndex, temp);
            }
        }
        if(dragonType!=getDragonType(towers)){
            int temp = towers.get(0);
            towers.set(0,towers.get(5));
            towers.set(5,temp);
        }
    }

    private SpeedrunPractice.DragonType getDragonType(List<Integer> towers) {
        return towers.get(0) > towers.get(5) ? SpeedrunPractice.DragonType.FRONT : SpeedrunPractice.DragonType.BACK;
    }
}
