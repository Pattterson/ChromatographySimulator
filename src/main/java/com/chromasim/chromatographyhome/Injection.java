package com.chromasim.chromatographyhome;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Injection implements Runnable {
    private InjectionInfo injectionInfo;
    private final ArrayList<XYChart.Data<Number, Number>> pointsToAdd; //generated datapoints that will be pushed to chart by output timer
    private int samplingPoint = 0; //counter to increment with each new data point

    public Injection(InjectionInfo injectionInfo) {
        this.injectionInfo = injectionInfo;
        pointsToAdd = new ArrayList<>();
    }


    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                injectionInfo.getController().sampleTable.getSelectionModel().select(injectionInfo.getThisInjectionNumber() - 1);
                SampleInfo sample = injectionInfo.getController().sampleTable.getItems().get(injectionInfo.getThisInjectionNumber() - 1);
                injectionInfo.getController().progressIndicatorText.setText("Injecting vial " + injectionInfo.getThisInjectionNumber() + ": " + sample.getSampleName());
            }
        });

        injectionInfo.getLineChart().getData().add(injectionInfo.getSeries());
        startGenerationTimer();
        startOutputTimer();

    }


    private void startGenerationTimer() {

        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {

            int speedSliderValue = injectionInfo.getSpeedSlider().valueProperty().intValue();

            public void run() {
                generate();

                if (samplingPoint > injectionInfo.getPointsToCollect() || injectionInfo.getInjectionAbandoned() || injectionInfo.getSetAbandoned()) {
                    timer.cancel();
                    timer.purge();


                    //if speedSliderValue has changed
                } else if (injectionInfo.getInstantaneousInjectionFlag()) {
                    timer.cancel();
                    timer.purge();
                    while (samplingPoint <= injectionInfo.getPointsToCollect()) {
                        generate();
                    }


                } else if (injectionInfo.getSpeedSlider().valueProperty().intValue() != speedSliderValue) {
                    timer.cancel();
                    timer.purge();
                    startGenerationTimer();   // start the time again with a new period time
                }


            }

            private void generate() {
                //If sampling rate is 5(hz), we collect 5 points per second, therefore time will increment by 0.2
                double time = samplingPoint * 1 / injectionInfo.getSamplingRate();


                double response = calculateResponseAtDatum(time);
                synchronized (pointsToAdd) {
                    pointsToAdd.add(new XYChart.Data<Number, Number>(time, response));

                    samplingPoint += 1;

                }
            }

//            frequency is (1/sampling rate(hz)) * 1000(ms/s) / speed slider modulation
        }, 0, (long) (1 / injectionInfo.getSamplingRate() * 1000 / injectionInfo.getSpeedSlider().valueProperty().longValue()));
    }


    private void startOutputTimer() {
        Timer timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int speedSliderValue = injectionInfo.getSpeedSlider().valueProperty().intValue();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (pointsToAdd) {
                            injectionInfo.getSeries().getData().addAll(pointsToAdd);

                            pointsToAdd.clear();

                        }
                        double percentCompleted = ((double) samplingPoint) / injectionInfo.getPointsToCollect();
                        injectionInfo.getProgressBar().progressProperty().set(percentCompleted);

                    }
                });

                if (injectionInfo.getSeries().getData().size() > injectionInfo.getPointsToCollect() || injectionInfo.getInjectionAbandoned() || injectionInfo.getSetAbandoned()) {
                    timer.cancel();
                    timer.purge();
                    System.out.println("output timer has stopped");
                    if (injectionInfo.getInjectionAbandoned()) {
                        Platform.runLater(() -> injectionInfo.getController().progressIndicatorText.setText("Injection Abandoned"));
                    }

                    else if (injectionInfo.getSetAbandoned()) {
                        Platform.runLater(() -> injectionInfo.getController().progressIndicatorText.setText("Sample Set Abandoned"));

                    } else {

                        Platform.runLater(() -> injectionInfo.getController().progressIndicatorText.setText("Injection Done"));

                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> injectionInfo.getController().progressIndicatorText.setText("Preparing for next Injection"));
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            injectionInfo.getNextInjection().fire();
                            injectionInfo.getLineChart().getData().remove(injectionInfo.getSeries());
                        });

                    }

                } else if (speedSliderValue != injectionInfo.getSpeedSlider().valueProperty().intValue()) {
                    timer.cancel();
                    timer.purge();
                    startOutputTimer();   // start the time again with a new period time
                }
            }


            //convert refresh rate to milliseconds, modulate by speedSlider value
        }, 1, injectionInfo.getRefreshRate() * 20 / injectionInfo.getSpeedSlider().valueProperty().intValue());

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


}
