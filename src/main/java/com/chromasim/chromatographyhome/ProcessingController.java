package com.chromasim.chromatographyhome;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.ArrayList;

public class ProcessingController {
    private Stage mainStage;
    private Scene mainScene;
    private static int processingControllerCounter=0;

    @FXML
    private LineChart<Number,Number> lineChart;

    private ArrayList<InjectionInfo> injectionDataList;








    public ProcessingController(Stage mainStage, Scene mainScene, ArrayList<InjectionInfo> injectionDataList) {




//        lineChart.getData().add(injectionDataList.get(0).getSeries());
        this.mainStage=mainStage;
        this.mainScene=mainScene;
        this.injectionDataList = injectionDataList;
        processingControllerCounter++;

    }

    public void initialize(){

         XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>(injectionDataList.get(0).getSeries().getData());
        lineChart.getData().add(series);
        System.out.println("fired1");
        lineChart.setTitle("IAMPOTATO");
        System.out.println("fired2");
    }


    public static int getProcessingControllerCounter() {
        return processingControllerCounter;
    }

    public void gotoAcquisitionView(){


            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainChromatograph.fxml"));
                mainStage.setScene(mainScene);
                mainStage.show();





    }
}
