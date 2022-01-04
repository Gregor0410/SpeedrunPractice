package com.gregor0410.speedrunpractice;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

//random class with accessible seed
public class SpeedrunPracticeRandom extends Random {
    public final AtomicLong seed;
    private static final long multiplier = 0x5DEECE66DL;
    private static final long addend = 0xBL;
    private static final long mask = (1L << 48) - 1;

    public SpeedrunPracticeRandom(){
        super();
        this.seed = new AtomicLong(new Random().nextLong());
        this.setSeed(seed.get());
    }

    protected int next(int bits) {
        long oldseed, nextseed;
        AtomicLong seed = this.seed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * multiplier + addend) & mask;
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int)(nextseed >>> (48 - bits));
    }


    public long getSeed(){
        return this.seed.get();
    }
}
