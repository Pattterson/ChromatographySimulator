package com.chromasim.chromatographyhome;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import org.xmlcml.euclid.Int;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;


public class DataIntegration implements Runnable {
    //inhibit
    //set threshold
    //set peak width
    //valley to valley
    //baseline continue

    //Hit floor, then check if input is greater than value
    private NavigableMap<Double,Double> inhibitList = new TreeMap<>();

    //key is start, value is end, if in between both then we get threshold from threshold map (Can make normal map?)
    private NavigableMap<Double,Double> thresholdRanges = new TreeMap<>();
    private NavigableMap<Double,Double> thresholds = new TreeMap<>();
    private NavigableMap<Double,Double> peakWidthRanges = new TreeMap<>();
    private NavigableMap<Double,Double> peakWidths = new TreeMap<>();
    List<DataBunch> dataBunchList=new ArrayList<>();


    private double peakWidth;
    private double samplingRate;
    private double[][] dataRegression;
    private int bunchingFactor = 17;
    private int liftoffThreshold;
    private int[] dataBunchedSlopes;
    private double[][] rawData;
    private double[][] bunchedData;
    private double[] slopeData;
    private double[] bunchedSlopeData;
    private ArrayList<Peak> peakList = new ArrayList<Peak>();
    private LineChart lineChart;
    private int defaultBunchingFactor;


    private double defaultPeakWidth;
    private double defaultThreshold;


    public DataIntegration(List<IntegrationEvent> eventList, double[][] rawData,String defaultPeakWidth,String defaultThreshold) {
        parseEventList(eventList);
        this.defaultPeakWidth =Double.parseDouble(defaultPeakWidth);
        this.defaultThreshold = Double.parseDouble(defaultThreshold);
        this.samplingRate = samplingRate;
        this.dataRegression = dataRegression;
        this.liftoffThreshold = liftoffThreshold;
        this.rawData = rawData;
        this.lineChart = lineChart;

        //Sampling rate is consistant throughout the run so we can calculate from difference in time between first two datapoints
        samplingRate = 1/(rawData[1][0]-rawData[0][0]);


        defaultBunchingFactor = (int)(peakWidth * samplingRate) / 15; //15 = number of points needed to define a peak- empirically derived
//        bunchingFactor = 4;
        bunchedData = new double[rawData.length / bunchingFactor + 1][2]; //data at end of array may be truncated
        System.out.println("rawdatalength = " + rawData.length);
        System.out.println("buncheddatalength = " + bunchedData.length);
        slopeData = new double[bunchedData.length - 1];
        bunchedSlopeData = new double[slopeData.length - 1];
    }


    private void parseEventList(List<IntegrationEvent> eventsList) {


        for (IntegrationEvent event:eventsList){
            String type = event.getEventType().getValue().toString();
            double eventStart = Double.parseDouble(event.getEventStartTime());
            double eventEnd = Double.parseDouble(event.getEventEndTime());
            double eventValue = Double.parseDouble(event.getEventValue());

            if (type=="Inhibit Integration"){
                inhibitList.put(eventStart, eventEnd);
            }
            else if (type=="Set Threshold"){
                thresholdRanges.put(eventStart,eventEnd);
                thresholds.put(eventStart,eventValue);
            }
            else if (type=="Set Peak Width"){
                peakWidthRanges.put(eventStart,eventEnd);
                peakWidths.put(eventStart,eventValue);


            }
        }

    }

    @Override
    public void run() {
        bunchData();
        slopeifyData();
        bunchslopeifyData();
        determinePeakStart();
        showIntegration();


    }

//    private void bunchData() {
//        long bunchedDataSize;
//        bunchedDataSize = calculateBunchedDataSize();
//        System.out.println("bunchingFactor = " + bunchingFactor);
//
//        for (int i = 0; i < rawData.length; i += bunchingFactor) {
//            //calculate bunching factor -implement tomorrow
//            double sumY = 0;
//            double sumX = 0;
//            for (int j = 0; j < bunchingFactor && i + j < rawData.length; j++) {
//                sumX += rawData[i + j][0];
//                sumY += rawData[i + j][1];
//            }
////            System.out.println("bunched data length = " + bunchedData.length);
//
////            System.out.println("i = " + i + " quotient = " + i/bunchingFactor);
//            bunchedData[i / bunchingFactor][0] = sumX / bunchingFactor;
//            bunchedData[i / bunchingFactor][1] = sumY / bunchingFactor;
//        }
////        for(int i=0; i<bunchedData.length; i++){
////            System.out.println("bunched data: " + bunchedData[i][0] +" , " + bunchedData[i][1]);
////        }
//
//    }

    //might do it all
    private void bunchData() {
        Double widthStart = peakWidthRanges.firstEntry().getKey();
        Double widthEnd = peakWidthRanges.firstEntry().getValue();

        //Reminder peakWidths are indexed by where they start
        double bunching = peakWidths.get(widthStart)*samplingRate/15;


        int i=0;
        double bunchedSumX=0;
        double bunchedSumY=0;
        double bunchedpointCounter;
        //knowing where each data bunch starts will be useful for integration
        double xStart=0;
        boolean firstFlag = true;

        //points in bunch is not nessesarily equal to bunch size due to edge conditions
        int pointsInBunch = 0;
        int numberBunched=0;

        while (i<rawData.length){

            //We use the default bunching factor if we are before or after a specified peak width segment
            //Null indicates there are no more specified peak width segments
            if(widthStart==null || rawData[i][0]<widthStart){

                //continue until widthStart(start of next region, end of current one
                   i= createDataBunches(i,widthStart,defaultBunchingFactor);
            }

            //When we are in a specified peak width segment, apply the special bunching factor
            if (!(widthStart==null) && rawData[i][0]>widthStart && rawData[i][0]<widthEnd ){
                bunchingFactor = calculateBunchingFactor(widthStart);
                i=createDataBunches(i,widthEnd,bunchingFactor);

                //Move on to next peak width region
                widthStart = peakWidthRanges.higherKey(widthStart);
                widthEnd = peakWidthRanges.get(widthStart);
            }


        }
    }


    private int createDataBunches(int i, double regionEnd,int bunchingFactor) {
        double bunchedSumX=0;
        double bunchedSumY=0;

        int pointsInBunch = 0;
        double xStart = rawData[i][0];
        while (rawData[i][0]<regionEnd && i<rawData.length){
            while (pointsInBunch<bunchingFactor && i<rawData.length && rawData[i][0]<regionEnd);
            bunchedSumX+=rawData[i][0];
            bunchedSumY+= rawData[i][1];
            pointsInBunch+=1;
            i+=1;
        }
        dataBunchList.add(new DataBunch(xStart,bunchedSumX,bunchedSumY,pointsInBunch));
        pointsInBunch=0;
        bunchedSumX=0;
        bunchedSumY=0;

        return i;
    }


    private int calculateBunchingFactor(double widthStart) {

        double peakWidth = peakWidths.get(widthStart);
        return (int) Math.round(peakWidth*samplingRate /15);
    }

    private void slopeifyData() {
        for (int i = 0; i < bunchedData.length - 1; i++) {
            double timeSum = 0;
            double responseSum = 0;
            System.out.println("bunched data[i][0] = " + bunchedData[i][0]);
            System.out.println("bunched data[i][1] = " + bunchedData[i + 1][0]);
            timeSum = bunchedData[i + 1][0] - bunchedData[i][0];

            System.out.println("timesum: " + timeSum);
            responseSum = bunchedData[i + 1][1] - bunchedData[i][1];  //redundant ∆T can be likened to bunching factor/sampling rate
            System.out.println("response Sum: " + responseSum);

            slopeData[i] = responseSum / timeSum;
        }
//        for(int i=0; i<slopeData.length;i++){
//            System.out.println("Slope data" + slopeData[i]);
//        }


    }

    private void bunchslopeifyData() {
        for (int i = 0; i < slopeData.length - 1; i++) {
            double slopesum = slopeData[i] + slopeData[i + 1];
            bunchedSlopeData[i] = slopesum / 2;
            System.out.println("bunched slope data: " + bunchedSlopeData[i]);
        }

    }

    private void determinePeakStart() {

        liftoffThreshold = 15;
        for (int i = 0; i < bunchedSlopeData.length; i++) {
            if (bunchedSlopeData[i] > liftoffThreshold) {
                Peak peak = new Peak();
                peakList.add(peak);
                peak.setPeakStartTime(bunchedData[i][0]);
                peak.setPeakStartResponse(bunchedData[i][1]);


                System.out.println("peak start: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
                i = determinePeakEnd(i, peak);
            }

        }
    }

    private int determinePeakEnd(int i, Peak peak) {
        boolean apexFlag = false;
        boolean endFlag = false;
        int touchdownThreshold = -15;
        while (!endFlag && i < bunchedData.length) {
            if (bunchedSlopeData[i] > 0) {
                i++;
            } else if (!apexFlag && bunchedSlopeData[i] < 0) {
                apexFlag = true;
                peak.setPeakApexTime(bunchedData[i][0]); //approximations for now, can sort through bunched points to find true apex
                peak.setPeakApexresponse(bunchedData[i][1]);
                System.out.println("peak apex: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
                while (bunchedSlopeData[i] > (double) touchdownThreshold) {
                    i++;
                }


            } else if (bunchedSlopeData[i] < touchdownThreshold) {
                i++;
            } else {  //else slope within touchdown threshold requirements
                endFlag = true;
                peak.setPeakEndTime(bunchedData[i][0]);
                peak.setPeakEndResponse(bunchedData[i][1]);
                System.out.println("peak end: " + bunchedData[i][0] + " , " + bunchedData[i][1]);
            }
        }

        return i;
    }

    //Move this elsewhere so we can make this class in separate package
    private void showIntegration() {

        for (Peak peak : peakList) {
            XYChart.Series integrationSeries = new XYChart.Series();
            ArrayList<XYChart.Data<Double, Integer>> pointsToAdd = new ArrayList<>();
            pointsToAdd.add(new XYChart.Data(peak.getPeakStartTime(), peak.getPeakStartResponse()));
            pointsToAdd.add(new XYChart.Data(peak.getPeakEndTime(), peak.getPeakEndResponse()));


            Platform.runLater(new Runnable() {

                @Override
                public void run() {

                    FXMLComponents.lineChart.applyCss();
                    integrationSeries.getData().addAll(pointsToAdd);
                    FXMLComponents.lineChart.getData().add(integrationSeries);

                    Node n = integrationSeries.getNode();
                    StringBuilder style = new StringBuilder();
                    style.append("-fx-stroke: red; \n  -fx-stroke-width: 3px;");
                    n.setStyle(style.toString());


                }
            });


        }

    }


}
