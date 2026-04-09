package model;

public class AdminUser extends User {

    public AdminUser(String username, String password) {
        super(username, password);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDashboardPath() {
        return "/users";
    }
}
