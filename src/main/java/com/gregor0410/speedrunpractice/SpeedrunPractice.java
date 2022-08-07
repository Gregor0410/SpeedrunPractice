package com.gregor0410.speedrunpractice;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.gregor0410.ptlib.PTLib;
import com.gregor0410.ptlib.rng.AccessibleRandom;
import com.gregor0410.speedrunpractice.command.Command;
import com.gregor0410.speedrunpractice.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.world.gen.chunk.StructureConfig;
import net.minecraft.world.gen.chunk.StructuresConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpeedrunPractice implements ModInitializer {
    public static ModConfig config;
    public static AccessibleRandom random = new AccessibleRandom();
    public static boolean welcomeShown = false;
    /*private*/ static final Gson gson = new Gson();
    private static final ModContainer modContainer = FabricLoader.getInstance().getModContainer("speedrun-practice").get();
    private static final String donationLink = "https://ko-fi.com/gregor0410";
    /*private*/ static final Version version = modContainer.getMetadata().getVersion();
    public static AutoSaveStater autoSaveStater = new AutoSaveStater();
    public static SpeedrunIGTInterface speedrunIGTInterface=null;
    private static final UpdateChecker updateChecker = new UpdateChecker();


    @Override
    public void onInitialize() {
        if(FabricLoader.getInstance().isModLoaded("speedrunigt")){
            try {
                speedrunIGTInterface = new SpeedrunIGTInterface();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        config = ModConfig.load();
        PTLib.setConfig(config.ptConfig);
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


    public enum DragonType{
        FRONT,
        BACK,
        BOTH
    }
}
