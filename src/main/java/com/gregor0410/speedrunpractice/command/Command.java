package com.gregor0410.speedrunpractice.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            LiteralArgumentBuilder<ServerCommandSource> inventoryTree = literal("inventory")
                .then(argument("slot", integer(1, 3))
                    .then(literal("select")
                            .executes(Practice::setSlot)
                    ).then(literal("save")
                            .executes(Practice::saveSlot)
                    )
                );
            dispatcher.register(
                literal("practice")
                    .then(literal("end").executes(new EndPractice()).then(inventoryTree).then(argument("seed", LongArgumentType.longArg()).executes(new EndPractice())))
                    .then(literal("nether").executes(new NetherPractice()).then(inventoryTree).then(argument("seed", LongArgumentType.longArg()).executes(new NetherPractice())))
                    .then(literal("overworld").executes(new OverworldPractice()).then(inventoryTree).then(argument("seed", LongArgumentType.longArg()).executes(new OverworldPractice())))
                    .then(literal("postblind").executes(new PostBlindPractice()).then(inventoryTree).then(argument("maxDist",integer(0)).executes(new PostBlindPractice()).then(argument("seed", LongArgumentType.longArg()).executes(new PostBlindPractice()))))
                    .then(literal("world").executes(ctx->{
                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                        player.sendMessage(new LiteralText(player.getServerWorld().getRegistryKey().getValue().toString()),false);
                        return 1;
                    }))
                    .then(literal("seed").executes(Practice::seed).then(argument("seed",LongArgumentType.longArg()).executes(Practice::setSeed)))
            );
            dispatcher.register(literal("instaperch").executes((ctx)->{
                ctx.getSource().getMinecraftServer().getCommandManager().execute(ctx.getSource().withSilent().withLevel(4),"/data merge entity @e[type=ender_dragon,limit=1] {DragonPhase:2}");
                return 1;
            }));
        }));
    }
}
