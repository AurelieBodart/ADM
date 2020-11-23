package be.henallux.ig3.adm;

public class Jump implements Comparable<Jump> {
    private int saut;
    private int ri;
    private double pi;
    private double npi;

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

    @Override
    public int compareTo(Jump o) {
        return Integer.compare(getSaut(), o.getSaut());
    }
}
