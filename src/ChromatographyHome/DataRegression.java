package ChromatographyHome;

// moving linear regression using least squares analysis, see https://www.mathsisfun.com/data/least-squares-regression.html
//use to implement traditional (Waters) integration https://www.waters.com/webassets/cms/library/docs/local_seminar_presentations/DA_NUT2013_A4_Rune_Frederiksen.pdf
public class DataRegression implements Runnable {

    private double[][] datapoints;
    private int peakwidth;
    private double[][] mAndBArray;

    public DataRegression(double[][] datapoints, int peakwidth) {
        this.datapoints = datapoints; //array of all data
        this.peakwidth = peakwidth; //number of points in regression analysis
        this.mAndBArray = new double[datapoints.length-peakwidth+1][2];
        System.out.println("MANDBARRAYLENGTH=" + mAndBArray.length);
    }

    @Override
    public void run() {
        doRegressionAnalysis();
        for(int i=0; i<8997;i++)
            System.out.println(getmAndBArray()[i][1]);
    }

    public double[][] getmAndBArray() {
        return mAndBArray;
    }

    public void doRegressionAnalysis(){
        System.out.println("datapoints.length= " + datapoints.length);
        double[][] regression = new double[datapoints.length][4];
        System.out.println("regression array has " + datapoints.length + " rows"
        );

        for(int i=0; i<regression.length; i++){
            regression[i][0] = datapoints[i][0]; //x note: adding x,y to array not really needed and might be removed, currently done this way to improve readability
            regression[i][1] = datapoints[i][1]; //y
            regression[i][2] = datapoints[i][0] * datapoints[i][0]; //x^2
            regression[i][3] = datapoints[i][0] * datapoints[i][1] ; //xy
        }

        for(int i=0; i<datapoints.length-peakwidth+1; i++){
            mAndBArray[i][0] = calculateM(peakwidth,i,regression);
//            mAndBArray[i][1] = calculateB(peakwidth,i,regression,mAndBArray[i][0]);

        }



    }
    private double calculateM(int peakwidth, int startingIndex, double[][] regression ){
        double n = peakwidth;
        double sumX=0;
        double sumY=0;
        double sumXY=0;

        double sumXSquared = sumX * sumX;
        for(int i=startingIndex; i<startingIndex+ peakwidth; i++){
            sumX+=regression[i][0];
//            System.out.println("adding " + regression[i][0] + "To sumS");
            sumY+=regression[i][1];
            sumXSquared+=regression[i][2];
            sumXY+= regression[i][3];
        }
//        For debugging
//        System.out.println("___________");
//        System.out.println(n);
//        System.out.println(sumXSquared);
//        System.out.println(sumX);

        double m = (n*sumXY-sumX*sumY)/(n*sumXSquared-sumX*sumX);
//        System.out.println("m= " + m);
        return m;


    }
    //don't think we'll even need this, only slope is important
    private double calculateB(int peakwidth, int startingIndex, double[][] regression, double m){
        int n=peakwidth;
        double sumX=0;
        double sumY=0;
        for(int i=startingIndex; i<startingIndex+ peakwidth; i++){
            sumX+=regression[i][0];
            sumY+=regression[i][1];
        }
        //System.out.println("m= " + m);
        double b = (sumY - m*sumX)/n;
        return b;


    }

    @Override
    public String toString() {

        return "";
    }
}

