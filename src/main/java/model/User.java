package model;

// OOP: Inheritance - User gets username/password behavior from Person using extends.
public abstract class User extends Person {

    protected User(String username, String password) {
        // OOP: Inheritance - super calls the parent class constructor.
        super(username, password);
    }

    // OOP: Polymorphism - this overrides Object.toString(), so a User prints in this custom format.
    @Override
    public String toString() {
        return username + ":" + password + ":" + getRole();
    }

    public String getRoleDisplayName() {
        // OOP: Polymorphism - getRole() runs the version from the real child object, such as AdminUser or RiderUser.
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
