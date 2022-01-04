package com.gregor0410.speedrunpractice;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.gregor0410.speedrunpractice.command.Command;
import com.gregor0410.speedrunpractice.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.ceil;

public class SpeedrunPractice implements ModInitializer {
    public static ModConfig config;
    public static Map<StructureFeature<?>, StructureConfig> overworldStructures = Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
    public static Map<StructureFeature<?>, StructureConfig> netherStructures = Maps.newHashMap(StructuresConfig.DEFAULT_STRUCTURES);
    public static List<StructurePoolFeatureConfig> possibleBastionConfigs=new ArrayList<>();
    public static SpeedrunPracticeRandom random = new SpeedrunPracticeRandom();

    static {
        netherStructures.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
    }

    @Override
    public void onInitialize() {
        config = ModConfig.load();
        update();
        Command.registerCommands();
    }

    public static void update() {
        updateStructures();
    }

    public static void updateStructures(){
        //Update Nether structure spacing and separation
        int defaultNetherSpacing = StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.FORTRESS).getSpacing();
        int defaultNetherSeparation = StructuresConfig.DEFAULT_STRUCTURES.get(StructureFeature.FORTRESS).getSeparation();
        int netherSalt = 30084232;
        StructureConfig netherConfig = new StructureConfig((int) ceil(defaultNetherSpacing*config.netherRegionSize), (int) ceil(defaultNetherSeparation*config.netherRegionSize),netherSalt);
        netherStructures.putAll(ImmutableMap.of(StructureFeature.FORTRESS,netherConfig,StructureFeature.BASTION_REMNANT,netherConfig));
        //Update possible bastion types
        possibleBastionConfigs.clear();
        if(config.bridge) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/bridge/start"),60));
        if(config.treasure) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/treasure/starters"),60));
        if(config.stables) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/hoglin_stable/origin"),60));
        if(config.housing) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/units/base"),60));
    }
}
