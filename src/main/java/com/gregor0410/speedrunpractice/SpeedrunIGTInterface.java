package com.gregor0410.speedrunpractice;

import com.redlimerl.speedrunigt.timer.InGameTimer;
import com.redlimerl.speedrunigt.timer.running.RunType;

import java.lang.reflect.Field;

public class SpeedrunIGTInterface {
    Field activateTicksField;

    public SpeedrunIGTInterface() throws NoSuchFieldException {
        activateTicksField = InGameTimer.class.getDeclaredField("activateTicks");
        activateTicksField.setAccessible(true);
    }

    public void resetTimer(){
        InGameTimer.start("practice-world", RunType.RANDOM_SEED);
    }

    public TimerState getTimerState() {
        InGameTimer timer = InGameTimer.getInstance();
        return new TimerState(timer.getRealTimeAttack(), timer.getTicks());
    }
    public void setTimerState(TimerState timerState) throws IllegalAccessException {
        InGameTimer timer = InGameTimer.getInstance();
        timer.setStartTime(System.currentTimeMillis()-timerState.rta);
        activateTicksField.setInt(timer,(int)timerState.activateTicks);
    }

    public static class TimerState{
        private final long rta;
        private final long activateTicks;

        public TimerState(long rta, long activateTicks){
            this.rta = rta;
            this.activateTicks = activateTicks;
        }
    }
}
