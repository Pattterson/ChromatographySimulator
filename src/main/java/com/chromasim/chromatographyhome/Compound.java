package com.chromasim.chromatographyhome;

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
        setRetentionTime(smiles.toString());
        setResponse(smiles.toString());
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

        //if smiles are updated, then we'll have to generate a response factor and a retention time
        setRetentionTime(smiles);
        setResponse(smiles);


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

    public void setRetentionTime(String smiles) {

        this.retentionTime = calculateRetentionTime(smiles);
    }



    public Double getResponse() {
        return response;
    }

    public void setResponse(String smiles) {
        this.response = calculateResponse(smiles);
    }

    //Sample method for now, will be made more robust in future
    private Double calculateResponse(String smiles) {




        return smiles.length() * 2000 * Math.random();
    }

    //Without  multidimentional statistical models such as QSRR, retention times can only be predicted with a low degree of accuracy.
    //Due to the nature of this program, low accuracy should not be an issue nor should it affect other parts of the software or calculations
    private Double calculateRetentionTime(String smiles) {
        return  Math.random()* 1750;
    }


}
