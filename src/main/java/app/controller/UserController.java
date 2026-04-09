package app.controller;

import dao.UserDAO;
import model.User;
import model.UserFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class UserController extends BaseWebController {

    @GetMapping("/users")
    public String users(@RequestParam(required = false) String keyword,
                        HttpSession session,
                        Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return redirectByRole(currentUser);
        }

        UserDAO userDAO = new UserDAO(dataPath());
        model.addAttribute("users", userDAO.searchUsers(keyword));
        model.addAttribute("keyword", keyword == null ? "" : keyword);
        return "users";
    }

    @PostMapping("/addUser")
    public String addUser(@RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String role,
                          HttpSession session,
                          Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login";
        }

        UserDAO userDAO = new UserDAO(dataPath());
        boolean created = userDAO.registerUser(UserFactory.createUser(username, password, role));
        if (!created) {
            model.addAttribute("error", "Username already exists.");
            model.addAttribute("users", userDAO.getAllUsers());
            model.addAttribute("keyword", "");
            return "users";
        }

        return "redirect:/users";
    }

    @PostMapping("/updateUser")
    public String updateUser(@RequestParam String originalUsername,
                             @RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String role,
                             HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login";
        }

        UserDAO userDAO = new UserDAO(dataPath());
        boolean updated = userDAO.updateUser(originalUsername, UserFactory.createUser(username, password, role));
        if (!updated) {
            return "redirect:/edit-user?username=" + originalUsername;
        }

        if (currentUser.getUsername().equals(originalUsername)) {
            session.setAttribute("currentUser", UserFactory.createUser(username, password, role));
        }

        return "redirect:/users";
    }

    @GetMapping("/deleteUser")
    public String deleteUser(@RequestParam String username, HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/login";
        }

        if (!username.equals(currentUser.getUsername())) {
            new UserDAO(dataPath()).deleteUser(username);
        }
        return "redirect:/users";
    }
}
