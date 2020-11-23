package be.henallux.ig3.adm;

public class Jump implements Comparable<Jump> {
    private int saut;
    private int ri;
    private double pi;
    private double npi;
    private double partialX2Observable;

    public Jump(int saut, int ri) {
        this.saut = saut;
        this.ri = ri;
    }

    public int getSaut() {
        return saut;
    }

    public void setSaut(int saut) {
        this.saut = saut;
    }

    public int getRi() {
        return ri;
    }

    public void setRi(int ri) {
        this.ri = ri;
    }

    public double getPi() {
        return pi;
    }

    public void setPi(double pi) {
        this.pi = pi;
    }

    public double getNpi() {
        return npi;
    }

    public void setNpi(double npi) {
        this.npi = npi;
    }

    public double getPartialX2Observable() {
        return partialX2Observable;
    }

    public void setPartialX2Observable(double partialX2Observable) {
        this.partialX2Observable = partialX2Observable;
    }

    @Override
    public int compareTo(Jump o) {
        return Integer.compare(getSaut(), o.getSaut());
    }
}
