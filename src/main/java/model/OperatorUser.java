package model;

// OOP: Inheritance - OperatorUser is a specialized type of User.
public class OperatorUser extends User {

    public OperatorUser(String username, String password) {
        super(username, password);
    }

    // OOP: Polymorphism - OperatorUser gives its own version of getRole().
    @Override
    public String getRole() {
        return "OPERATOR";
    }

    // OOP: Polymorphism - OperatorUser gives its own dashboard path.
    @Override
    public String getDashboardPath() {
        return "/bikes";
    }
}
