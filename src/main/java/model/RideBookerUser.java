package model;

// OOP: Inheritance - RideBookerUser is a specialized type of User.
public class RideBookerUser extends User {

    public RideBookerUser(String username, String password) {
        super(username, password);
    }

    // OOP: Polymorphism - RideBookerUser gives its own version of getRole().
    @Override
    public String getRole() {
        return "RIDE_BOOKER";
    }

    // OOP: Polymorphism - RideBookerUser gives its own dashboard path.
    @Override
    public String getDashboardPath() {
        return "/rideBookings";
    }
}
