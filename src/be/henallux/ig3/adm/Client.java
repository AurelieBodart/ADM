package be.henallux.ig3.adm;

public class Client {
    private String type;
    private int serviceDuration;
    private int systemEntry;
    private boolean ejected;

    public Client(String type, int serviceDuration, int systemEntry, boolean ejected) {
        this.type = type;
        this.serviceDuration = serviceDuration;
        this.systemEntry = systemEntry;
        this.ejected = ejected;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getServiceDuration() {
        return serviceDuration;
    }

    public void decrementServiceDuration() {
        this.serviceDuration--;
    }

    public int getSystemEntry() {
        return systemEntry;
    }

    public void setSystemEntry(int systemEntry) {
        this.systemEntry = systemEntry;
    }

    public void setIsEjected(boolean ejected) {
        this.ejected = ejected;
    }

    public boolean isEjected() {
        return ejected;
    }
}
