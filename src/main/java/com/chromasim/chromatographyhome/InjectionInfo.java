package com.chromasim.chromatographyhome;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

import java.util.ArrayList;
import java.util.List;


//All information needed to perform one injection (instrument method parameters / sampleInfo / Series)
//will be included in an Injection Info object which is passed to an injection object to actually perform the injection
public class InjectionInfo {

//instead of transfering all fx elements manually, perhaps just share a copy of the controller
    private boolean injectionAbandoned;
    private ObservableList<Compound> compounds;
    private double runTime; //in minutes
    private double samplingRate;

    static int injectionCounter =1;
    private final int thisInjectionNumber;
    private LineChart<Number,Number> lineChart;
    private XYChart.Series<Number,Number> series;
    private Button nextInjection;
    private int pointsToCollect;
    private int refreshRate; //chromatogram refresh / add data rate, in seconds
    static List<InjectionInfo> injectionList= new ArrayList<>();
    private boolean instantaneousInjectionFlag = false;

    private ObservableList<IntegrationEvent> eventsList = FXCollections.observableArrayList();
    private boolean setAbandoned;
    private boolean injected = false;

    public boolean isInjected() {
        return injected;
    }

    public void setInjected(boolean injected) {
        this.injected = injected;
    }

    public InjectionInfo(ObservableList<Compound> compounds, double runTime, double samplingRate,  int refreshRate) {
        thisInjectionNumber = injectionCounter;
        injectionAbandoned = false;
        this.compounds = compounds;
        this.runTime = runTime;
        this.samplingRate = samplingRate;
        this.refreshRate = refreshRate;
        series = new XYChart.Series<Number,Number>();
        addDefaultIntegrationEvents();

        injectionCounter++;

        System.out.println(this.series);

        pointsToCollect = (int)(samplingRate * 60 * runTime + 1); //+1 for time =0 datapoint

        injectionList.add(this);

        Platform.runLater(() -> {
            FXMLComponents.lineChart.getData().add(series);
            Node n = series.getNode();
            System.out.println(n);
            StringBuilder style = new StringBuilder();
            style.append("-fx-stroke: black; \n  -fx-stroke-width: 1px;");
            n.setStyle(style.toString());
        });

    }

    private void addDefaultIntegrationEvents() {
        IntegrationEvent defaultEvent1 = new IntegrationEvent("Set Threshold","0", "0");
        IntegrationEvent defaultEvent2 = new IntegrationEvent("Set Peak Width","0", "0");
        eventsList.add(defaultEvent1);
        eventsList.add(defaultEvent2);
        FXMLComponents.eventsTable.setItems(eventsList);
    }

    public ObservableList<IntegrationEvent> getEventsList() {
        return eventsList;
    }

    public boolean getInjectionAbandoned() {
        return injectionAbandoned;
    }

    public int getThisInjectionNumber() {
        return thisInjectionNumber;
    }

    public ObservableList<Compound> getCompounds() {
        return compounds;
    }

    public double getRunTime() {
        return runTime;
    }

    public double getSamplingRate() {
        return samplingRate;
    }


    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    public XYChart.Series<Number, Number> getSeries() {


        Platform.runLater(() -> {
            Node n = series.getNode();
            StringBuilder style = new StringBuilder();
            style.append("-fx-stroke: black; \n  -fx-stroke-width: 1px;");
            n.setStyle(style.toString());
        });

        return series;
    }

    public static int getInjectionCounter() {
        return injectionCounter;
    }

    public int getPointsToCollect() {
        return pointsToCollect;
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    public Button getNextInjection() {
        return nextInjection;
    }

    public void setInjectionAbandoned(boolean injectionAbandoned) {
        this.injectionAbandoned = injectionAbandoned;
    }

    public boolean getInstantaneousInjectionFlag() {

        return instantaneousInjectionFlag;
    }

    public void setInstantaneousInjectionFlag(boolean instantaneousInjectionFlag){
        this.instantaneousInjectionFlag = instantaneousInjectionFlag;
    }

    public boolean isSetAbandoned() {
        return setAbandoned;
    }

    //bad naming scheme
    public void setSetAbandoned(boolean setAbandoned) {
        this.setAbandoned = setAbandoned;
    }
    public boolean getSetAbandoned(){
        return setAbandoned;
    }
}
