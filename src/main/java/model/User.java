package model;

public abstract class User extends Person {

    protected User(String username, String password) {
        super(username, password);
    }

    @Override
    public String toString() {
        return username + ":" + password + ":" + getRole();
    }
}
