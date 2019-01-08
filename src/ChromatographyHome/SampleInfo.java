package ChromatographyHome;

import javafx.scene.control.ComboBox;

import java.util.Map;

public class SampleInfo {

    static Integer sampleCounter = 1;
    private int sampleNumber;
    private String sampleName;
    private ComboBox<String> sampleType = new ComboBox<>();
    private Double injectionVolume;
    private Map<Compound,Double> sampleCompounds;

    public SampleInfo(String sampleName, Double injectionVolume) {
        sampleNumber = sampleCounter;
        sampleCounter ++;
        sampleType.getItems().add("Sample");
        sampleType.getItems().add("Standard");
        sampleType.getItems().add("Blank");

        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.injectionVolume = injectionVolume;

    }


    public static Integer getSampleCounter() {
        return sampleCounter;
    }

    public static void setSampleCounter(Integer sampleCounter) {
        SampleInfo.sampleCounter = sampleCounter;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public ComboBox<String> getSampleType() {
        return sampleType;
    }

    public void setSampleType(ComboBox<String> sampleType) {
        this.sampleType = sampleType;
    }

    public Double getInjectionVolume() {
        return injectionVolume;
    }

    public void setInjectionVolume(Double injectionVolume) {
        this.injectionVolume = injectionVolume;
    }

    public Map<Compound, Double> getSampleCompounds() {
        return sampleCompounds;
    }

    public void setSampleCompounds(Map<Compound, Double> sampleCompounds) {
        this.sampleCompounds = sampleCompounds;
    }
}
