package ChromatographyHome;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Slider;

import java.util.*;

public class GenerateData implements Runnable {


    private static int counter;

    private double runTime = 30; //minutes
    private double samplingRate = 0.2; //will instantiate in constructor -- in hz, typical~0.2 hz
    private static Slider speedSlider;
    private LineChart lineChart;
    private XYChart.Series series;
    private List<Double> retentionTimes = new ArrayList();
    private List<Double> responseFactor = new ArrayList();
    private List<Double> halfPeakWidth = new ArrayList();
    private int speedSliderValue;
    ArrayList<XYChart.Data<Double, Integer>> pointsToAdd = new ArrayList<>();
    double[][] dataForProcessing;
    private static double maximumCycles;


    public GenerateData(LineChart lineChart, XYChart.Series series, List<Double> retentionTimes, List<Double> responseFactor, Slider speedSlider) {
        this.lineChart = lineChart;
        this.series = series;
        this.retentionTimes = retentionTimes;
        this.responseFactor = responseFactor;
        this.speedSlider = speedSlider;
        speedSliderValue = speedSlider.valueProperty().intValue();
        maximumCycles = runTime * 60 / samplingRate;

        System.out.println();
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
//        new Timer(true).scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//                Platform.runLater(new Runnable() {
//                    @Override
//                    public void run() {
//                        synchronized (pointsToAdd) {
//                            series.getData().addAll(pointsToAdd);
//                            pointsToAdd.clear();
//                        }
//                    }
//                });
//
//
////                    System.out.println(pointsToAdd.isEmpty());
//            }
//
//
//        }, 100, 5000);


        //Generate the data

        GenerateDatum generateDatum = new GenerateDatum();
        startGenerationTimer(speedSliderValue * 200, generateDatum);


//        Timer generationTimer = new Timer(true);

//        see startGenerationTimer method

//        TimerTask generationTask = new TimerTask() {
//            @Override
//            public void run() {
//                int frequency = 3;
//
//                double time = counter * 0.2;
//                int response = generateDatum.calculateResponse(time);
//                synchronized (pointsToAdd) {
//                    System.out.println(time);
//                    pointsToAdd.add(new XYChart.Data(time, response));
//                    counter += 1;
//
//                   if(speedSlider.valueProperty().intValue()!=speedSliderValue){
//                       generationTimer.cancel();
//                       generationTimer.purge();
//                       speedSliderValue = speedSlider.valueProperty().intValue();
//                       startGenerationTimer(new Timer);
//
//
//                   }
//                }
//            }
//
//        };
//        generationTimer.scheduleAtFixedRate(generationTask,0,200*speedSliderValue);

//        new Timer(true).scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//
//            }
//
//
//        },0,200);


//        for(int i=0; i<10000; i++){
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            //5 points per second
//            time = i * 0.2;
//            response = generateDatum.calculateResponse(time);
//
//            synchronized (pointsToAdd) {
//
//                pointsToAdd.add(new XYChart.Data(time, response));
//
//            }
//        }
    }

    private void startGenerationTimer(int frequency, GenerateDatum generateDatum) {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {


                //How often a datapoint is generated -- to be updated so user can control this LABEL D
                double time = counter * 0.2;
                int response = generateDatum.calculateResponse(time);
                synchronized (pointsToAdd) {
//                    System.out.println(time);
                    pointsToAdd.add(new XYChart.Data(time, response));
                    dataForProcessing[counter][0] = time;
                    dataForProcessing[counter][1] = response;
//                    System.out.println("Added (x,y) to dataForProcessing Array: (" + dataForProcessing[counter][0] + "," + dataForProcessing[counter][1] + ")"
//                            + "counter is: " + counter + " out of " + maximumCycles);
                    counter += 1;

                    if (counter > maximumCycles) {
                        timer.cancel();
                        timer.purge();
//
//                        DataRegression evaluateData = new DataRegression(dataForProcessing);
//                        evaluateData.doNothing();


                    } else if (speedSlider.valueProperty().intValue() != 200 / frequency) {
                        timer.cancel();
                        timer.purge();
                        speedSliderValue = speedSlider.valueProperty().intValue();
                        System.out.println("speedSlider value is " + speedSlider.valueProperty().intValue());
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
//                    System.out.println(pointsToAdd.isEmpty());
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
            //calculate response as a function of time and compound list

            List<Compound> dummyCompoundList= new ArrayList<>();
            populateDummyCompoundList(dummyCompoundList);
            int response = 0;

            for(Compound compound: dummyCompoundList) {
                double retentionTime = compound.getRetentionTime();

                //adjusted retention time: since we want peaks to elute as specific times,
                // we must subtract the current time since a gaussian distribution is centered at x = 0.
                //example retentionTime = 30s; when time = 30s, adjusted retention time will be zero (the max of the distribution)
                double adjustedRetentionTime = retentionTime-time;

                //we coiuld also use mu to shift peaks, but there is no need to at the moment
                //use sigma to account for peak broadening
                response += calculateGaussian(adjustedRetentionTime,0,10) * compound.getResponse();
            }
            response += NoiseGenerator.generateNoise();
            return response;
        }

        private void populateDummyCompoundList(List<Compound> compoundList){
            for(int i=0; i<5;i++){
                Compound compound = new Compound("CCCHHH");
                compound.setRetentionTime((double)30+30*i);
                compound.setResponse(i+1+0.5);
                compoundList.add(compound);

            }
        };

        private int calculateGaussian(double x, double mu, double sigma){
            int result = 0;

            double coefficient = 1/(Math.sqrt(2*Math.PI*sigma*sigma));
            double exponential = Math.exp(-(x-mu) * (x-mu)/(2*sigma*sigma));

            return (int)(coefficient*exponential*1000);
            //response of 1000 just for example, will be based on response factor for each specific compound
        }


    }


}
