package ChromatographyHome;

import javafx.beans.property.SimpleStringProperty;

public class Compound {


    private Double retentionTime;
    private Double response;
    private String name = "";
    private String number = "";
    private SimpleStringProperty smiles = new SimpleStringProperty("");
    private String concentration = "";
    private String offset ="";

    public Compound(SimpleStringProperty smiles) {
        this.smiles = smiles;
    }


    public String getName() {
        return name;

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSmiles(String smiles) {
        this.smiles = new SimpleStringProperty(smiles);
    }

    public String getConcentration() {
        return concentration;
    }

    public void setConcentration(String concentration) {
        this.concentration = concentration;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }


    public String getSmiles() {

        return smiles.get();
    }

    public Double getRetentionTime() {
        return retentionTime;
    }

    public void setRetentionTime(Double retentionTime) {
        this.retentionTime = retentionTime;
    }

    public Double getResponse() {
        return response;
    }

    public void setResponse(Double response) {
        this.response = response;
    }
}
