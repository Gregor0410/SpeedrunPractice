package com.gregor0410.speedrunpractice.command;

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
                    .then(literal("set")
                            .executes(Practice::setSlot)
                    ).then(literal("save")
                            .executes(Practice::saveSlot)
                    )
                );
            dispatcher.register(
                literal("practice")
                    .then(literal("end").executes(new EndPractice()).then(inventoryTree))
                    .then(literal("nether").executes(new NetherPractice()).then(inventoryTree))
                    .then(literal("world").executes(ctx->{
                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                        player.sendMessage(new LiteralText(player.getServerWorld().getRegistryKey().getValue().toString()),false);
                        return 1;
                    }))
            );
        }));
    }
}
