package com.gregor0410.speedrunpractice.command;

import com.gregor0410.speedrunpractice.SpeedrunPractice;
import com.gregor0410.speedrunpractice.mixin.ServerPlayerEntityAccess;
import com.gregor0410.speedrunpractice.world.PracticeWorld;
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
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
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

    public static void startSpeedrunIGTTimer(){
        try {
            Class<?> timerClass = Class.forName("com.redlimerl.speedrunigt.timer.InGameTimer");
            Method startMethod = timerClass.getMethod("start");
            startMethod.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored){}
    }


    public static void getInventory(ServerPlayerEntity player, String key) {
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
        player.setExperienceLevel(0);
        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevelClient(5f);
        player.clearStatusEffects();
        player.setVelocity(Vec3d.ZERO);
        ((ServerPlayerEntityAccess)player).setSeenCredits(false);
    }

    public static int seed(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        String seed= String.valueOf(SpeedrunPractice.random.getSeed());
        Text text = Texts.bracketed((new LiteralText(seed)).styled((style) -> {
            return style.withColor(Formatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(seed))).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.copy.click"))).withInsertion(String.valueOf(seed));
        }));
        ctx.getSource().getPlayer().sendMessage(text,false);
        return 1;
    }

    public static int setSeed(CommandContext<ServerCommandSource> ctx) {
        SpeedrunPractice.random.seed.set(ctx.getArgument("seed",long.class));
        return 1;
    }

    static void createPortals(Map<RegistryKey<DimensionType>, PracticeWorld> linkedPracticeWorld, ServerPlayerEntity player, PracticeWorld overworld, BlockPos overworldPos) {
        BlockPos prevPos = player.getBlockPos();
        player.refreshPositionAndAngles(new BlockPos(overworldPos.getX()/8D, overworldPos.getY(), overworldPos.getZ()/8D),90,0);
        player.setInNetherPortal(overworldPos);
        linkedPracticeWorld.get(DimensionType.THE_NETHER_REGISTRY_KEY).getPortalForcer().createPortal(player);
        player.refreshPositionAndAngles(overworldPos,90,0);
        overworld.getPortalForcer().createPortal(player);
        player.refreshPositionAndAngles(prevPos,90,0);
        player.netherPortalCooldown = player.getDefaultNetherPortalCooldown();
    }

    public static void setSpawnPos(PracticeWorld overworld, ServerPlayerEntity player) {
        ServerPlayerEntityAccess playerAccess = (ServerPlayerEntityAccess) player;
        playerAccess.setSpawnPointDimension(overworld.getRegistryKey());
        playerAccess.setSpawnPointPosition(null);
        playerAccess.setSpawnPointSet(false);
    }
}
