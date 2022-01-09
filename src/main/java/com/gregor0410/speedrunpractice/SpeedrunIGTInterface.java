package com.gregor0410.speedrunpractice;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class SpeedrunIGTInterface {
    private final Class<?> timer;
    private final Field startTimeField;
    private final Field activateTicksField;

    public SpeedrunIGTInterface() throws NoSuchFieldException, ClassNotFoundException {
        timer = Class.forName("com.redlimerl.speedrunigt.timer.InGameTimer");
        startTimeField = timer.getDeclaredField("startTime");
        activateTicksField = timer.getDeclaredField("activateTicks");
        startTimeField.setAccessible(true);
        activateTicksField.setAccessible(true);
    }
    public TimerState getTimerState() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object instance = timer.getMethod("getInstance").invoke(null);
        long startTime = startTimeField.getLong(instance);
        long activateTicks = activateTicksField.getLong(instance);
        return new TimerState(startTime, activateTicks);
    }
    public void setTimerState(TimerState timerState) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object instance = timer.getMethod("getInstance").invoke(null);
        startTimeField.setLong(instance,timerState.startTime);
        activateTicksField.setLong(instance,timerState.activateTicks);
    }

    public static class TimerState{
        private final long startTime;
        private final long activateTicks;

        public TimerState(long startTime, long activateTicks){
            this.startTime = startTime;
            this.activateTicks = activateTicks;
        }
    }
}
