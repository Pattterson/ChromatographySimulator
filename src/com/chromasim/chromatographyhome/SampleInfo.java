package com.chromasim.chromatographyhome;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;


//Each sample (characterized by a SampleInfo Object) contains both sample metadata (sample number, name, injection volume, etc...)
//and a list of zero or more compounds.  Compounds represent the specific molecules that make up the sample and are what will determine
//chromatographic profiles.

public class SampleInfo {

    static Integer sampleCounter = 1;
    private int sampleNumber;
    private String sampleName;
    private ComboBox<Label> sampleType = new ComboBox<>();
    private Double injectionVolume;
    private ObservableList<Compound> sampleCompounds = FXCollections.observableArrayList();

    private Button compoundButton = new Button("0");




    public Button getCompoundButton() {
        return compoundButton;
    }

    public void setCompoundButton(Button compoundButton) {
        this.compoundButton = compoundButton;
    }

    public SampleInfo(String sampleName, Double injectionVolume) {
        IntegerBinding sampleCompoundsSizeProperty = Bindings.size(sampleCompounds);

        compoundButton.textProperty().bind(sampleCompoundsSizeProperty.asString());
        compoundButton.setOnAction(actionEvent -> {

            ObservableList<Compound> dummyList = FXCollections.observableArrayList();



            FXMLLoader loader = new FXMLLoader(getClass().getResource("CompoundAdditionTable.fxml"));
            CompoundAdditionTableController controller = new CompoundAdditionTableController(sampleCompounds);
            loader.setController(controller);

            try {
                Scene compoundTableScene = new Scene(loader.load(),600,300);
                Stage window = new Stage();

                window.setScene(compoundTableScene);
                window.show();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Unable to load Compound table view scene");
            }
        });


        sampleNumber = sampleCounter;
        sampleCounter++;
        Label label = new Label("Standard");
                label.setEllipsisString("");

        Label label2 = new Label("Sample");
        label.setEllipsisString("");

        Label label3 = new Label("Blank");
        label.setEllipsisString("");



        sampleType.getItems().add(label2);
        sampleType.getItems().add(label);
        sampleType.getItems().add(label3);

        this.sampleName = sampleName;
        this.injectionVolume = injectionVolume;

    }

    public SampleInfo() {
        sampleNumber = sampleCounter;
        sampleCounter++;
//        sampleType.getItems().add("Sample");
//        sampleType.getItems().add("Standard");
//        sampleType.getItems().add("Blank");
        this.injectionVolume = (double) 5;




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

    public ComboBox<Label> getSampleType() {
        return sampleType;
    }

    public void setSampleType(ComboBox<Label> sampleType) {
        this.sampleType = sampleType;
    }

    public Double getInjectionVolume() {
        return injectionVolume;
    }

    public void setInjectionVolume(Double injectionVolume) {
        this.injectionVolume = injectionVolume;
    }

    public ObservableList<Compound> getSampleCompounds() {
        return sampleCompounds;
    }

    public void setSampleCompounds(ObservableList<Compound> sampleCompounds) {
        this.sampleCompounds = sampleCompounds;
    }

    public void addCompound(Compound compound) {
        sampleCompounds.add(compound);
    }

    public void removeCompound(Compound compound) {
        sampleCompounds.remove(compound);
    }
}
