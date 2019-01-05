package sample;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;
import java.util.Random;

public class GenerateRandomData implements Runnable {
    Random rand = new Random();
    private LineChart lineChart;

    private XYChart.Series series;

    public GenerateRandomData(LineChart lineChart, XYChart.Series series) {

        this.lineChart = lineChart;
        this.series = series;
        lineChart.getData().addAll(series);
    }

    @Override
    public void run() {


        for(int j=0; j<50; j++) {
            ArrayList<XYChart.Data<Integer, Integer>> list = new ArrayList<>();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 30; i++) {
                list.add(new XYChart.Data(rand.nextInt(), rand.nextInt()));
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Integer fred = 0;
                    synchronized (fred) {
                        series.getData().addAll(list);

                    }

                }

            });
        }

    }

}













