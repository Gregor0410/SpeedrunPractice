package com.gregor0410.speedrunpractice;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.gregor0410.speedrunpractice.command.Command;
import com.gregor0410.speedrunpractice.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStreamReader;
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
    public static boolean welcomeShown = false;
    /*private*/ static final Gson gson = new Gson();
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer("speedrun-practice").get();
    private static final String donationLink = "https://ko-fi.com/gregor0410";
    /*private*/ static final Version version = modContainer.getMetadata().getVersion();
    public static AutoSaveStater autoSaveStater = new AutoSaveStater();
    public static SpeedrunIGTInterface speedrunIGTInterface=null;
    private static final UpdateChecker updateChecker = new UpdateChecker();

    static {
        netherStructures.put(StructureFeature.RUINED_PORTAL, new StructureConfig(25, 10, 34222645));
    }


    @Override
    public void onInitialize() {
        try {
            speedrunIGTInterface = new SpeedrunIGTInterface();
        } catch (NoSuchFieldException | ClassNotFoundException ignored) {}
        config = ModConfig.load();
        update();
        Command.registerCommands();
        updateChecker.checkUpdate();
    }

    public static void sendWelcomeMessage(ServerPlayerEntity player) throws IOException, VersionParsingException {
        player.sendMessage(new LiteralText(String.format("[SpeedrunPractice v%s by Gregor0410]",version)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00ff00))),false);
        player.sendMessage(new LiteralText("[Donation Link]")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,donationLink))
                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new LiteralText("Click"))))
                .formatted(Formatting.DARK_GREEN),false);

        if (updateChecker.isOutdatedVersion()) {
            player.sendMessage(new LiteralText(String.format("There is a new version available: v%s", updateChecker.getVersionName())).formatted(Formatting.RED),false);
            player.sendMessage(new LiteralText(String.format("Patch notes:\n%s ", updateChecker.getChangelogs().replace('\r',' ').replace('-','•'))),false);
            player.sendMessage(
                    new LiteralText("Click to download latest version")
                            .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00ff00))
                                    .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,"https://github.com/Gregor0410/SpeedrunPractice/releases/latest"))
                                    .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new LiteralText("Click")))),false);
        } else if (updateChecker.isCheckedUpdate()) {
            player.sendMessage(new LiteralText("You are on the latest version."),false);
        }
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
        if(config.housing) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/units/base"),60));
        if(config.stables) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/hoglin_stable/origin"),60));
        if(config.treasure) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/treasure/starters"),60));
        if(config.bridge) possibleBastionConfigs.add(new StructurePoolFeatureConfig(new Identifier("bastion/bridge/start"),60));

    }

    public enum DragonType{
        FRONT,
        BACK,
        BOTH
    }
}
