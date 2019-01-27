package com.chromasim.chromatographyhome;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainChromatograph.fxml"));
        primaryStage.setTitle("Chromatography Simulator 2019");
        Scene scene = new Scene(root, 1350, 825);

        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        primaryStage.show();

        //test comment2



    }


    public static void main(String[] args) {
        launch(args);
    }
}
