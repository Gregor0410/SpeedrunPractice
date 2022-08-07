package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            LiteralArgumentBuilder<ServerCommandSource> selectTree = literal("select")
                    .then(argument("slot", integer(1, 3)).executes(Practice::setSlot));
            LiteralArgumentBuilder<ServerCommandSource> saveTree = literal("save")
                    .then(argument("slot", integer(1,3)).executes(Practice::saveSlot));
            LiteralArgumentBuilder<ServerCommandSource> inventoryTree = literal("inventory")
                    .then(literal("end").then(selectTree).then(saveTree))
                    .then(literal("nether").then(selectTree).then(saveTree))
                    .then(literal("postblind").then(selectTree).then(saveTree))
                    .then(literal("overworld").then(selectTree).then(saveTree))
                    .then(literal("stronghold").then(selectTree).then(saveTree));
            SuggestionProvider<ServerCommandSource> revertSuggestionsProvider = (commandSource,suggestionBuilder)-> CommandSource.suggestMatching(SpeedrunPractice.autoSaveStater.splitsToUUID.keySet().stream(),suggestionBuilder);
            dispatcher.register(
                literal("practice")
                    .then(literal("end").executes(new EndPractice()).then(argument("seed", LongArgumentType.longArg()).executes(new EndPractice())))
                    .then(literal("stronghold").executes(new StrongholdPractice()).then(argument("seed", LongArgumentType.longArg()).executes(new StrongholdPractice())))
                    .then(literal("nether").executes(new NetherPractice()).then(argument("seed", LongArgumentType.longArg()).executes(new NetherPractice())))
                    .then(literal("overworld")
                        .executes(new OverworldPractice())
                        .then(argument("seed", LongArgumentType.longArg()).executes(new OverworldPractice()))
                        .then(literal("bt").executes(new BuriedTreasurePractice()).then(argument("seed", LongArgumentType.longArg()).executes(new BuriedTreasurePractice())))
                    )
                    .then(literal("postblind").executes(new PostBlindPractice()).then(argument("maxDist",integer(0)).executes(new PostBlindPractice()).then(argument("seed", LongArgumentType.longArg()).executes(new PostBlindPractice()))))
                    .then(literal("world").executes(ctx->{
                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                        player.sendMessage(new LiteralText(player.getServerWorld().getRegistryKey().getValue().toString()),false);
                        return 1;
                    }))
                    .then(literal("seed").executes(Practice::seed).then(argument("seed",LongArgumentType.longArg()).executes(Practice::setSeed)))
                    .then(inventoryTree)
                    .then(literal("revert")
                        .then(argument("split", StringArgumentType.word()).executes(new Revert()).suggests(revertSuggestionsProvider)))
            );
            dispatcher.register(literal("instaperch").executes((ctx)->{
                ctx.getSource().getMinecraftServer().getCommandManager().execute(ctx.getSource().withSilent().withLevel(4),"/data merge entity @e[type=ender_dragon,limit=1] {DragonPhase:2}");
                return 1;
            }));
        }));
    }
}
