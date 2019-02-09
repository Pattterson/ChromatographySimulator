package com.chromasim.chromatographyhome;


import com.sun.javafx.stage.StageHelper;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;

//for a second stage (for reference)
//public void changeScreenButtonPushed(ActionEvent event) throws IOException
//        {
//        Parent tableViewParent = FXMLLoader.load(getClass().getResource("ExampleOfTableView.fxml"));
//        Scene tableViewScene = new Scene(tableViewParent);
//
//        //This line gets the Stage information
//        Stage window = new Stage();
//
//        window.setScene(tableViewScene);
//        window.show();
//        }
public class Controller {
    //must be updated before deploying to getclass.getresource();
    private static File file;
    private static String MEDIA_URI;
    private static Media sound;
    private Stage mainStage;
    private boolean acquisitionViewShowing = true;
    private Scene processingScene;
    private ArrayList<InjectionInfo> injectionDataList = new ArrayList<>();
    private LineChart<Number,Number> lineChart;


    @FXML
    private Label speedIndicator;

    private Task<ObservableList<XYChart.Data>> task;
    private static MediaPlayer mediaPlayer;
    private ObservableList<IntegrationEvent> eventsList = FXCollections.observableArrayList();
    private ObservableList<SampleInfo> sampleList = FXCollections.observableArrayList();

    @FXML
    private Button startButton;

    @FXML
    private Slider speedSlider;


    @FXML
    private TableView<IntegrationEvent> eventsTable;

    @FXML
    private TableColumn<IntegrationEvent, ComboBox> eventColumn;

    @FXML
    private TableColumn<IntegrationEvent, String> eventStartColumn;

    @FXML
    private TableColumn<IntegrationEvent, String> eventEndColumn;

    @FXML
    private TableColumn<SampleInfo, Integer> sampleNumberColumn;

    @FXML
    private TableColumn<SampleInfo, String> sampleNameColumn;

    @FXML
    private TableColumn<SampleInfo, ComboBox> sampleTypeColumn;

    @FXML
    private TableColumn<SampleInfo, String> injectionVolumeColumn;

    @FXML
     TableView<SampleInfo> sampleTable;

    @FXML
    private Button newSampleButton;
    @FXML
    private Button deleteSampleButton;
    @FXML
    private Button newEventButton;
    @FXML
    private Button deleteEventButton;
    @FXML
    private TableColumn<SampleInfo, Button> compoundsColumn;

    @FXML
    private GridPane chartContainer;

    @FXML
    private LineChartController lineChartController;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private TableColumn<IntegrationEvent,String> eventValueColumn;

    @FXML
    Label progressIndicatorText;


    private InjectionInfo injectionInfo;






    int injectionNumber = 1;



    public void initialize() {

        lineChartController.setController(this);
        chartContainer = lineChartController.getChartContainer();
        lineChart = lineChartController.getLineChart();

        URL url = getClass().getResource("/music.wav");
        try {
            Media sound = new Media(url.toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //initialize integration table
        Label placeholder = new Label("Integration events will be available once run has started");
        placeholder.setWrapText(true);
        eventsTable.setPlaceholder(placeholder);
        initializeIntegrationEvents();
//        eventsTable.setItems(eventsList);

        eventsTable.setEditable(true);
        initializeTableColumn(eventColumn,"eventType",false);
        initializeTableColumn(eventStartColumn,"eventStartTime",true);
        initializeTableColumn(eventEndColumn,"eventEndTime",true);
        initializeTableColumn(eventValueColumn,"eventValue",true);



//        eventStartColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        eventEndColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        eventValueColumn.setCellFactory(TextFieldTableCell.forTableColumn());



        //initialize sample table
        addInjectionDummyData();
        sampleTable.setItems(sampleList);

        sampleTable.setEditable(true);

        initializeTableColumn(sampleNumberColumn,"SampleNumber",false);
        initializeTableColumn(sampleNameColumn,"sampleName",true);
        initializeTableColumn(sampleTypeColumn,"sampleType",false);
        initializeTableColumn(injectionVolumeColumn,"injectionVolume",true);
        initializeTableColumn(compoundsColumn,"compoundButton",false);

        sampleNameColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, String>("sampleName"));

        sampleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        sampleNameColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, String>("SampleName"));
//        sampleTypeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, ComboBox>("sampleType"));
//        injectionVolumeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Double>("injectionVolume"));
//        compoundsColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Button>("compoundButton"));
//        sampleNumberColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Integer>("SampleNumber"));



//        sampleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        sampleTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        startButton.setDisable(false);

        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, //
                                Number oldValue, Number newValue) {
                if (newValue.intValue() == 1) {
                    speedIndicator.setText("Real Time (1X)");
                } else {
                    speedIndicator.setText("Accelerated (" + newValue.intValue() + "X)");
                }
            }
        });


    }

    private void initializeTableColumn(TableColumn column, String fieldName,boolean makeEditable){
        column.setCellValueFactory(new PropertyValueFactory<>(fieldName));


        if(makeEditable){
            column.setCellFactory(TextFieldTableCell.forTableColumn());
            column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent>() {
                @Override
                public void handle(TableColumn.CellEditEvent event) {
                    SampleInfo selected = (SampleInfo) event.getTableView().getItems().get(
                            event.getTablePosition().getRow());

                    Method method;
                    System.out.println("handler fired");
                    try {
                        String setterMethodName = "set" + fieldName.substring(0,1).toUpperCase() + fieldName.substring(1);
                        System.out.println(setterMethodName);

                         method = selected.getClass().getMethod(setterMethodName, String.class);
                        System.out.println(event.getNewValue().toString());
                        try {
                            method.invoke(selected, event.getNewValue().toString());
                        } catch (IllegalArgumentException e) {e.printStackTrace();}
                        catch (IllegalAccessException e) {e.printStackTrace();}
                        catch (InvocationTargetException e) {e.printStackTrace();}
                    } catch (NoSuchMethodException e) {
                        System.out.println("oh nose");
                    }





                }
            });

        }


    }


    public void laceReboot() {


        lineChartController.getLineChart().setVisible(true);
        startButton.setDisable(false);


    }

    public void startButtonClicked() {

        if (InjectionInfo.injectionCounter == 1) {
            mediaPlayer.play();

        }
        lineChartController.getxAxis().setAutoRanging(true);
        lineChartController.getyAxis().setAutoRanging(true);
        int injectionNumber = InjectionInfo.injectionCounter;


        if (injectionNumber <= sampleList.size()) {

             injectionInfo = new InjectionInfo(sampleList.get(injectionNumber - 1).getSampleCompounds(), 33, 5, speedSlider,
                    lineChart, new XYChart.Series<>(), startButton, 5,progressBar,this);

            injectionDataList.add(injectionInfo);

            //Controller does not need it's own injectionCounter, use one in injectionInfo instead
            Injection injection = new Injection(injectionInfo);
            injection.run();

        } else {
            startButton.setDisable(true);

        }

    }

    public void integrate() {

        //The easiest way to start will be to convert series into array and process the array
        //Eventual goal is to process as a series without explicit conversion to an array
        XYChart.Series<Number, Number> series2 = lineChartController.getLineChart().getData().get(0);
        double[][] rawData = new double[series2.getData().size()][2];


        for (int i = 0; i < series2.getData().size(); i++) {
            rawData[i][0] = series2.getData().get(i).getXValue().doubleValue();
            rawData[i][1] = series2.getData().get(i).getYValue().doubleValue();
        }

        Runnable dataIntegration = new DataIntegration(4, 5, rawData, 3, rawData, lineChartController.getLineChart());
        Thread thread = new Thread(dataIntegration);
        thread.setDaemon(true);
        thread.start();


    }

    public void button3Clicked() {


    }

    private void initializeIntegrationEvents() {
        IntegrationEvent defaultEvent1 = new IntegrationEvent("3.4", "4.8");
        IntegrationEvent defaultEvent2 = new IntegrationEvent("3.5", "4.9");
        IntegrationEvent defaultEvent3 = new IntegrationEvent("3.6", "4.1");
        IntegrationEvent defaultEvent4 = new IntegrationEvent("3.7", "4.2");
        IntegrationEvent defaultEvent5 = new IntegrationEvent("3.8", "4.3");

        eventsList.add(defaultEvent1);
        eventsList.add(defaultEvent2);
        eventsList.add(defaultEvent3);
        eventsList.add(defaultEvent4);
        eventsList.add(defaultEvent5);

    }


    private void addInjectionDummyData() {
        SampleInfo dummySample1 = new SampleInfo("Working Standard", "5");
        SampleInfo dummySample2 = new SampleInfo("Sensitivity", "5");
        SampleInfo dummySample3 = new SampleInfo(" Lot 749353", "5");
        sampleList.add(dummySample1);
        sampleList.add(dummySample2);
        sampleList.add(dummySample3);


    }


    public void newSampleButtonPushed() {
        SampleInfo sampleInfo = new SampleInfo("", "5");
        sampleTable.getItems().add(sampleInfo);
    }

    public void deleteSampleButtonPushed() {

        ObservableList<SampleInfo> selectedRows = sampleTable.getSelectionModel().getSelectedItems();
        ObservableList<SampleInfo> allSamples = sampleTable.getItems();
        for (SampleInfo sample : selectedRows) {
            int sampleNumberRemoved = sample.getSampleNumber();
            SampleInfo.sampleCounter--;
            allSamples.remove(sample);

            for (SampleInfo remainingSample : sampleList) {
                if (remainingSample.getSampleNumber() > sampleNumberRemoved) {
                    remainingSample.setSampleNumber(remainingSample.getSampleNumber() - 1);


                }


            }


        }

    }

    public void newEventButtonPushed() {

    }


    public void deleteEventButtonPushed() {
    }


    public void goToProcessingView() {

        Scene mainScene = StageHelper.getStages().get(0).getScene();
        mainStage = StageHelper.getStages().get(0);

//            mainStage.hide();
        if (ProcessingController.getProcessingControllerCounter() == 0) {
            ProcessingController processingController = new ProcessingController(mainStage, mainScene, injectionDataList);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProcessingView.fxml"));
            loader.setController(processingController);
            try {
                processingScene = new Scene(loader.load());
                mainStage.setWidth(mainStage.getWidth());
                mainStage.setHeight(mainStage.getHeight());
                mainStage.setScene(processingScene);


            } catch (IOException e) {
                e.printStackTrace();

            }
        } else {

            mainStage.setWidth(mainStage.getWidth());
            mainStage.setHeight(mainStage.getHeight());
            mainStage.setScene(processingScene);
        }


    }


    public void getClickedSampleInjection(MouseEvent mouseEvent) {
        SampleInfo selectedItem = sampleTable.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            int injectionIndex = selectedItem.getSampleNumber() - 1;
            if (injectionIndex < InjectionInfo.injectionList.size()) {

                InjectionInfo selectedInjection = InjectionInfo.injectionList.get(injectionIndex);
                System.out.println(selectedInjection.getCompounds().size());
                lineChart.getData().clear();
                lineChart.getData().add(selectedInjection.getSeries());
                eventsTable.setItems(selectedInjection.getEventsList());


            }


        }


    }

    public void abandonInjection(ActionEvent actionEvent) {
        InjectionInfo currentInjection = InjectionInfo.injectionList.get( InjectionInfo.injectionList.size()-1);
        currentInjection.setInjectionAbandoned(true);
    }

    public void finishInjection(ActionEvent actionEvent) {
        InjectionInfo currentInjection = InjectionInfo.injectionList.get( InjectionInfo.injectionList.size()-1);
        currentInjection.setInstantaneousInjectionFlag(true);
    }

    public TableView<IntegrationEvent> getEventsTable() {
        return eventsTable;
    }

    public void exportSeriesAsCSV(ActionEvent actionEvent) throws IOException {

        PrintWriter out = new PrintWriter("chromatogramData.csv");
        XYChart.Series<Number,Number> series = lineChart.getData().get(0);
        for(XYChart.Data<Number,Number> data: series.getData()){
            out.println(data.getXValue().doubleValue() + "," + data.getYValue().doubleValue());

        }
        // Close the file.
        out.close();  // Step 4
        }


        public void createNewSampleSet(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Creating new sample set abandons any currently running sets.  " +
                "Are you sure you wish to continue?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                abandonSet();
                ObservableList<SampleInfo> newSampleList = FXCollections.observableArrayList();
               sampleList.clear();
                System.out.println(sampleList);
               sampleList = newSampleList;
                sampleTable.setItems(sampleList);
                SampleInfo.setSampleCounter(1);
                injectionInfo = null;
                InjectionInfo.injectionList.clear();
                InjectionInfo.injectionCounter=1;

            }

        }
        public void abandonSet(){

            InjectionInfo currentInjection = InjectionInfo.injectionList.get( InjectionInfo.injectionList.size()-1);
            currentInjection.setSetAbandoned(true);
            lineChart.getData().clear();

        }
}



