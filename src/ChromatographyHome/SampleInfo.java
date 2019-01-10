package ChromatographyHome;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleInfo {

    static Integer sampleCounter = 1;
    private int sampleNumber;
    private String sampleName;
    private ComboBox<String> sampleType = new ComboBox<>();
    private Double injectionVolume;
    private List<Compound> sampleCompounds;

    private Button compoundButton = new Button("0");



    public Button getCompoundButton() {
        return compoundButton;
    }

    public void setCompoundButton(Button compoundButton) {
        this.compoundButton = compoundButton;
    }

    public SampleInfo(String sampleName, Double injectionVolume) {
        compoundButton.setOnAction(actionEvent -> {


            Parent compoundAdditionTable = null;
            try {
                compoundAdditionTable = FXMLLoader.load(getClass().getResource("CompoundAdditionTable.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Could not instantiate add Compound Table");
            }
            Scene tableViewScene = new Scene(compoundAdditionTable);

        //This line gets the Stage information
        Stage window = new Stage();

        window.setScene(tableViewScene);
        window.show();

        });

        sampleNumber = sampleCounter;
        sampleCounter++;
        sampleType.getItems().add("Sample");
        sampleType.getItems().add("Standard");
        sampleType.getItems().add("Blank");

        this.sampleName = sampleName;
        this.injectionVolume = injectionVolume;

        sampleCompounds = new ArrayList<>();

    }

    public SampleInfo() {
        sampleNumber = sampleCounter;
        sampleCounter++;
        sampleType.getItems().add("Sample");
        sampleType.getItems().add("Standard");
        sampleType.getItems().add("Blank");
        this.injectionVolume = (double) 5;

        sampleCompounds = new ArrayList<>();

    }


    public static Integer getSampleCounter() {
        return sampleCounter;
    }

    public static void setSampleCounter(Integer sampleCounter) {
        SampleInfo.sampleCounter = sampleCounter;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public ComboBox<String> getSampleType() {
        return sampleType;
    }

    public void setSampleType(ComboBox<String> sampleType) {
        this.sampleType = sampleType;
    }

    public Double getInjectionVolume() {
        return injectionVolume;
    }

    public void setInjectionVolume(Double injectionVolume) {
        this.injectionVolume = injectionVolume;
    }

    public List<Compound> getSampleCompounds() {
        return sampleCompounds;
    }

    public void setSampleCompounds(List<Compound> sampleCompounds) {
        this.sampleCompounds = sampleCompounds;
    }

    public void addCompound(Compound compound) {
        sampleCompounds.add(compound);
    }

    public void removeCompound(Compound compound) {
        sampleCompounds.remove(compound);
    }
}
