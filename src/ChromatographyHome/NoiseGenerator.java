package ChromatographyHome;

import java.util.concurrent.ThreadLocalRandom;

public class NoiseGenerator {
    //No need to instantiate;
    private NoiseGenerator(){};

    static int generateNoise(){
        int min = 20;
        int max = 25;

        int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);

        return randomNum;

    }



}
