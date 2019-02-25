package com.chromasim.chromatographyhome;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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


            instrumentMethod.setSamplingRate(removeTextTail(samplingRate.getText()));
            instrumentMethod.setRunTime(removeTextTail(runTime.getText()));
            instrumentMethod.setInitialTemp(removeTextTail(initialTemp.getText()));
            instrumentMethod.setInitialTime(removeTextTail(initialTime.getText()));
            instrumentMethod.setRamp(removeTextTail(ramp.getText()));
            instrumentMethod.setMaxTemp(removeTextTail(maxTemp.getText()));
            instrumentMethod.setInletTemp(removeTextTail(inletTemp.getText()));
            instrumentMethod.setColumnFlow(removeTextTail(columnFlow.getText()));

            instrumentMethod.setPointsToCollect((int) (instrumentMethod.getRunTime()*60 * instrumentMethod.getSamplingRate()+1));

            //prevent map listener from being fired with duplicate
            if(!allMethodsMapObservable.keySet().contains(methodName.getText())){
                allMethodsMapObservable.put(methodName.getText(),instrumentMethod);
//                pushMethodToDatabase();
            }




        }
        catch (StringIndexOutOfBoundsException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Parameters cannot be left blank");
            alert.show();

        }



    }

    private void pushMethodToDatabase() {
//        MongoClientURI uri  = new MongoClientURI("mongodb://test123:test123@ds113855.mlab.com:13855/sandboxdb");
//        MongoClient mongoClient = new MongoClient(uri);
////        System.out.println(mongoClient.getAddress());
//       MongoDatabase database = mongoClient.getDatabase("sandboxdb");
//        MongoCollection collection = database.getCollection("methods");
//        DBCollection collection2 = database.getCollection("people2");
//        DBCollection collection3 = database.getCollection("data");
//        collection3.createIndex(new BasicDBObject("createdAt",1),new BasicDBObject("expireAfterSeconds",1L));
//        DBObject injection= new BasicDBObject();

//        String line = null;

//        FileReader fileReader = new FileReader("src/chromatogramData.csv");
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        double[][] data = new double[9902][2];
//        int index = 0;
//        while((line = bufferedReader.readLine())!=null){
//            data[index][0] = Double.parseDouble(line.substring(0,2));
//            data[index][1] = Double.parseDouble(line.substring(7,line.length()-1));
//                    index++;
//        }

//        ((BasicDBObject) injection).append("series",data).append("createdAt",new Date());
//        System.out.println("initial: " + collection3.getCount());
//        collection3.insert(injection);
//        while (true){
//            Thread.sleep(1000);
//            System.out.println(collection3.getCount());
//        }


//        BasicDBList list = (BasicDBList) object.get("series");
//        System.out.println(list.toArray()[0]);

//        ArrayList<double[][]> arrayList = (ArrayList) list;
//
//        Person person = new Person("fred",4);
//
//        collection3.remove(new BasicDBObject());
//        collection3.insert(person.toDBObject());
//
//        for(int i=0; i<100; i++){
//            collection3.insert(person.toDBObject());
//            System.out.println(collection3.getCount());
//
//        }
////        System.out.println(collection3.getCount());
//        DBCursor cursor = collection3.find();
//        DBObject object = cursor.one();
////        System.out.println(object.get("name"));
//        collection3.createIndex(new BasicDBObject("createdAt",1),new BasicDBObject("expireAfterSeconds",1L));
//
//        while (true){
//            Thread.sleep(1000);
//            System.out.println(collection3.getCount());
//        }


//        DBCursor cursor = collection3.find();
//
//        Random rand = new Random();
////        DBObject person = new BasicDBObject("_id", rand.nextInt())
////                .append("name", "Jo Bloggs");
//
////collection2.insert(person);
//DBObject seriesRet =  cursor.next();
////        System.out.println(seriesRet);
//        System.out.println(seriesRet.get("series"));
//        BasicDBList list = (BasicDBList) seriesRet.get("series");
//        for(Object element: list){
//            System.out.println(element);
//        }


//collection.remove(person);
//        collection.insert(person);
//        DBObject query = new BasicDBObject("_id", "jo");
//        DBCursor cursor = collection.find(query);
//        DBObject jo = cursor.one();
//        System.out.println((String)cursor.one().get("name"));







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
