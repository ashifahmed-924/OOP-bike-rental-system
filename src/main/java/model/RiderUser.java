package model;

public class RiderUser extends User {

    public RiderUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "RIDER";
    }

    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
