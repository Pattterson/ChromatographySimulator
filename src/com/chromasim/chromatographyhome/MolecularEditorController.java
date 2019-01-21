package com.chromasim.chromatographyhome;

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
        URL url = getClass().getResource("MolecularEditorResources/jme_examples/jme_window.html");

        WebEngine engine = webView.getEngine();
        engine.load(url.toURI().toString());


    }

    public void closeScene(){
        Stage stage =(Stage) closeButton.getScene().getWindow();
        stage.close();
    }



}
