package ChromatographyHome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;




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
    private static File file = new File("./src/ChromatographyHome/music.wav");
    private static final String MEDIA_URI = file.toURI().toString();
    private static Media sound = new Media(MEDIA_URI);



    @FXML
    private Label speedIndicator;

    private Task<ObservableList<XYChart.Data>> task;
    private static MediaPlayer mediaPlayer;
    private ObservableList<IntegrationEvent> eventsList = FXCollections.observableArrayList();
    private ObservableList<SampleInfo> sampleList = FXCollections.observableArrayList();

    @FXML
    private NumberAxis yAxis;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private Button startButton;

    @FXML
    private Slider speedSlider;

    XYChart.Series series = new XYChart.Series();

    @FXML
    private TableView<IntegrationEvent> eventsTable;

    @FXML
    private TableColumn<IntegrationEvent,ComboBox> eventColumn;

    @FXML
    private TableColumn<IntegrationEvent,Double> eventStartColumn;

    @FXML
    private TableColumn<IntegrationEvent,Double> eventEndColumn;

    @FXML
    private TableColumn<SampleInfo,Integer> sampleNumberColumn;

    @FXML
    private TableColumn<SampleInfo,String> sampleNameColumn;

    @FXML
    private TableColumn<SampleInfo,ComboBox> sampleTypeColumn;

    @FXML
    private TableColumn<SampleInfo,Double> injectionVolumeColumn;

    @FXML
    private TableView<SampleInfo> sampleTable;






//Not used
//    public void acquisitionSpeedChange(){
//        System.out.println("fired");
//        int speed = (int) speedSlider.getValue();
//        if (speed==1){
//            speedIndicator.setText("Real Time (1X)");
//        }
//        else {
//            speedIndicator.setText("Accelerated (" + speed +"X)");
//        }
//    }


    public void initialize() {


        //initialize integration table
        initializeIntegrationEvents();
        System.out.println(eventsTable);
        eventsTable.setItems(eventsList);

        eventColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent,ComboBox>("eventType"));
        eventStartColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent,Double>("eventStartTime"));
        eventEndColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent,Double>("eventEndTime"));

        //initialize sample table
        addInjectionDummyData();
        sampleTable.setItems(sampleList);

        sampleNumberColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo,Integer>("SampleNumber"));
        sampleNameColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo,String>("SampleName"));
        sampleTypeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo,ComboBox>("sampleType"));
        injectionVolumeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo,Double>("injectionVolume"));









        xAxis.autoRangingProperty().setValue(true);
        yAxis.autoRangingProperty().setValue(true);


        lineChart.getData().add(series);
        lineChart.setVisible(true);
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


    public void laceReboot() {
        System.out.println("Hello World!");


        lineChart.setVisible(true);
        startButton.setDisable(false);
        System.out.println(speedSlider.getValue());

    }

    public void buttonClicked() {
        startButton.setDisable(false);
        System.out.println(MEDIA_URI);


        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        System.out.println(sound.getDuration());
//        mediaPlayer.setOnError(() -> System.out.println("Error : " + mediaPlayer.getError().toString()));
        mediaPlayer.play();
        // For example


        List list1 = new ArrayList<>();
        List list2 = new ArrayList<>();
        Runnable generateData = new GenerateData(lineChart, series, list1, list2, speedSlider);
        Thread thread = new Thread(generateData);
        thread.setDaemon(true);
        thread.start();


    }

    public void integrate() {

        //The easiest way to start will be to convert series into array and process the array
        //Eventual goal is to process as a series without explicit conversion to an array
        XYChart.Series<Number, Number> series2 = lineChart.getData().get(0);
        double[][] rawData = new double[series2.getData().size()][2];


        for (int i = 0; i < series2.getData().size(); i++) {
            rawData[i][0] = series2.getData().get(i).getXValue().doubleValue();
            rawData[i][1] = series2.getData().get(i).getYValue().doubleValue();
        }
//        Runnable dataRegression = new DataRegression(RawData,4);
//        Thread thread = new Thread(dataRegression);
//        thread.setDaemon(true);
//        thread.start();

        Runnable dataIntegration = new DataIntegration(4,5,rawData,3,rawData,lineChart);
        Thread thread = new Thread(dataIntegration);
        thread.setDaemon(true);
        thread.start();





    }

    public void button3Clicked() {






    }

    private void initializeIntegrationEvents(){
        IntegrationEvent defaultEvent1 = new IntegrationEvent(3.4,4.8 );
        IntegrationEvent defaultEvent2 = new IntegrationEvent(3.5,4.9 );
        IntegrationEvent defaultEvent3 = new IntegrationEvent(3.6,4.1 );
        IntegrationEvent defaultEvent4 = new IntegrationEvent(3.7,4.2 );
        IntegrationEvent defaultEvent5 = new IntegrationEvent(3.8,4.3 );

        eventsList.add(defaultEvent1);
        eventsList.add(defaultEvent2);
        eventsList.add(defaultEvent3);
        eventsList.add(defaultEvent4);
        eventsList.add(defaultEvent5);
        System.out.println(eventsList);
    }


    private void addInjectionDummyData() {
        SampleInfo dummySample1 = new SampleInfo("Working Standard",(double)5);
        SampleInfo dummySample2 = new SampleInfo("Sensitivity",(double)5);
        SampleInfo dummySample3 = new SampleInfo(" Lot 749353",(double)10);
        sampleList.add(dummySample1);
        sampleList.add(dummySample2);
        sampleList.add(dummySample3);


    }


}


