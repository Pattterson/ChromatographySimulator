package com.chromasim.chromatographyhome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstrumentMethodController {
    @FXML
    private TextField samplingRate;
    @FXML
    private TextField runTime;
    @FXML
    private TextField initialTemp;
    @FXML
    private TextField initialTime;
    @FXML
    private TextField ramp;
    @FXML
    private TextField maxTemp;
    @FXML
    private TextField inletTemp;
    @FXML
    private TextField columnFlow;
    @FXML
    private ChoiceBox<String> columnType;
    @FXML
    private ChoiceBox<String> detectorType;
    @FXML
    private TextField methodName;
    @FXML
    private ListView<String> methodList;


    private InstrumentMethod instrumentMethod;
    private Map<String,InstrumentMethod> allMethodsMap = new HashMap<>();
   final private ObservableMap<String,InstrumentMethod>  allMethodsMapObservable= FXCollections.observableMap(allMethodsMap);



    public InstrumentMethodController(InstrumentMethod instrumentMethod) {
        this.instrumentMethod = instrumentMethod;


    }

    public void initialize() {



        //can use enum
        addFocusListener(samplingRate, TextFieldType.FREQUENCY);
        addFocusListener(runTime, TextFieldType.TIME);
        addFocusListener(initialTemp, TextFieldType.TEMP);
        addFocusListener(initialTime, TextFieldType.TEMP);
        addFocusListener(ramp, TextFieldType.TEMPRAMP);
        addFocusListener(maxTemp, TextFieldType.TEMP);
        addFocusListener(inletTemp, TextFieldType.TEMP);
        addFocusListener(columnFlow, TextFieldType.FLOW);


        columnType.getItems().add("DB-624");
        columnType.getItems().add("HP5-MS");
        detectorType.getItems().add("FID");
        detectorType.getItems().add("MS");
        allMethodsMapObservable.addListener(new MapChangeListener<String, InstrumentMethod>() {
            @Override
            public void onChanged(Change<? extends String, ? extends InstrumentMethod> change) {
                methodList.getItems().add(change.getKey());
            }
        });


    }


    private void addFocusListener(TextField textField, TextFieldType textFieldType) {

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (oldValue && textField.getText().length() > 0) {
                     validateAndCorrectInput(textField, textFieldType);
                }
                ;
            }
        });

    }

    private void validateAndCorrectInput(TextField textField, TextFieldType textFieldType) {
        String text = textField.getText();

        Double textValue = null;
        while (textValue == null) {
            if (text.length() == 0) {
                textField.setText("0.00" + new TextFieldTypeEnum(textFieldType).textFieldString());
                return;
            }
            textValue = parseDouble(text, textValue);
            text = text.substring(0, text.length() - 1);
        }
        System.out.println("value = " + textValue);
        System.out.println(new DecimalFormat("#.##").format(textValue));
        textField.setText(String.format("%.2f", textValue) + new TextFieldTypeEnum(textFieldType).textFieldString());
    }

    private Double parseDouble(String text, Double textValue) {

        try {
            textValue = Double.parseDouble(text);
            return textValue;
        } catch (NumberFormatException e) {
            return null;

        }


    }

    public void saveButtonPressed() {
        try{

            instrumentMethod = new InstrumentMethod();
            instrumentMethod.setSamplingRate(removeTextTail(samplingRate.getText()));
            instrumentMethod.setRunTime(removeTextTail(runTime.getText()));
            instrumentMethod.setInitialTemp(removeTextTail(initialTemp.getText()));
            instrumentMethod.setInitialTime(removeTextTail(initialTime.getText()));
            instrumentMethod.setRamp(removeTextTail(ramp.getText()));
            instrumentMethod.setMaxTemp(removeTextTail(maxTemp.getText()));
            instrumentMethod.setInletTemp(removeTextTail(inletTemp.getText()));
            instrumentMethod.setColumnFlow(removeTextTail(columnFlow.getText()));

            allMethodsMapObservable.put(methodName.getText(),instrumentMethod);


        }
        catch (StringIndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Parameters cannot be left blank");
            alert.show();

        }



    }

    public void loadButtonPressed() {

    }

    private Double removeTextTail(String str){
        int i=0;
       for( i=str.length()-1;i>0;i--){
           if(str.charAt(i)==' '){
               break;
           }
       }

        return Double.parseDouble(str.substring(0,i));
    }


}


enum TextFieldType {
    FREQUENCY, TIME, TEMP, TEMPRAMP,
    FLOW, FRIDAY, SATURDAY;
}


class TextFieldTypeEnum {
    TextFieldType textFieldType;

    // Constructor
    public TextFieldTypeEnum(TextFieldType textFieldType) {
        this.textFieldType = textFieldType;
    }

    // Prints a line about Day using switch
    public String textFieldString() {
        switch (textFieldType) {
            case FREQUENCY:
                return " Hz";

            case TIME:
                return " min";

            case TEMP:
                return " °C";

            case TEMPRAMP:
                return " °C/min";

            case FLOW:
                return " mL/min";

        }
        return "";
    }
}
