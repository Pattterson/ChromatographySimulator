package ChromatographyHome;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

public class MolecularEditorController {

    @FXML
    WebView webView;
    @FXML
    Button closeButton;

    public void initialize(){
        File file = new File("./src/ChromatographyHome/MolecularEditorResources/jme_examples/jme_window.html");
        System.out.println(file.exists());
        WebEngine engine = webView.getEngine();
        engine.load(file.toURI().toString());


    }

    public void closeScene(){
        Stage stage =(Stage) closeButton.getScene().getWindow();
        stage.close();
    }



}
