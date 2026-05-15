package model;

public class Bike {
    // OOP: Encapsulation - bike data is private and is accessed through getters and setters.
    private int id;
    private String bikeName;
    private String bikeType;
    private String station;
    private double hourlyRate;
    private String status;
    private String operator;
    private String phoneNumber;

    public Bike() {}

    // OOP: Constructor overloading - same constructor name, but different parameter lists.
    public Bike(int id, String bikeName, String bikeType, String station, double hourlyRate, String status, String operator) {
        this(id, bikeName, bikeType, station, hourlyRate, status, operator, "");
    }

    // OOP: Constructor overloading - this version accepts all bike details, including phoneNumber.
    public Bike(int id, String bikeName, String bikeType, String station, double hourlyRate, String status, String operator, String phoneNumber) {
        this.id = id;
        this.bikeName = bikeName;
        this.bikeType = bikeType;
        this.station = station;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.operator = operator;
        this.phoneNumber = phoneNumber;
    }

    // OOP: Encapsulation - getters and setters control how outside code reads or changes private fields.
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

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // OOP: Polymorphism - this overrides Object.toString() with a Bike-specific text format.
    @Override
    public String toString() {
        return id + ":" + bikeName + ":" + bikeType + ":" + station + ":" + hourlyRate + ":" + status + ":" + operator + ":" + phoneNumber;
    }
}
