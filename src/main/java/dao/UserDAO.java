package dao;

import model.User;
import model.UserFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final File file;

    public UserDAO(String basePath) {
        File dataDirectory = DataDirectoryResolver.resolve(basePath);
        this.file = new File(dataDirectory, "users.txt");
    }

    // CREATE - Register new user
    public boolean registerUser(User user) throws IOException {
        // Check if username already exists
        if (findUserByUsername(user.getUsername()) != null) {
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(user.toString());
            writer.newLine();
            return true;
        }
    }

    // READ - Login
    public User loginUser(String username, String password) throws IOException {
        if (!file.exists()) {
            return null;
        }

        for (User user : getAllUsers()) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws IOException {
        List<User> users = new ArrayList<>();
        if (!file.exists()) {
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    users.add(UserFactory.createUser(parts[0], parts[1], parts[2]));
                }
            }
        }

        return users;
    }

    public User findUserByUsername(String username) throws IOException {
        for (User user : getAllUsers()) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public List<User> searchUsers(String keyword) throws IOException {
        List<User> matches = new ArrayList<>();
        String searchTerm = keyword == null ? "" : keyword.trim().toLowerCase();

        for (User user : getAllUsers()) {
            if (searchTerm.isEmpty() ||
                    user.getUsername().toLowerCase().contains(searchTerm) ||
                    user.getRole().toLowerCase().contains(searchTerm)) {
                matches.add(user);
            }
        }

        return matches;
    }

    public boolean updateUser(String originalUsername, User updatedUser) throws IOException {
        List<User> users = getAllUsers();

        for (User user : users) {
            if (!originalUsername.equals(updatedUser.getUsername()) &&
                    user.getUsername().equals(updatedUser.getUsername())) {
                return false;
            }
        }

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(originalUsername)) {
                users.set(i, updatedUser);
                rewriteFile(users);
                return true;
            }
        }

        return false;
    }

    public boolean deleteUser(String username) throws IOException {
        List<User> users = getAllUsers();
        boolean removed = users.removeIf(user -> user.getUsername().equals(username));

        if (removed) {
            rewriteFile(users);
        }

        return removed;
    }

    private void rewriteFile(List<User> users) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User user : users) {
                writer.write(user.toString());
                writer.newLine();
            }
        }
    }
}
