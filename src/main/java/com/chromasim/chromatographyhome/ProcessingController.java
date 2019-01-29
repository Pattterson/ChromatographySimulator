package com.chromasim.chromatographyhome;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ProcessingController {
    private Stage mainStage;
    private Scene mainScene;
    private static int processingControllerCounter=0;

    public ProcessingController(Stage mainStage, Scene mainScene) {
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
