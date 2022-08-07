package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;

public class OverworldPractice implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        long seed;
        try{
            seed = ctx.getArgument("seed",long.class);
        }catch(IllegalArgumentException e){
            seed = SpeedrunPractice.random.nextLong();
        }
        Practice.linkedPracticeWorldPractice(ctx,seed,true,false,false, ServerWorld::getSpawnPos,"overworld");
        return 1;
    }

}

