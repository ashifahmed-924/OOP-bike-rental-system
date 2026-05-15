package model;

// OOP: Abstraction - this class gives common user details, but it is not complete enough to create directly.
public abstract class Person {
    // OOP: Encapsulation - these fields are kept inside the class and accessed through methods, not directly.
    protected String username;
    protected String password;

    public Person(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // OOP: Abstraction - child classes must provide their own role value.
    public abstract String getRole();
    // OOP: Abstraction - child classes must provide their own dashboard page.
    public abstract String getDashboardPath();
}
