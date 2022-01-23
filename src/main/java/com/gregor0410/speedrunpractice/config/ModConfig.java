package com.gregor0410.speedrunpractice.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.BooleanListEntry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModConfig{
    public Boolean stables=true;
    public Boolean bridge=true;
    public Boolean treasure=true;
    public Boolean housing=true;
    public List<Boolean> endTowers = Lists.newArrayList(true,true,true,true,true,true,true,true,true,true);
    public static final List<String> DEFAULTENDINVENTORY;
    public static final List<String> DEFAULTNETHERINVENTORY;
    public static final List<String> DEFAULTPOSTBLINDINVENTORY;
    public Map<String,List<List<String>>> practiceInventories =new HashMap<>(ImmutableMap.of(
            "end",Lists.newArrayList(DEFAULTENDINVENTORY,new ArrayList<>(),new ArrayList<>()),
            "nether",Lists.newArrayList(DEFAULTNETHERINVENTORY,new ArrayList<>(),new ArrayList<>()),
            "overworld",Lists.newArrayList(new ArrayList<>(),new ArrayList<>(),new ArrayList<>()),
            "postblind",Lists.newArrayList(DEFAULTPOSTBLINDINVENTORY,new ArrayList<>(),new ArrayList<>())));
    public Map<String,Integer> practiceSlots = new HashMap<>(ImmutableMap.of("end", 0, "nether", 0,"postblind",0,"overworld",0));
    public float netherRegionSize=1;
    public int bastionRarity = 60;
    public int defaultMaxDist = 1000;
    public boolean calcMode = true;
    public boolean deletePracticeWorlds = true;
    public boolean postBlindSpawnChunks =false;
    public boolean caveSpawns=true;
    public boolean randomisePostBlindInventory=true;
    public boolean eliminateCageSpawns = false;
    public SpeedrunPractice.DragonType dragonType = SpeedrunPractice.DragonType.BOTH;

    public static ModConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("speedrun-practice.json");
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(path);
            Gson gson = new Gson();
            ModConfig config = gson.fromJson(reader,ModConfig.class);
            reader.close();
            return config;
        } catch (IOException e) {
            return new ModConfig();
        }
    }

    public Screen getScreen(Screen parent){
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDoesConfirmSave(false)
                .setTransparentBackground(true)
                .setTitle(new TranslatableText("speedrun-practice.options"))
                .setSavingRunnable(()->{
                    try {
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("speedrun-practice.options.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("speedrun-practice.options.nether_region_size"),(int)(netherRegionSize*100),7,200)
                .setDefaultValue(100)
                .setSaveConsumer(a->netherRegionSize=(float)a/100)
                .setTextGetter(a->new LiteralText(String.format("%d %%",a)))
                .build());
        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("speedrun-practice.options.bastion_rarity"),bastionRarity,0,100)
                .setDefaultValue(60)
                .setTextGetter(a->new LiteralText(String.format("%d %%",a)))
                .setSaveConsumer(a->bastionRarity=a)
                .setTooltip(new TranslatableText("speedrun-practice.options.bastion_rarity_tooltip"))
                .build());
        general.addEntry(entryBuilder.startIntField(new TranslatableText("speedrun-practice.options.max_dist"),defaultMaxDist)
                .setDefaultValue(1000)
                .setMin(0)
                .setSaveConsumer(a->defaultMaxDist=a)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.randomisePostBlindInventory"),randomisePostBlindInventory)
                .setDefaultValue(true)
                .setSaveConsumer(a->randomisePostBlindInventory=a)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.deletePracticeWorlds"),deletePracticeWorlds)
                .setDefaultValue(true)
                .setSaveConsumer(a->deletePracticeWorlds=a)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.postBlindSpawnChunks"),postBlindSpawnChunks)
                .setDefaultValue(false)
                .setSaveConsumer(a->postBlindSpawnChunks=a)
                .setTooltip(new TranslatableText("speedrun-practice.options.postBlindSpawnChunks.tooltip1"),new TranslatableText("speedrun-practice.options.postBlindSpawnChunks.tooltip2"))
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.postBlindCaveSpawns"),caveSpawns)
                .setDefaultValue(true)
                .setSaveConsumer(a->caveSpawns=a)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.calc_mode"),calcMode)
                .setDefaultValue(true)
                .build());
        general.addEntry(entryBuilder.startSubCategory(new TranslatableText("speedrun-practice.options.bastions"), Lists.newArrayList(
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.housing"),housing)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->housing=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.stables"),stables)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->stables=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.treasure"),treasure)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->treasure=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.bridge"),bridge)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->bridge=a)
                        .build())).build());
        general.addEntry(entryBuilder.startSubCategory(new TranslatableText("speedrun-practice.options.end"), Lists.newArrayList(
                entryBuilder.startEnumSelector(new TranslatableText("speedrun-practice.options.dragon_type"),SpeedrunPractice.DragonType.class,dragonType)
                        .setDefaultValue(SpeedrunPractice.DragonType.BOTH)
                        .setSaveConsumer(a->dragonType=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.eliminate_cage_spawns"),eliminateCageSpawns)
                        .setDefaultValue(true)
                        .setSaveConsumer(a->eliminateCageSpawns=a)
                        .build(),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.small_boy",0),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.small_cage",1),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.tall_cage",2),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.m85",3),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.m88",4),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.m91",5),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.t94",6),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.t97",7),
                getEndTower(entryBuilder, "speedrun-practice.options.towers.t100",8),
                getEndTower(entryBuilder,"speedrun-practice.options.towers.t103",9)
        )).build());

        return builder.build();
    }

    @NotNull
    private BooleanListEntry getEndTower(ConfigEntryBuilder entryBuilder, String text, int tower) {
        return entryBuilder.startBooleanToggle(new TranslatableText(text), endTowers.get(tower))
                .setDefaultValue(true)
                .setYesNoTextSupplier(a -> a ? ScreenTexts.ON : ScreenTexts.OFF)
                .setSaveConsumer(a ->endTowers.set(tower,a))
                .build();
    }

    public void save() throws IOException {
        SpeedrunPractice.update();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path path = FabricLoader.getInstance().getConfigDir().resolve("speedrun-practice.json");
        Files.createDirectories(path.getParent());
        BufferedWriter writer = Files.newBufferedWriter(path);
        gson.toJson(this,writer);
        writer.close();
    }

    static{
        DEFAULTENDINVENTORY=Lists.newArrayList("{Slot:0b,id:\"minecraft:iron_axe\",Count:1b,tag:{Damage:0}}",
                "{Slot:1b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:2b,id:\"minecraft:iron_pickaxe\",Count:1b,tag:{Damage:0}}",
                "{Slot:3b,id:\"minecraft:water_bucket\",Count:1b}",
                "{Slot:4b,id:\"minecraft:ender_pearl\",Count:4b}",
                "{Slot:5b,id:\"minecraft:respawn_anchor\",Count:4b}",
                "{Slot:6b,id:\"minecraft:glowstone\",Count:4b}",
                "{Slot:7b,id:\"minecraft:crying_obsidian\",Count:64b}",
                "{Slot:8b,id:\"minecraft:cobblestone\",Count:64b}",
                "{Slot:9b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:10b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:11b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:12b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:13b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:14b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:15b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:16b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:17b,id:\"minecraft:white_bed\",Count:1b}",
                "{Slot:27b,id:\"minecraft:bow\",Count:1b,tag:{Damage:0}}",
                "{Slot:28b,id:\"minecraft:arrow\",Count:64b}");
        DEFAULTNETHERINVENTORY=Lists.newArrayList("{Slot:0b,id:\"minecraft:iron_axe\",Count:1b,tag:{Damage:0}}",
                "{Slot:1b,id:\"minecraft:iron_shovel\",Count:1b,tag:{Damage:0}}",
                "{Slot:2b,id:\"minecraft:iron_pickaxe\",Count:1b,tag:{Damage:0}}",
                "{Slot:3b,id:\"minecraft:flint_and_steel\",Count:1b,tag:{Damage:0}}",
                "{Slot:4b,id:\"minecraft:oak_boat\",Count:1b}",
                "{Slot:5b,id:\"minecraft:bread\",Count:5b}",
                "{Slot:6b,id:\"minecraft:crafting_table\",Count:1b}",
                "{Slot:7b,id:\"minecraft:lava_bucket\",Count:1b}",
                "{Slot:8b,id:\"minecraft:oak_planks\",Count:20b}",
                "{Slot:27b,id:\"minecraft:stick\",Count:2b}");
        DEFAULTPOSTBLINDINVENTORY=Lists.newArrayList("{Slot:0b,id:\"minecraft:iron_axe\",Count:1b,tag:{Damage:0}}",
                "{Slot:1b,id:\"minecraft:iron_shovel\",Count:1b,tag:{Damage:0}}",
                "{Slot:2b,id:\"minecraft:iron_pickaxe\",Count:1b,tag:{Damage:0}}",
                "{Slot:3b,id:\"minecraft:flint_and_steel\",Count:1b,tag:{Damage:0}}",
                "{Slot:4b,id:\"minecraft:ender_pearl\",Count:16b}",
                "{Slot:5b,id:\"minecraft:ender_eye\",Count:12b}",
                "{Slot:6b,id:\"minecraft:crafting_table\",Count:1b}",
                "{Slot:7b,id:\"minecraft:water_bucket\",Count:1b}",
                "{Slot:8b,id:\"minecraft:nether_bricks\",Count:32b}",
                "{Slot:9b,id:\"minecraft:string\",Count:64b}",
                "{Slot:10b,id:\"minecraft:glowstone\",Count:8b}",
                "{Slot:11b,id:\"minecraft:crying_obsidian\",Count:32b}",
                "{Slot:12b,id:\"minecraft:oak_planks\",Count:32b}",
                "{Slot:13b,id:\"minecraft:oak_boat\",Count:1b}",
                "{Slot:100b,id:\"minecraft:iron_boots\",Count:1b,tag:{Damage:0,Enchantments:[{lvl:3s,id:\"minecraft:soul_speed\"}]}}",
                "{Slot:103b,id:\"minecraft:golden_helmet\",Count:1b,tag:{Damage:0}}",
                "{Slot:-106b,id:\"minecraft:bread\",Count:5b}");
    }
}
