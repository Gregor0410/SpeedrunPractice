package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.nbt.Tag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Practice {
    public static int setSlot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        int slot = IntegerArgumentType.getInteger(ctx,"slot");
        String key = ctx.getNodes().get(1).getNode().getName();
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(new LiteralText(String.format("§aSet %s slot to §2§lslot %d", key,slot)),false);
        SpeedrunPractice.config.practiceSlots.put(key,slot-1);
        try {
            SpeedrunPractice.config.save();
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }

    public static int saveSlot(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String key = ctx.getNodes().get(1).getNode().getName();
        int slot = IntegerArgumentType.getInteger(ctx,"slot");
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        player.sendMessage(new LiteralText(String.format("§aSaved current inventory to §2§l%s slot %d",key, slot)),false);
        PlayerInventory inventory = player.inventory;
        ListTag listTag = new ListTag();
        inventory.serialize(listTag);
        SpeedrunPractice.config.practiceInventories.get(key).set(slot-1,listTag.stream().map(Tag::toString).collect(Collectors.toList()));
        try {
            SpeedrunPractice.config.save();
        } catch (IOException e) {
            return 0;
        }
        return 1;
    }

    static void getInventory(ServerPlayerEntity player, String key) {
        List<String> inventoryStringList = SpeedrunPractice.config.practiceInventories.get(key).get(SpeedrunPractice.config.practiceSlots.get(key));
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
    }

    static void resetPlayer(ServerPlayerEntity player) {
        player.setHealth(20f);
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevelClient(5f);
        player.clearStatusEffects();
        player.setVelocity(0,0,0);
    }
}
