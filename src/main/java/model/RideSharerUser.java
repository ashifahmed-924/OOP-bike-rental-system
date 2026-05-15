package model;

// OOP: Inheritance - RideSharerUser is a specialized type of User.
public class RideSharerUser extends User {

    public RideSharerUser(String username, String password) {
        super(username, password);
    }

    // OOP: Polymorphism - RideSharerUser gives its own version of getRole().
    @Override
    public String getRole() {
        return "RIDE_SHARER";
    }

    // OOP: Polymorphism - RideSharerUser gives its own dashboard path.
    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
