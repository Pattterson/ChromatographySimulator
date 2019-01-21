package com.chromasim.chromatographyhome;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class NoiseGenerator {
    //No need to instantiate;
    private NoiseGenerator(){};

    static int generateNoise(){
        int min = 20;
        int max = 25;
        Random rand = new Random();



        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1) + (int) (5*rand.nextDouble());;

        return randomNum;

    }



}
