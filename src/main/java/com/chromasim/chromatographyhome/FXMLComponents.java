package com.chromasim.chromatographyhome;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class FXMLComponents {

    //FXML components need to be accessed / set in classes other than the controllers, therefore all
    //components are grouped here for convenience and all classes within class can access as needed.

    static Button startButton;
    static Slider speedSlider;
    static TableView<IntegrationEvent> eventsTable;
    static TableColumn<IntegrationEvent, ComboBox> eventColumn;
    static TableColumn<IntegrationEvent, String> eventStartColumn;
    static TableColumn<IntegrationEvent, String> eventEndColumn;
    static TableColumn<SampleInfo, Integer> sampleNumberColumn;
    static TableColumn<SampleInfo, String> sampleNameColumn;
    static TableColumn<SampleInfo, ComboBox> sampleTypeColumn;
    static TableColumn<SampleInfo, String> injectionVolumeColumn;
    static TableView<SampleInfo> sampleTable;
    static Button newSampleButton;
    static Button deleteSampleButton;
    static Button newEventButton;
    static Button deleteEventButton;
    static TableColumn<SampleInfo, Button> compoundsColumn;
    static GridPane chartContainer;
    static LineChartController lineChartController;
    static ProgressBar progressBar;
    static TableColumn<IntegrationEvent,String> eventValueColumn;
    static Label progressIndicatorText;
    static LineChart<Number,Number> lineChart;
    static NumberAxis xAxis;
    static NumberAxis yAxis;


private FXMLComponents(){}


}
