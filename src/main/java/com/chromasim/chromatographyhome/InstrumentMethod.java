package com.chromasim.chromatographyhome;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;


public class InstrumentMethod {

    private double samplingRate;
    private double runTime;
    private double initialTemp;
    private double initialTime;
    private double ramp;
    private double maxTemp;
    private double inletTemp;
    private double columnFlow;
    private String columnType;
    private String detectorType;

    public InstrumentMethod() {

    }

    public double getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public double getRunTime() {
        return runTime;
    }

    public void setRunTime(double runTime) {
        this.runTime = runTime;
    }

    public double getInitialTemp() {
        return initialTemp;
    }

    public void setInitialTemp(double initialTemp) {
        this.initialTemp = initialTemp;
    }

    public double getInitialTime() {
        return initialTime;
    }

    public void setInitialTime(double initialTime) {
        this.initialTime = initialTime;
    }

    public double getRamp() {
        return ramp;
    }

    public void setRamp(double ramp) {
        this.ramp = ramp;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getInletTemp() {
        return inletTemp;
    }

    public void setInletTemp(double inletTemp) {
        this.inletTemp = inletTemp;
    }

    public double getColumnFlow() {
        return columnFlow;
    }

    public void setColumnFlow(double columnFlow) {
        this.columnFlow = columnFlow;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(String detectorType) {
        this.detectorType = detectorType;
    }
}
