package model;

// OOP: Inheritance - RiderUser is a specialized type of User.
public class RiderUser extends User {

    public RiderUser(String username, String password) {
        super(username, password);
    }

    // OOP: Polymorphism - RiderUser gives its own version of getRole().
    @Override
    public String getRole() {
        return "RIDER";
    }

    // OOP: Polymorphism - RiderUser gives its own dashboard path.
    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
