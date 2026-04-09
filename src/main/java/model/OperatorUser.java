package model;

public class OperatorUser extends User {

    public OperatorUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "OPERATOR";
    }

    @Override
    public String getDashboardPath() {
        return "/bikes";
    }
}
