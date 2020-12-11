package be.henallux.ig3.adm;

public class CostPerStationsNumber implements Comparable<CostPerStationsNumber> {
    private double cost;
    private int stationsNumber;

    public CostPerStationsNumber(double cost, int stationsNumber) {
        this.cost = cost;
        this.stationsNumber = stationsNumber;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getStationsNumber() {
        return stationsNumber;
    }

    public void setStationsNumber(int stationsNumber) {
        this.stationsNumber = stationsNumber;
    }

    @Override
    public int compareTo(CostPerStationsNumber o) {
        return Double.compare(getCost(), o.getCost());
    }

    @Override
    public String toString() {
        return "\n{Station " + stationsNumber + " : " + cost + "€}";
    }
}
