package model;

public final class UserFactory {

    private UserFactory() {
    }

    public static User createUser(String username, String password, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return new AdminUser(username, password);
        }
        if ("OPERATOR".equalsIgnoreCase(role)) {
            return new OperatorUser(username, password);
        }
        if ("RIDE_SHARER".equalsIgnoreCase(role)) {
            return new RideSharerUser(username, password);
        }
        if ("RIDE_BOOKER".equalsIgnoreCase(role)) {
            return new RideBookerUser(username, password);
        }
        return new RiderUser(username, password);
    }
}
