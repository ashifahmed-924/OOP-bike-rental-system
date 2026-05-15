package model;

// OOP: Inheritance - AdminUser is a specialized type of User.
public class AdminUser extends User {

    public AdminUser(String username, String password) {
        super(username, password);
    }

    // OOP: Polymorphism - AdminUser gives its own version of getRole().
    @Override
    public String getRole() {
        return "ADMIN";
    }

    // OOP: Polymorphism - AdminUser gives its own dashboard path.
    @Override
    public String getDashboardPath() {
        return "/users";
    }
}
