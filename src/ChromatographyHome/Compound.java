package ChromatographyHome;

public class Compound {

    private String smiles;
    private Double retentionTime;
    private Double response;

    public Compound(String smiles) {
        this.smiles = smiles;
    }

    public String getSmiles() {
        return smiles;
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
