package com.gregor0410.speedrunpractice;

import com.google.common.collect.ImmutableMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import net.minecraft.advancement.Advancement;
import net.minecraft.server.MinecraftServer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class AutoSaveStater {
    public final Map<String,String> splitsToUUID = new HashMap<>();
    private final Map<String, SpeedrunIGTInterface.TimerState> uuidToTimerState = new HashMap<>();
    private final Boolean LOCK=false;
    private static final Map<String,String> criterionToAdvancementId = new ImmutableMap.Builder<String,String>()
            .put("entered_nether","nether/root")
            .put("bastion","nether/find_bastion")
            .put("fortress","nether/find_fortress")
            .put("minecraft:blaze","adventure/kill_all_mobs")
            .put("blaze_rod","nether/obtain_blaze_rod")
            .put("has_ender_eye","recipes/decorations/ender_chest")
            .put("in_stronghold","story/follow_ender_eye")
            .put("entered_end","end/root").build();
    private static final Map<String,String> criterionToSplitName = new ImmutableMap.Builder<String,String>()
            .put("entered_nether","nether")
            .put("bastion","bastion")
            .put("fortress","fortress")
            .put("minecraft:blaze","first_blaze")
            .put("blaze_rod","first_rod")
            .put("has_ender_eye","eye_crafted")
            .put("in_stronghold","stronghold")
            .put("entered_end","end").build();

    public void onGrantCriterion(Advancement advancement, String criterionName, MinecraftServer server){
        if(!FabricLoader.getInstance().isModLoaded("delorean")){
            return;
        }
        else {
            try {
                if(FabricLoader.getInstance().getModContainer("delorean").get().getMetadata().getVersion().compareTo(new SemanticVersionImpl("0.2.10",false))<0){
                    return;
                }
            } catch (VersionParsingException e) {
                e.printStackTrace();
            }
        }
        if(advancement.getId().getPath().equals(criterionToAdvancementId.get(criterionName))){
            String uuid = UUID.randomUUID().toString();
            splitsToUUID.put(criterionToSplitName.get(criterionName), uuid);
            server.execute(()->{
                try {
                    saveState(uuid,server);
                } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | InterruptedException | InvocationTargetException | NoSuchMethodException ignored) {}
            });
        }
    }
    private void saveState(String uuid,MinecraftServer server) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InterruptedException, InvocationTargetException, NoSuchMethodException {
        if(!FabricLoader.getInstance().isModLoaded("delorean")){
            return;
        }
        Class<?> delorean = Class.forName("me.logwet.delorean.DeLorean");
        AtomicReference<String> TRIGGER_SAVE_ID = (AtomicReference) delorean.getDeclaredField("TRIGGER_SAVE_ID").get(null);
        AtomicBoolean TRIGGER_SAVE = (AtomicBoolean) delorean.getDeclaredField("TRIGGER_SAVE").get(null);
        new Thread(()-> {
            synchronized (TRIGGER_SAVE) {
                while (TRIGGER_SAVE.get()) {
                    try {
                        TRIGGER_SAVE.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            TRIGGER_SAVE_ID.set(uuid);
            TRIGGER_SAVE.set(true);
        }).start();
        if(SpeedrunPractice.speedrunIGTInterface!=null){
            this.uuidToTimerState.put(uuid,SpeedrunPractice.speedrunIGTInterface.getTimerState());
        }
    }

    public boolean revertToSplit(String splitName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, InterruptedException, InvocationTargetException, NoSuchMethodException {
        String uuid = splitsToUUID.get(splitName);
        if(uuid==null){
            return false;
        }
        Class<?> delorean = Class.forName("me.logwet.delorean.DeLorean");
        AtomicReference<String> TRIGGER_LOAD_ID = (AtomicReference) delorean.getDeclaredField("TRIGGER_LOAD_ID").get(null);
        AtomicBoolean TRIGGER_LOAD = (AtomicBoolean) delorean.getDeclaredField("TRIGGER_LOAD").get(null);
        AtomicBoolean TRIGGER_SAVE = (AtomicBoolean) delorean.getDeclaredField("TRIGGER_SAVE").get(null);
        new Thread(()-> {
            try {
                synchronized (TRIGGER_SAVE) {
                    while (TRIGGER_SAVE.get()) {
                        TRIGGER_SAVE.wait();
                    }
                }
                synchronized (TRIGGER_LOAD) {
                    while (TRIGGER_LOAD.get()) {
                        TRIGGER_LOAD.wait();
                    }
                }
            }catch (InterruptedException ignored){}
            TRIGGER_LOAD_ID.set(uuid);
            TRIGGER_LOAD.set(true);
        }).start();
        if(SpeedrunPractice.speedrunIGTInterface!=null){
            SpeedrunPractice.speedrunIGTInterface.setTimerState(this.uuidToTimerState.get(uuid));
        }
        return true;
    }

    public void deleteAllStates(){
        if(!FabricLoader.getInstance().isModLoaded("delorean"))return;
        try {
            Class<?> delorean = Class.forName("me.logwet.delorean.DeLorean");
            AtomicReference<String> TRIGGER_DELETE_ID = (AtomicReference) delorean.getDeclaredField("TRIGGER_DELETE_ID").get(null);
            AtomicBoolean TRIGGER_DELETE = (AtomicBoolean) delorean.getDeclaredField("TRIGGER_DELETE").get(null);
            for (String uuid : this.splitsToUUID.values()) {
                new Thread(()-> {
                    synchronized (TRIGGER_DELETE) {
                        while (TRIGGER_DELETE.get()) {
                            try {
                                TRIGGER_DELETE.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        TRIGGER_DELETE_ID.set(uuid);
                        TRIGGER_DELETE.set(true);
                    }
                }).start();
            }
        }
        catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
        }
        this.splitsToUUID.clear();
        this.uuidToTimerState.clear();
    }
}
