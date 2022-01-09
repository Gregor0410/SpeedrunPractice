package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;

import java.lang.reflect.InvocationTargetException;

public class Revert implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String split = ctx.getArgument("split",String.class);
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        try {
            if(!SpeedrunPractice.autoSaveStater.revertToSplit(split)) {
                player.sendMessage(new LiteralText(String.format("No save state exists for split %s", split)), false);
                return -1;
            }
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            player.sendMessage(new LiteralText("An error occured - Delorean is probably not installed.").formatted(Formatting.RED),false);
            e.printStackTrace();
            return -1;
        } catch (InterruptedException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return 1;
    }
}
