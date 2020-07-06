package com.chromasim.chromatographyhome;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class WelcomeScreenController {
    @FXML private ImageView logo;



    public WelcomeScreenController(){

    }

    //Image reference can be added to FXML
    public void init(){
        URL url = getClass().getResource("/ChromaSimLogo.png");
        InputStream input = null;
        try {
            input = url.openStream();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image image = new Image(input);

        logo.setImage(image);
    }


}
