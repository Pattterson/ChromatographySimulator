package com.chromasim.chromatographyhome;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LineChartController {

    private Controller controller;
    @FXML
    private LineChart<Number,Number> lineChart;

    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private GridPane chartContainer;

    final Rectangle zoomRect = new Rectangle();

    public void initialize(){
        xAxis.autoRangingProperty().setValue(false);
        yAxis.autoRangingProperty().setValue(false);
        lineChart.setVisible(true);

        zoomRect.setManaged(false);
        zoomRect.setFill(Color.LIGHTSEAGREEN.deriveColor(0, 1, 1, 0.5));
        chartContainer.getChildren().add(zoomRect);


        setUpZooming(zoomRect);
    }


    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Controller getController() {
        return controller;
    }

    public LineChart<Number,Number> getLineChart() {
        return lineChart;
    }

    public void setLineChart(LineChart lineChart) {
        this.lineChart = lineChart;
    }

    public NumberAxis getxAxis() {
        return xAxis;
    }

    public void setxAxis(NumberAxis xAxis) {
        this.xAxis = xAxis;
    }

    public NumberAxis getyAxis() {
        return yAxis;
    }

    public void setyAxis(NumberAxis yAxis) {
        this.yAxis = yAxis;
    }

    public GridPane getChartContainer() {
        return chartContainer;
    }

    public void setChartContainer(GridPane chartContainer) {
        this.chartContainer = chartContainer;
    }

    private void setUpZooming(final Rectangle rect) {

        final Node zoomingNode = lineChart;

        //two x,y points to define zooming rectange in lineChart coordinates
        double[][] zoomRange = new double[2][2];
        final ObjectProperty<Point2D> mouseAnchorSemiLocal = new SimpleObjectProperty<>();
        final ObjectProperty<Point2D> mouseAnchorScene = new SimpleObjectProperty<>();
        final ObjectProperty<Point2D> mouseAnchorLocal = new SimpleObjectProperty<>();

        zoomingNode.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {


                Point2D pointClicked = new Point2D(event.getSceneX(), event.getSceneY());

                //relative to top left of axial coordinate system
                double xPosInAxis = xAxis.sceneToLocal(new Point2D(pointClicked.getX(), 0)).getX();
                double yPosInAxis = yAxis.sceneToLocal(new Point2D(0, pointClicked.getY())).getY();


                //cartesian coordinates of event
                double x1 = xAxis.getValueForDisplay(xPosInAxis).doubleValue(); //coordinates
                double y1 = yAxis.getValueForDisplay(yPosInAxis).doubleValue(); //coordinates

//              //coordinates of initial clicked (for zooming)
                zoomRange[0][0] = x1;
                zoomRange[0][1] = y1;


                //local-cartesian coordinates
                //semiLocal-relative to gridPane
                //scene-relative to scene

                mouseAnchorSemiLocal.set(new Point2D(event.getX(), event.getY()));
                mouseAnchorScene.set(new Point2D(event.getSceneX(), event.getSceneY()));
                mouseAnchorLocal.set(new Point2D(x1, y1));

                rect.setWidth(0);
                rect.setHeight(0);

            }
        });
        zoomingNode.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

//                double x = event.getX();
//                double y = event.getY();
//                rect.setX(Math.min(x, mouseAnchorSemiLocal.get().getX()));
//                rect.setY(Math.min(y, mouseAnchorSemiLocal.get().getY()));
//                rect.setWidth(Math.abs(x - mouseAnchorSemiLocal.get().getX()));
//                rect.setHeight(Math.abs(y - mouseAnchorSemiLocal.get().getY()));

                Point2D pointInScene = new Point2D(event.getSceneX(), event.getSceneY());
                final ObjectProperty<Point2D> movingMouseAnchorSemiLocal = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> movingMouseAnchorScene = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> movingMouseAnchorLocal = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> x0PosScene = new SimpleObjectProperty<>();
                final ObjectProperty<Point2D> x0PosSemiLocal = new SimpleObjectProperty<>();

                movingMouseAnchorSemiLocal.set(new Point2D(event.getX(), event.getY()));
                movingMouseAnchorScene.set(new Point2D(event.getSceneX(), event.getSceneY()));


                //relative to left of scene
                double xPosInGridPane = xAxis.sceneToLocal(new Point2D(pointInScene.getX(), 0)).getX();
                double yPosInGridPane = yAxis.sceneToLocal(new Point2D(0, pointInScene.getY())).getY();

                double x2 = xAxis.getValueForDisplay(xPosInGridPane).doubleValue(); //coordinates
                double y2 = yAxis.getValueForDisplay(yPosInGridPane).doubleValue(); //coordinates
                movingMouseAnchorLocal.set(new Point2D(x2, y2));

                //relative to left of gridpane
                double xRect = event.getX();
                double yRect = event.getY();

                //rectange is drawn relative to parent gridPane
                double anchorX = mouseAnchorSemiLocal.get().getX();
                double anchorY = mouseAnchorSemiLocal.get().getY();

                //equivalent
//                    rect.setX(Math.min(mouseAnchorSemiLocal.get().getX(), movingMouseAnchorSemiLocal.get().getX()));

                //0.001: added to XRect since otherwise when dragging from lower right to upper left, rectangle node interferes
                //with setOnMouseClicked listener and listener will not fire
                rect.setX(Math.min(xRect+0.001, anchorX));
                rect.setY(Math.min(yRect, anchorY));
                rect.setWidth(Math.abs(xRect - anchorX));
                rect.setHeight(Math.abs(yRect - anchorY));

                zoomRange[1][0] = x2;
                zoomRange[1][1] = y2;
            }


        });

        zoomingNode.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.SECONDARY) {
                    if (event.getClickCount() == 2) {
                        xAxis.setAutoRanging(true);
                        yAxis.setAutoRanging(true);
                    }

                } else {
//                    System.out.println("we will zoom from " + zoomRange[0][0] + " , " + zoomRange[0][1] +
//                            " to " + zoomRange[1][0] + " , " + zoomRange[1][1]);
                    doZoom(zoomRange);
                }

            }
        });
    }

    private void doZoom(double[][] zoomRange) {

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);


        xAxis.setLowerBound(Math.min(zoomRange[0][0], zoomRange[1][0]));
        yAxis.setLowerBound(Math.min(zoomRange[0][1], zoomRange[1][1]));
        xAxis.setUpperBound(Math.max(zoomRange[0][0], zoomRange[1][0]));
        ;
        yAxis.setUpperBound(Math.max(zoomRange[0][1], zoomRange[1][1]));

        zoomRect.setWidth(0);
        zoomRect.setHeight(0);
    }


}
