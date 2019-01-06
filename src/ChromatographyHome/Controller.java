package ChromatographyHome;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private static File file = new File("/Users/alexpatterson/IdeaProjects/ChromatographySimulator/src/ChromatographyHome/music.wav");
    private static final String MEDIA_URI = file.toURI().toString();
    private static Media sound = new Media(MEDIA_URI);

    @FXML
    private Label speedIndicator;

    private Task<ObservableList<XYChart.Data>> task;
    private static MediaPlayer mediaPlayer;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private LineChart<Number, Number> lineChart;

    @FXML
    private Button startButton;

    @FXML
    private Slider speedSlider;

    XYChart.Series series = new XYChart.Series();




//Not used
//    public void acquisitionSpeedChange(){
//        System.out.println("fired");
//        int speed = (int) speedSlider.getValue();
//        if (speed==1){
//            speedIndicator.setText("Real Time (1X)");
//        }
//        else {
//            speedIndicator.setText("Accelerated (" + speed +"X)");
//        }
//    }


    public void initialize() {
        xAxis.autoRangingProperty().setValue(true);
        yAxis.autoRangingProperty().setValue(true);


        lineChart.getData().add(series);
        lineChart.setVisible(true);
        startButton.setDisable(false);

        speedSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> observable, //
                                Number oldValue, Number newValue) {
                if (newValue.intValue() == 1) {
                    speedIndicator.setText("Real Time (1X)");
                } else {
                    speedIndicator.setText("Accelerated (" + newValue.intValue() + "X)");
                }
            }
        });

    }

    public void laceReboot() {
        lineChart.setVisible(true);
        startButton.setDisable(false);
        System.out.println(speedSlider.getValue());

    }

    public void buttonClicked() {
        startButton.setDisable(false);
        System.out.println(MEDIA_URI);


        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        System.out.println(sound.getDuration());
//        mediaPlayer.setOnError(() -> System.out.println("Error : " + mediaPlayer.getError().toString()));
        mediaPlayer.play();
        // For example


        List list1 = new ArrayList<>();
        List list2 = new ArrayList<>();
        Runnable generateData = new GenerateData(lineChart, series, list1, list2, speedSlider);
        Thread thread = new Thread(generateData);
        thread.setDaemon(true);
        thread.start();


    }

    public void integrate() {

        //The easiest way to start will be to convert series into array and process the array
        //Eventual goal is to process as a series without explicit conversion to an array
        XYChart.Series<Number, Number> series2 = lineChart.getData().get(0);
        double[][] rawData = new double[series2.getData().size()][2];


        for (int i = 0; i < series2.getData().size(); i++) {
            rawData[i][0] = series2.getData().get(i).getXValue().doubleValue();
            rawData[i][1] = series2.getData().get(i).getYValue().doubleValue();
        }
//        Runnable dataRegression = new DataRegression(RawData,4);
//        Thread thread = new Thread(dataRegression);
//        thread.setDaemon(true);
//        thread.start();

        Runnable dataIntegration = new DataIntegration(4,5,rawData,3,rawData,lineChart);
        Thread thread = new Thread(dataIntegration);
        thread.setDaemon(true);
        thread.start();





    }

    public void button3Clicked() {


    }
}


