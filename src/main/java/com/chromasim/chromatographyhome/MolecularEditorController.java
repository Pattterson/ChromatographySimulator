package com.chromasim.chromatographyhome;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

public class MolecularEditorController {

    @FXML
    WebView webView;
    @FXML
    Button closeButton;

    public void initialize() throws URISyntaxException {
        URL url = getClass().getResource("/MolecularEditorResources/jsme_chromasim.html");

        WebEngine engine = webView.getEngine();
        engine.load(url.toURI().toString());

        //listen for state change (smiles)
        engine.getLoadWorker().stateProperty().addListener((ov, o, n) -> {
            if (Worker.State.SUCCEEDED == n) {
                engine.setOnStatusChanged(webEvent -> {

                    //Call value change
                    onValueChange(webEvent.getData());
                });
            }
        });


    }


    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


    private void onValueChange(String data) {

        //Print out data
        System.out.println(data);

        //If the data is equal to "exit", close the program
        if ("exit".equals(data)) {

            //Print goodbye message
            System.out.println("Received exit command! Goodbye :D");

            //Exit
            System.exit(0);
        }


    }
}
