package com.chromasim.chromatographyhome;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NoiseGenerator {
    //No need to instantiate;
    private NoiseGenerator(){}

    static double generateNoise(){
        Random rand = new Random();

        return rand.nextGaussian()*2+5;

    }



}
