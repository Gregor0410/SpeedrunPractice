package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.IMinecraftServer;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class EndPractice implements Command<ServerCommandSource> {
    public static int setSlot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int slot = IntegerArgumentType.getInteger(ctx,"slot");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(new LiteralText(String.format("Set slot to slot %d", slot)),false);
        SpeedrunPractice.config.endPracticeSlot=slot-1;
        try {
            SpeedrunPractice.config.save();
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }

    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        List<String> inventoryStringList = SpeedrunPractice.config.endPracticeInventories.get(SpeedrunPractice.config.endPracticeSlot);
        if(inventoryStringList!=null) {
            List<CompoundTag> inventoryTagList = inventoryStringList.stream().map(tag -> {
                try {
                    return StringNbtReader.parse(tag);
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            ListTag listTag = new ListTag();
            listTag.addAll(inventoryTagList);
            player.inventory.deserialize(listTag);
        }
        MinecraftServer server = context.getSource().getMinecraftServer();
        ServerWorld world = null;
        try {
            world = ((IMinecraftServer)server).createEndPracticeWorld();
            ServerWorld.createEndSpawnPlatform(world);
            player.setHealth(20f);
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevelClient(5f);
            player.clearStatusEffects();
            player.setVelocity(0,0,0);
            player.teleport(world,100,49,0,90,0);
            return 1;
        } catch (IOException e) {
            return 0;
        }
    }

    public static int saveSlot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int slot = IntegerArgumentType.getInteger(ctx,"slot");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(new LiteralText(String.format("%d", slot)),false);
        PlayerInventory inventory = player.inventory;
        ListTag listTag = new ListTag();
        inventory.serialize(listTag);
        SpeedrunPractice.config.endPracticeInventories.set(slot-1,listTag.stream().map(Tag::toString).collect(Collectors.toList()));
        try {
            SpeedrunPractice.config.save();
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }
}
