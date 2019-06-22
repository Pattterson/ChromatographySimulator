package com.chromasim.chromatographyhome;

import java.util.ArrayList;
import java.util.List;

//helper class which permits a list of injectionInfos (injections) to be stored together as one in database
public class SampleSet {
    private List<InjectionInfo> injectionList;

    public SampleSet(List<InjectionInfo> injectionList){
        this.injectionList = new ArrayList<>();
        this.injectionList = injectionList;


    }

}
