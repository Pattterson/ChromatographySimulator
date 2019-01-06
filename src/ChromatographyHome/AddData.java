package ChromatographyHome;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class AddData implements Runnable {

    private LineChart lineChart;
    private XYChart.Series series;

    public AddData(LineChart lineChart, XYChart.Series series) {

        this.lineChart = lineChart;
        this.series = series;
    }

    @Override
    public void run() {

        lineChart.getData().addAll(series);

        Platform.runLater(new Runnable() {
            @Override

            public void run() {



                series.getData().addAll();

            }
        });




    }
}
