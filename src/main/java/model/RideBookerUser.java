package model;

public class RideBookerUser extends User {

    public RideBookerUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "RIDE_BOOKER";
    }

    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
