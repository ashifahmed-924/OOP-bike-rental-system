package model;

public class Bike {
    private int id;
    private String bikeName;
    private String bikeType;
    private String station;
    private double hourlyRate;
    private String status;
    private String operator;

    public Bike() {}

    public Bike(int id, String bikeName, String bikeType, String station, double hourlyRate, String status, String operator) {
        this.id = id;
        this.bikeName = bikeName;
        this.bikeType = bikeType;
        this.station = station;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.operator = operator;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBikeName() { return bikeName; }
    public void setBikeName(String bikeName) { this.bikeName = bikeName; }

    public String getBikeType() { return bikeType; }
    public void setBikeType(String bikeType) { this.bikeType = bikeType; }

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }

    @Override
    public String toString() {
        return id + ":" + bikeName + ":" + bikeType + ":" + station + ":" + hourlyRate + ":" + status + ":" + operator;
    }
}
