package model;

public class User extends Person {

    private String role;

    public User(String username, String password, String role) {
        super(username, password);
        this.role = role;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return username + ":" + password + ":" + role;
    }
}