package model;

public class RideSharerUser extends User {

    public RideSharerUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "RIDE_SHARER";
    }

    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
