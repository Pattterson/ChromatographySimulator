package com.chromasim.chromatographyhome;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


//this class needs badly needs refactoring, might not need to implement runnable
public class Injection implements Runnable {
    private InjectionInfo injectionInfo;
    private final ArrayList<XYChart.Data<Number, Number>> pointsToAdd; //generated datapoints that will be pushed to chart by output timer
    private int samplingPoint = 0; //counter to increment with each new data point
    private ObservableList<SampleInfo> samplesList;
    private int injectionNumber = 0;
    private SampleInfo sample;
    private InstrumentMethod instrumentMethod;

    private List<InjectionInfo> injectedSamplesList = new ArrayList<>();

    public Injection(ObservableList<SampleInfo> samplesList, InstrumentMethod instrumentMethod) {
        System.out.println(instrumentMethod.getPointsToCollect());
        System.out.println(instrumentMethod.getSamplingRate());
        this.samplesList = samplesList;
        this.instrumentMethod = instrumentMethod;
        injectNext();
//        sample = samplesList.get(0);
//        sample.setInjectionInfo(new InjectionInfo(samplesList.get(0).getSampleCompounds(), 33, 5, FXMLComponents.speedSlider,
//                FXMLComponents.lineChart, new XYChart.Series<Number,Number>(),5, FXMLComponents.progressBar));
//        injectionInfo = sample.getInjectionInfo();
        pointsToAdd = new ArrayList<>();
    }


    @Override
    public void run() {
        Platform.runLater(() -> {

            FXMLComponents.sampleTable.getSelectionModel().select(injectionInfo.getThisInjectionNumber() - 1);
            FXMLComponents.progressIndicatorText.setText("Injecting vial " + injectionInfo.getThisInjectionNumber() + ": " + sample.getSampleName());
        });

        injectionInfo = sample.getInjectionInfo();
        System.out.println(injectionNumber);
        startGenerationTimer();
        startOutputTimer();

    }


    private void startGenerationTimer() {

        Timer timer = new Timer(true);


        timer.schedule(new TimerTask() {


            int speedSliderValue = FXMLComponents.speedSlider.valueProperty().intValue();

            public void run() {

                generate();

                if (samplingPoint > instrumentMethod.getPointsToCollect() || injectionInfo.getInjectionAbandoned() || injectionInfo.getSetAbandoned()) {

                    timer.cancel();
                    timer.purge();


                    //if speedSliderValue has changed
                } else if (injectionInfo.getInstantaneousInjectionFlag()) {
                    timer.cancel();
                    timer.purge();
                    while (samplingPoint <= instrumentMethod.getPointsToCollect()) {
                        generate();
                    }


                } else if (FXMLComponents.speedSlider.valueProperty().intValue() != speedSliderValue) {
                    timer.cancel();
                    timer.purge();
                    startGenerationTimer();   // start the time again with a new period time
                }


            }

            private void generate() {
                //If sampling rate is 5(hz), we collect 5 points per second, therefore time will increment by 0.2
                double time = samplingPoint * 1 / instrumentMethod.getSamplingRate();


                double response = calculateResponseAtDatum(time);
                synchronized (pointsToAdd) {
                    pointsToAdd.add(new XYChart.Data<Number, Number>(time, response));

                    samplingPoint += 1;

                }
            }

//            frequency is (1/sampling rate(hz)) * 1000(ms/s) / speed slider modulation
        }, 0, (long) (1 / instrumentMethod.getSamplingRate() * 1000 / FXMLComponents.speedSlider.valueProperty().longValue()));
    }


    private void startOutputTimer() {
        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int speedSliderValue = FXMLComponents.speedSlider.valueProperty().intValue();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (pointsToAdd) {
                            injectionInfo.getSeries().getData().addAll(pointsToAdd);

                            pointsToAdd.clear();

                        }
                        double percentCompleted = ((double) samplingPoint) / instrumentMethod.getPointsToCollect();
                        FXMLComponents.progressBar.progressProperty().set(percentCompleted);

                    }
                });

                if (injectionInfo.getSeries().getData().size() > instrumentMethod.getPointsToCollect() || injectionInfo.getInjectionAbandoned() || injectionInfo.getSetAbandoned()) {
                    timer.cancel();
                    timer.purge();
                    if (injectionInfo.getSetAbandoned()) {
                        Platform.runLater(() -> FXMLComponents.progressIndicatorText.setText("Sample Set Abandoned"));

                    } else {
                        if (injectionInfo.getInjectionAbandoned()) {
                            Platform.runLater(() -> FXMLComponents.progressIndicatorText.setText("Injection Abandoned"));
                        } else {
                            Platform.runLater(() -> FXMLComponents.progressIndicatorText.setText("Injection Done"));
                        }


                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> FXMLComponents.progressIndicatorText.setText("Preparing for next Injection"));
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        Platform.runLater(() -> {
                            injectNext();
                            FXMLComponents.lineChart.getData().remove(injectionInfo.getSeries());
                        });

                    }

                } else if (speedSliderValue != FXMLComponents.speedSlider.valueProperty().intValue()) {
                    timer.cancel();
                    timer.purge();
                    startOutputTimer();   // start the time again with a new period time
                }
            }


            //convert refresh rate to milliseconds, modulate by speedSlider value
        }, 1, injectionInfo.getRefreshRate() * 20 / FXMLComponents.speedSlider.valueProperty().intValue());

    }


    private double calculateResponseAtDatum(double time) {
        double response = NoiseGenerator.generateNoise();
        if (injectionInfo.getCompounds().size() == 0) {
//            System.out.println("there are no compounds in the list");

            return response;


        }


        for (Compound compound : injectionInfo.getCompounds()) {

            double retentionTime = compound.getRetentionTime();

            //adjusted retention time: since we want peaks to elute as specific times,
            // we must subtract the current time since a gaussian distribution is centered at x = 0.
            //example retentionTime = 30s; when time = 30s, adjusted retention time will be zero (the max of the distribution)
            double adjustedRetentionTime = retentionTime - time;

            //we could also use mu to shift peaks, but there is no need to at the moment
            //use sigma to account for peak broadening
            //sum up gaussian responses
            response += calculateGaussian(adjustedRetentionTime, 0, 10) * compound.getResponse();


        }
        return response;
    }

    private double calculateGaussian(double x, double mu, double sigma) {
        double result = 0;

        double coefficient = 1 / (Math.sqrt(2 * Math.PI * sigma * sigma));
        double exponential = Math.exp(-(x - mu) * (x - mu) / (2 * sigma * sigma));

        return coefficient * exponential * 1000;
        //response of 1000 just for example, will be based on response factor for each specific compound
    }


    public void injectNext() {

        FXMLComponents.lineChart.getData().clear();
        System.out.println(injectionNumber);
        if (injectionNumber < samplesList.size()) {


            SampleInfo sampleToInject = samplesList.get(injectionNumber);
            injectionNumber++;
            injectionInfo = new InjectionInfo(sampleToInject.getSampleCompounds(), 33, 5,
                     5);
            injectedSamplesList.add(injectionInfo);

            sampleToInject.setInjectionInfo(injectionInfo);

            sampleToInject.getInjectionInfo().setInjected(true);
            sample = sampleToInject;
            samplingPoint = 0;
            run();
        }
        else{
            Platform.runLater(() -> {
                Platform.runLater(() -> FXMLComponents.progressIndicatorText.setText("Injections finished"));
            });
    }

}


}
