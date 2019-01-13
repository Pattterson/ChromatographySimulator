package ChromatographyHome;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;

import java.util.*;

public class GenerateData implements Runnable {


    private static int counter;
    private static Slider speedSlider;
    private static double maximumCycles;
    private static int injectionCounter =1;
    ArrayList<XYChart.Data<Double, Integer>> pointsToAdd = new ArrayList<>();
    double[][] dataForProcessing;
    private double runTime = 30; //minutes
    private double samplingRate = 0.2; //will instantiate in constructor -- in hz, typical~0.2 hz
    private LineChart lineChart;
    private XYChart.Series series;
    private List<Double> retentionTimes = new ArrayList();
    private List<Double> responseFactor = new ArrayList();
    private List<Double> halfPeakWidth = new ArrayList();
    private int speedSliderValue;
    private ObservableList<Compound> compounds;
    private ObservableList<SampleInfo> sampleInfo;


    public GenerateData(LineChart lineChart, XYChart.Series series, ObservableList<SampleInfo> sampleInfo, Slider speedSlider) {
        this.lineChart = lineChart;
        this.series = series;
        this.speedSlider = speedSlider;
        speedSliderValue = speedSlider.valueProperty().intValue();
        maximumCycles = runTime * 60 / samplingRate;
        this.sampleInfo = sampleInfo;

        dataForProcessing = new double[(int) (maximumCycles + 1)][2];


    }

    private void generateDummyData() {
        retentionTimes.add(120.);
        retentionTimes.add(240.);
        retentionTimes.add(360.);
        responseFactor.add(1.);
        responseFactor.add(10.);
        responseFactor.add(100.);
        halfPeakWidth.add(30.);
        halfPeakWidth.add(30.);
        halfPeakWidth.add(60.);


    }

    public void run() {
        generateDummyData();
        double time;
        int response;


        //Periodically takes generated datapoints and pushes to scene graph
        startOutputTimer(speedSliderValue * 5000);


        //Generate the data

        GenerateDatum generateDatum = new GenerateDatum();
        startGenerationTimer(speedSliderValue * 200, generateDatum);
        compounds=sampleInfo.get(injectionCounter-1).getSampleCompounds();


    }

    private void startGenerationTimer(int frequency, GenerateDatum generateDatum) {
        Timer timer = new Timer(true);
        counter = 0;

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                //How often a datapoint is generated -- to be updated so user can control this LABEL D
                double time = counter * 0.2;
                int response = generateDatum.calculateResponse(time);
                synchronized (pointsToAdd) {
                    pointsToAdd.add(new XYChart.Data(time, response));
                    dataForProcessing[counter][0] = time;
                    dataForProcessing[counter][1] = response;

                    counter += 1;

                    if (counter > maximumCycles) {
                        timer.cancel();
                        timer.purge();
                        injectionCounter++;
                        GenerateDatum generateNextDatum = new GenerateDatum();
                        compounds=sampleInfo.get(injectionCounter-1).getSampleCompounds();

                        startGenerationTimer(speedSliderValue * 200, generateDatum);



                    } else if (speedSlider.valueProperty().intValue() != 200 / frequency) {
                        timer.cancel();
                        timer.purge();
                        speedSliderValue = speedSlider.valueProperty().intValue();
                        startGenerationTimer(200 / speedSliderValue, generateDatum);   // start the time again with a new period time
                    }
                }
            }
        }, 0, frequency);
    }

    private void startOutputTimer(int frequency) {
        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (pointsToAdd) {
                            series.getData().addAll(pointsToAdd);
                            pointsToAdd.clear();


                        }

                    }
                });

                if (counter > maximumCycles) {
                    timer.cancel();
                    timer.purge();
                } else if (speedSlider.valueProperty().intValue() != 5000 / frequency) {
                    timer.cancel();
                    timer.purge();
                    speedSliderValue = speedSlider.valueProperty().intValue();
                    startOutputTimer(5000 / speedSliderValue);   // start the time again with a new period time
                }
            }


        }, 100, frequency);

    }

    private class generationTask implements Runnable {
        @Override
        public void run() {

        }

    }

    private class GenerateDatum {
        //generate datum may be better as just a function since it only does one thing

        private int calculateResponse(double time) {

            int response = 0;

            for (Compound compound : compounds) {

                double retentionTime = compound.getRetentionTime();

                //adjusted retention time: since we want peaks to elute as specific times,
                // we must subtract the current time since a gaussian distribution is centered at x = 0.
                //example retentionTime = 30s; when time = 30s, adjusted retention time will be zero (the max of the distribution)
                double adjustedRetentionTime = retentionTime - time;

                //we coiuld also use mu to shift peaks, but there is no need to at the moment
                //use sigma to account for peak broadening
                response += calculateGaussian(adjustedRetentionTime, 0, 10) * compound.getResponse();
            }
            response += NoiseGenerator.generateNoise();
            return response;
        }

        private void populateDummyCompoundList(List<Compound> compoundList) {
            for (int i = 0; i < 5; i++) {
                Compound compound = new Compound(new SimpleStringProperty(" qwijibo"));
                compound.setRetentionTime(Integer.toString(30 + 30 * i));
                compound.setResponse(Double.toString(i + 1 + 0.5));
                compoundList.add(compound);

            }
        }

        private int calculateGaussian(double x, double mu, double sigma) {
            int result = 0;

            double coefficient = 1 / (Math.sqrt(2 * Math.PI * sigma * sigma));
            double exponential = Math.exp(-(x - mu) * (x - mu) / (2 * sigma * sigma));

            return (int) (coefficient * exponential * 1000);
            //response of 1000 just for example, will be based on response factor for each specific compound
        }


    }


}
