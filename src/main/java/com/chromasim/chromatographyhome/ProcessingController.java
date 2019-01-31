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








    public ProcessingController(Stage mainStage, Scene mainScene, ArrayList<InjectionInfo> injectionDataList) {

        XYChart.Series series = new XYChart.Series<Number,Number>();
        series.getData().add(new XYChart.Data<Number,Number>(3,2));
        series.getData().add(new XYChart.Data<Number,Number>(5,7));
        series.getData().add(new XYChart.Data<Number,Number>(2,5));
        lineChart.getData().add(series);

        lineChart.setTitle("chromatogram");
//        lineChart.getData().add(injectionDataList.get(0).getSeries());
        this.mainStage=mainStage;
        this.mainScene=mainScene;
        processingControllerCounter++;

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
