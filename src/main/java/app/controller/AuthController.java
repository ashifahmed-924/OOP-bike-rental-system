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
// OOP: Inheritance - AuthController reuses common helper methods from BaseWebController.
public class AuthController extends BaseWebController {

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) throws IOException {
        UserDAO userDAO = new UserDAO(dataPath());
        User user = userDAO.loginUser(username, password);

        if (user == null) {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }

        session.setAttribute("currentUser", user);
        return redirectByRole(user);
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam(required = false) String role,
                           Model model) throws IOException {
        String effectiveRole = normalizePublicRole(role);

        UserDAO userDAO = new UserDAO(dataPath());
        boolean created = userDAO.registerUser(UserFactory.createUser(username, password, effectiveRole));
        if (!created) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    private String normalizePublicRole(String role) {
        if ("OPERATOR".equalsIgnoreCase(role)) {
            return "OPERATOR";
        }
        if ("RIDE_BOOKER".equalsIgnoreCase(role)) {
            return "RIDE_BOOKER";
        }
        if ("RIDE_SHARER".equalsIgnoreCase(role)) {
            return "RIDE_SHARER";
        }
        return "RIDE_SHARER";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/login";
    }
}
