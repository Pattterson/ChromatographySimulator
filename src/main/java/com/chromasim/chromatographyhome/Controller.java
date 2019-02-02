package com.chromasim.chromatographyhome;

import com.sun.javafx.stage.StageHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.vecmath.Point2d;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
    //must be updated before deploying to getclass.getresource();
    private static File file;
    private static String MEDIA_URI;
    private static Media sound;
    private static int injectionCounter = 1;
    private Stage mainStage;
    private boolean acquisitionViewShowing = true;
    private Scene processingScene;
    private ArrayList<InjectionInfo> injectionDataList = new ArrayList<>();
    final Rectangle zoomRect = new Rectangle();


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


    @FXML
    private TableView<IntegrationEvent> eventsTable;

    @FXML
    private TableColumn<IntegrationEvent, ComboBox> eventColumn;

    @FXML
    private TableColumn<IntegrationEvent, Double> eventStartColumn;

    @FXML
    private TableColumn<IntegrationEvent, Double> eventEndColumn;

    @FXML
    private TableColumn<SampleInfo, Integer> sampleNumberColumn;

    @FXML
    private TableColumn<SampleInfo, String> sampleNameColumn;

    @FXML
    private TableColumn<SampleInfo, ComboBox> sampleTypeColumn;

    @FXML
    private TableColumn<SampleInfo, Double> injectionVolumeColumn;

    @FXML
    private TableView<SampleInfo> sampleTable;

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


    int injectionNumber = 1;


    public void initialize() throws URISyntaxException {

        URL url = getClass().getResource("/music.wav");
        try {
            Media sound = new Media(url.toURI().toString());
            mediaPlayer = new MediaPlayer(sound);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //initialize integration table
        initializeIntegrationEvents();
        eventsTable.setItems(eventsList);

        eventColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent, ComboBox>("eventType"));
        eventStartColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent, Double>("eventStartTime"));
        eventEndColumn.setCellValueFactory(new PropertyValueFactory<IntegrationEvent, Double>("eventEndTime"));

        //initialize sample table
        addInjectionDummyData();
        sampleTable.setItems(sampleList);

        sampleNumberColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Integer>("SampleNumber"));
        sampleNameColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, String>("SampleName"));
        sampleTypeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, ComboBox>("sampleType"));
        injectionVolumeColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Double>("injectionVolume"));
        compoundsColumn.setCellValueFactory(new PropertyValueFactory<SampleInfo, Button>("compoundButton"));


        sampleTable.setEditable(true);
        sampleNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        sampleTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        xAxis.autoRangingProperty().setValue(false);
        yAxis.autoRangingProperty().setValue(false);
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


        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        chartContainer.getChildren().add(zoomRect);


        setUpZooming(zoomRect);
        System.out.println(xAxis.getUpperBound());

    }


    public void laceReboot() {


        lineChart.setVisible(true);
        startButton.setDisable(false);


    }

    public void startButtonClicked() {
        if (injectionCounter == 1) {
            mediaPlayer.play();
        }
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);



        if (injectionCounter <= sampleList.size()) {

            InjectionInfo injectionInfo = new InjectionInfo(sampleList.get(injectionCounter - 1).getSampleCompounds(), 33, 5, speedSlider,
                    injectionCounter, lineChart, new XYChart.Series<>(), startButton, 5);

            injectionDataList.add(injectionInfo);

            //Controller does not need it's own injectionCounter, use one in injectionInfo instead
            injectionCounter++;
            Injection injection = new Injection(injectionInfo);
            injection.run();

        } else {
            startButton.setDisable(true);

        }

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

        Runnable dataIntegration = new DataIntegration(4, 5, rawData, 3, rawData, lineChart);
        Thread thread = new Thread(dataIntegration);
        thread.setDaemon(true);
        thread.start();


    }

    public void button3Clicked() {


    }

    private void initializeIntegrationEvents() {
        IntegrationEvent defaultEvent1 = new IntegrationEvent(3.4, 4.8);
        IntegrationEvent defaultEvent2 = new IntegrationEvent(3.5, 4.9);
        IntegrationEvent defaultEvent3 = new IntegrationEvent(3.6, 4.1);
        IntegrationEvent defaultEvent4 = new IntegrationEvent(3.7, 4.2);
        IntegrationEvent defaultEvent5 = new IntegrationEvent(3.8, 4.3);

        eventsList.add(defaultEvent1);
        eventsList.add(defaultEvent2);
        eventsList.add(defaultEvent3);
        eventsList.add(defaultEvent4);
        eventsList.add(defaultEvent5);

    }


    private void addInjectionDummyData() {
        SampleInfo dummySample1 = new SampleInfo("Working Standard", (double) 5);
        SampleInfo dummySample2 = new SampleInfo("Sensitivity", (double) 5);
        SampleInfo dummySample3 = new SampleInfo(" Lot 749353", (double) 10);
        sampleList.add(dummySample1);
        sampleList.add(dummySample2);
        sampleList.add(dummySample3);


    }


    public void newSampleButtonPushed() {
        SampleInfo sampleInfo = new SampleInfo("", (double) 0);
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
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);

    }


    public void deleteEventButtonPushed() {
        final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(1000);
        final NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(1000);

        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
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

    private void setUpZooming(final Rectangle rect) {

        final Node zoomingNode = lineChart;

        //two x,y points to define zooming rectange in lineChart coordinates
        double[][] zoomRange = new double[2][2];
        final ObjectProperty<Point2D> mouseAnchorSemiLocal = new SimpleObjectProperty<>();
        final ObjectProperty<Point2D> mouseAnchorScene = new SimpleObjectProperty<>();
        final ObjectProperty<Point2D> mouseAnchorLocal = new SimpleObjectProperty<>();

        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("mousepressed");

                Point2D pointClicked = new Point2D(event.getSceneX(), event.getSceneY());

                //relative to top left of axial coordinate system
                double xPosInAxis = xAxis.sceneToLocal(new Point2D(pointClicked.getX(), 0)).getX();
                double yPosInAxis = yAxis.sceneToLocal(new Point2D(0, pointClicked.getY())).getY();


                //cartesian coordinates of event
                double x1 = xAxis.getValueForDisplay(xPosInAxis).doubleValue(); //coordinates
                double y1 = yAxis.getValueForDisplay(yPosInAxis).doubleValue(); //coordinates

//              //coordinates of initial clicked (for zooming)
                zoomRange[0][0] = x1;
                zoomRange[0][1] = y1;


                //local-cartesian coordinates
                //semiLocal-relative to gridPane
                //scene-relative to scene

                mouseAnchorSemiLocal.set(new Point2D(event.getX(), event.getY()));
                mouseAnchorScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
                mouseAnchorLocal.set(new Point2D(x1, y1));

                rect.setWidth(0);
                rect.setHeight(0);

            }
        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

//                double x = event.getX();
//                double y = event.getY();
//                rect.setX(Math.min(x, mouseAnchorSemiLocal.get().getX()));
//                rect.setY(Math.min(y, mouseAnchorSemiLocal.get().getY()));
//                rect.setWidth(Math.abs(x - mouseAnchorSemiLocal.get().getX()));
//                rect.setHeight(Math.abs(y - mouseAnchorSemiLocal.get().getY()));

                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                final ObjectProperty<Point2D> movingMouseAnchorSemiLocal = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> movingMouseAnchorScene = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> movingMouseAnchorLocal = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> x0PosScene = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> x0PosSemiLocal = new SimpleObjectProperty();

                movingMouseAnchorSemiLocal.set(new Point2D(event.getX(), event.getY()));
                movingMouseAnchorScene.set(new Point2D(event.getSceneX(), event.getSceneY()));


                //relative to left of scene
//                System.out.println(pointInScene.getX());
                double xPosInGridPane = xAxis.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInGridPane = yAxis.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();

                double x2 = xAxis.getValueForDisplay(xPosInGridPane).doubleValue(); //coordinates
                double y2 = yAxis.getValueForDisplay(yPosInGridPane).doubleValue(); //coordinates
                movingMouseAnchorLocal.set(new Point2D(x2, y2));

                //relative to left of gridpane
                double xRect = event.getX();
                double yRect = event.getY();

                //rectange is drawn relative to parent gridPane
                double anchorX = mouseAnchorSemiLocal.get().getX();
                double anchorY = mouseAnchorSemiLocal.get().getY();

                //equivalent
//                    rect.setX(Math.min(mouseAnchorSemiLocal.get().getX(), movingMouseAnchorSemiLocal.get().getX()));

                //0.001: added to XRect since otherwise when dragging from lower right to upper left, rectangle node interferes
                //with setOnMouseClicked listener and listener will not fire
                rect.setX(Math.min(xRect+0.001, anchorX));
                rect.setY(Math.min(yRect, anchorY));
                rect.setWidth(Math.abs(xRect - anchorX));
                rect.setHeight(Math.abs(yRect - anchorY));

                zoomRange[1][0] = x2;
                zoomRange[1][1] = y2;
                System.out.println("x1 "+zoomRange[0][0]);
                System.out.println("x2 " +zoomRange[0][1]);
                System.out.println("y1 "+zoomRange[1][0]);
                System.out.println("y2 "+zoomRange[1][1]);
            }


        });

        zoomingNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("mouseCLick");
                if (event.getButton() == MouseButton.SECONDARY) {
                    if (event.getClickCount() == 2) {
                        System.out.println("double click");
                        xAxis.setAutoRanging(true);
                        yAxis.setAutoRanging(true);
                    }

                } else {
//                    System.out.println("we will zoom from " + zoomRange[0][0] + " , " + zoomRange[0][1] +
//                            " to " + zoomRange[1][0] + " , " + zoomRange[1][1]);
                doZoom(zoomRange);
                }

            }
        });
    }

    private void doZoom(double[][] zoomRange) {

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);


        xAxis.setLowerBound(Math.min(zoomRange[0][0], zoomRange[1][0]));
        yAxis.setLowerBound(Math.min(zoomRange[0][1], zoomRange[1][1]));
        xAxis.setUpperBound(Math.max(zoomRange[0][0], zoomRange[1][0]));
        ;
        yAxis.setUpperBound(Math.max(zoomRange[0][1], zoomRange[1][1]));

        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }


}
