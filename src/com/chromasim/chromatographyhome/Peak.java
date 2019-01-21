package com.chromasim.chromatographyhome;

public class Peak {
    private double peakStartTime;
    private double peakStartResponse;
    private double peakApexTime;
    private double peakApexresponse;
    private double peakEndTime;
    private double peakEndResponse;
    private double peakBaseline;

    public Peak() {
    }

    public void setPeakStartTime(double peakStartTime) {
        this.peakStartTime = peakStartTime;
    }

    public void setPeakStartResponse(double peakStartResponse) {
        this.peakStartResponse = peakStartResponse;
    }

    public void setPeakApexTime(double peakApexTime) {
        this.peakApexTime = peakApexTime;
    }

    public void setPeakApexresponse(double peakApexresponse) {
        this.peakApexresponse = peakApexresponse;
    }

    public void setPeakEndTime(double peakEndTime) {
        this.peakEndTime = peakEndTime;
        setPeakBaseline();
    }

    public void setPeakBaseline(){
        Baseline baseline = new Baseline(peakStartTime,peakEndTime,peakStartResponse,peakEndResponse);
    }

    public void setPeakEndResponse(double peakEndResponse) {
        this.peakEndResponse = peakEndResponse;
    }

    public double getPeakStartTime() {
        return peakStartTime;
    }

    public double getPeakStartResponse() {
        return peakStartResponse;
    }

    public double getPeakApexTime() {
        return peakApexTime;
    }

    public double getPeakApexresponse() {
        return peakApexresponse;
    }

    public double getPeakEndTime() {
        return peakEndTime;
    }

    public double getPeakEndResponse() {
        return peakEndResponse;
    }



    private class Baseline{
        public Baseline(double baselineStartTime, double baselineStartResponse, double baselineEndTime, double baselineEndResponse) {

            this.baselineStartTime = baselineStartTime;
            this.baselineStartResponse = baselineStartResponse;
            this.baselineEndTime = baselineEndTime;
            this.baselineEndResponse = baselineEndResponse;
            this.baselineSlope = calculateBaselineSlope();
        }

        private double baselineSlope;
        private double baselineStartTime;
        private double baselineStartResponse;
        private double baselineEndTime;
        private double baselineEndResponse;

        private double calculateBaselineSlope(){
            //two points for a line formula
            return (baselineEndResponse-baselineStartResponse)/(baselineEndTime-baselineStartTime);

        }

    }
}
