package com.gregor0410.speedrunpractice.command;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Command {
    public static void registerCommands(){
        CommandRegistrationCallback.EVENT.register(((dispatcher, dedicated) -> {
            dispatcher.register(
                literal("practice")
                        .then(literal("end").executes(new EndPractice())
                                .then(literal("inventory")
                                        .then(argument("slot", integer(1,3))
                                                .then(literal("set")
                                                    .executes(EndPractice::setSlot)
                                                ).then(literal("save")
                                                        .executes(EndPractice::saveSlot)
                                                )
                                        )
                                )
                        )
            );
        }));
    }
}
