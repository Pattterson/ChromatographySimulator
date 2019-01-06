package ChromatographyHome;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

public class DataIntegration implements Runnable {

    private int peakWidth;
    private int samplingRate;
    private double[][] dataRegression;
    private int bunchingFactor = 17;
    private int liftoffThreshold;
    private int[] dataBunchedSlopes;
    private double[][] rawData;
    private double[][]bunchedData;
    private double[]slopeData;
    private double[]bunchedSlopeData;
    private ArrayList<Peak> peakList = new ArrayList<Peak>();
    private LineChart lineChart;

    public DataIntegration(int peakWidth, int samplingRate, double[][] dataRegression, int liftoffThreshold, double[][] rawData, LineChart lineChart) {
        this.peakWidth = peakWidth;
        this.samplingRate = samplingRate;
        this.dataRegression = dataRegression;
        this.liftoffThreshold = liftoffThreshold;
        this.rawData = rawData;
        this.lineChart = lineChart;
        bunchingFactor = (peakWidth * samplingRate)/15; //15 = number of points needed to define a peak- empirically derived
       bunchingFactor = 4;


        bunchedData = new double[rawData.length/bunchingFactor + 1][2]; //data at end of array may be truncated
        System.out.println("rawdatalength = " + rawData.length);
        System.out.println("buncheddatalength = " + bunchedData.length);
        slopeData = new double[bunchedData.length-1];
        bunchedSlopeData = new double[slopeData.length-1];
    }

    @Override
    public void run() {
        bunchData();
        slopeifyData();
        bunchslopeifyData();
        determinePeakStart();
        showIntegration();


    }

    private void bunchData(){
        System.out.println("bunchingFactor = " + bunchingFactor);

        for(int i=0; i<rawData.length; i +=bunchingFactor){
            double sumY = 0;
            double sumX = 0;
            for(int j=0; j<bunchingFactor && i+j<rawData.length; j++){
                sumX +=rawData[i+j][0];
                sumY += rawData[i+j][1];
            }
//            System.out.println("bunched data length = " + bunchedData.length);

//            System.out.println("i = " + i + " quotient = " + i/bunchingFactor);
            bunchedData[i/bunchingFactor][0] = sumX/bunchingFactor;
            bunchedData [i/bunchingFactor][1] = sumY/bunchingFactor;
        }
//        for(int i=0; i<bunchedData.length; i++){
//            System.out.println("bunched data: " + bunchedData[i][0] +" , " + bunchedData[i][1]);
//        }

    }
    private void slopeifyData(){
        for(int i = 0; i<bunchedData.length-1; i++){
            double timeSum = 0;
            double responseSum = 0;
            System.out.println("bunched data[i][0] = " + bunchedData[i][0]);
            System.out.println("bunched data[i][1] = " + bunchedData[i+1][0]);
        timeSum = bunchedData[i+1][0] - bunchedData[i][0];

            System.out.println("timesum: " + timeSum);
        responseSum = bunchedData[i+1][1] - bunchedData[i][1];  //redundant ∆T can be likened to bunching factor/sampling rate
            System.out.println("response Sum: " + responseSum);

            slopeData[i]= responseSum/timeSum;
        }
//        for(int i=0; i<slopeData.length;i++){
//            System.out.println("Slope data" + slopeData[i]);
//        }



    }

    private void bunchslopeifyData(){
        for(int i = 0; i<slopeData.length -1; i++){
            double slopesum = slopeData[i] + slopeData[i+1];
            bunchedSlopeData[i] = slopesum /2;
            System.out.println("bunched slope data: " + bunchedSlopeData[i]);
        }

    }

    private void determinePeakStart(){

        liftoffThreshold = 15;
        for(int i=0; i<bunchedSlopeData.length; i++){
            if(bunchedSlopeData[i]>liftoffThreshold){
                Peak peak = new Peak();
                peakList.add(peak);
                peak.setPeakStartTime(bunchedData[i][0]);
                peak.setPeakStartResponse(bunchedData[i][1]);


                System.out.println("peak start: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
                i = determinePeakEnd(i,peak);
            }

        }
    }
    private int determinePeakEnd(int i, Peak peak){
        boolean apexFlag = false;
        boolean endFlag = false;
        int touchdownThreshold = -15;
        while(!endFlag && i<bunchedData.length){
             if(bunchedSlopeData[i]>0){
                i++;
            }
             else if(!apexFlag && bunchedSlopeData[i]<0){
                apexFlag = true;
                peak.setPeakApexTime(bunchedData[i][0]); //approximations for now, can sort through bunched points to find true apex
                peak.setPeakApexresponse(bunchedData[i][1]);
                 System.out.println("peak apex: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
                 while(bunchedSlopeData[i]>(double) touchdownThreshold){
                     i++;
                 }


            }

            else if (bunchedSlopeData[i]<touchdownThreshold){
                i++;
             }
             else {  //else slope within touchdown threshold requirements
                 endFlag = true;
                 peak.setPeakEndTime(bunchedData[i][0]);
                 peak.setPeakEndResponse(bunchedData[i][1]);
                 System.out.println("peak end: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
             }
        }

        return i;
    }

    private void showIntegration(){

        for(Peak peak: peakList){
            XYChart.Series integrationSeries = new XYChart.Series();
            ArrayList<XYChart.Data<Double, Integer>> pointsToAdd = new ArrayList<>();
            pointsToAdd.add(new XYChart.Data(peak.getPeakStartTime(),peak.getPeakStartResponse()));
            pointsToAdd.add(new XYChart.Data(peak.getPeakEndTime(),peak.getPeakEndResponse()));


            Platform.runLater(new Runnable() {

                @Override
                public void run() {

                    lineChart.applyCss();
                    integrationSeries.getData().addAll(pointsToAdd);
                    lineChart.getData().add(integrationSeries);

                        Node n = integrationSeries.getNode();
                            StringBuilder style = new StringBuilder();
                            style.append("-fx-stroke: black;");
                            n.setStyle(style.toString());



                }
            });


        }

    }




}
