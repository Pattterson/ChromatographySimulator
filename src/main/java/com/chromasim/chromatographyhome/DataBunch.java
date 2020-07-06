package com.chromasim.chromatographyhome;

public class DataBunch {

    private double averageX;
    private double averageY;
    private double xStart;
    private int pointsInBunch;


    public DataBunch(double xStart, double bunchedSumX, double bunchedSumY, int pointsInBunch) {
        this.averageX = bunchedSumX/pointsInBunch;
        this.averageY = bunchedSumY/pointsInBunch;
        this.xStart = xStart;
        this.pointsInBunch = pointsInBunch;
    }

    public double getAverageX() {
        return averageX;
    }

    public double getAverageY() {
        return averageY;
    }

    public double getxStart() {
        return xStart;
    }
}