package com.chromasim.chromatographyhome;

import com.sun.javafx.application.PlatformImpl;
import de.codecentric.centerdevice.javafxsvg.SvgImageLoaderFactory;
import de.codecentric.centerdevice.javafxsvg.dimension.PrimitiveDimensionProvider;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        SvgImageLoaderFactory.install(new PrimitiveDimensionProvider());
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainChromatograph.fxml"));
        primaryStage.setTitle("Chromatography Simulator 2019");
        Scene scene = new Scene(root, 1350, 825);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Main.class.getResource("/styles.css").toExternalForm());
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();


        });
        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
