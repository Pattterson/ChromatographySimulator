package com.chromasim.chromatographyhome;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;


//All information needed to perform one injection (instrument method parameters / sample compounds, etc...
//will be included in an Injection Info object which is passed to an injection object to actually perform the injection
public class InjectionInfo {

    private boolean injectionAbandoned;
    private ObservableList<Compound> compounds;
    private double runTime; //in minutes
    private double samplingRate;
    private Slider speedSlider;
    static int injectionCounter =1;
    private LineChart<Number,Number> lineChart;
    private XYChart.Series<Number,Number> series;
    private Button nextInjection;
    private int pointsToCollect;
    private int refreshRate; //chromatogram refresh / add data rate, in seconds

    public InjectionInfo(ObservableList<Compound> compounds, double runTime, double samplingRate, Slider speedSlider, LineChart<Number, Number> lineChart, XYChart.Series<Number, Number> series, Button nextInjection, int refreshRate) {
        injectionAbandoned = false;
        this.compounds = compounds;
        this.runTime = runTime;
        this.samplingRate = samplingRate;
        this.speedSlider = speedSlider;
        this.refreshRate = refreshRate;

        injectionCounter++;

        this.lineChart = lineChart;
        this.series = series;
        this.nextInjection = nextInjection;
        pointsToCollect = (int)(samplingRate * 60 * runTime + 1); //+1 for time =0 datapoint

        Platform.runLater(() -> {
            Node n = series.getNode();
            StringBuilder style = new StringBuilder();
            style.append("-fx-stroke: black; \n  -fx-stroke-width: 1px;");
            n.setStyle(style.toString());
        });

    }

    public boolean isInjectionAbandoned() {
        return injectionAbandoned;
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

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    public XYChart.Series<Number, Number> getSeries() {
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
}
