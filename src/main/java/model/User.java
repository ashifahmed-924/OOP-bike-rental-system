package model;

public abstract class User extends Person {

    protected User(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return username + ":" + password + ":" + getRole();
    }

    public String getRoleDisplayName() {
        String role = getRole();
        if ("OPERATOR".equalsIgnoreCase(role)) {
            return "Bike Owner / Rental Provider";
        }
        if ("RIDE_SHARER".equalsIgnoreCase(role)) {
            return "Ride Sharer";
        }
        if ("RIDE_BOOKER".equalsIgnoreCase(role)) {
            return "Ride Booker / Passenger";
        }
        if ("ADMIN".equalsIgnoreCase(role)) {
            return "Admin";
        }
        return "Rider";
    }
}
