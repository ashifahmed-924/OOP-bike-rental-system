package app.controller;

import dao.BikeDAO;
import dao.RideBookingDAO;
import dao.UserDAO;
import model.Bike;
import model.RideBooking;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PageController extends BaseWebController {

    @GetMapping("/")
    public String home(HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        BikeDAO bikeDAO = new BikeDAO(dataPath());
        List<Bike> allBikes = bikeDAO.getAllBikes();
        List<Bike> availableBikes = new ArrayList<Bike>();
        for (Bike bike : allBikes) {
            if ("AVAILABLE".equalsIgnoreCase(bike.getStatus())) {
                availableBikes.add(bike);
            }
        }

        model.addAttribute("allBikes", allBikes);
        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("dashboardLink", dashboardLink(currentUser));
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/add-bike")
    public String addBikePage(HttpSession session) {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return redirectByRole(currentUser);
        }
        return "add-bike";
    }

    @GetMapping("/edit-bike")
    public String editBikePage(@RequestParam int id, HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return redirectByRole(currentUser);
        }

        Bike bike = new BikeDAO(dataPath()).getBikeById(id);
        if (bike == null || !currentUser.getUsername().equals(bike.getOperator())) {
            return "redirect:/bikes";
        }
        model.addAttribute("bike", bike);
        return "edit-bike";
    }

    @GetMapping("/edit-user")
    public String editUserPage(@RequestParam String username, HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return redirectByRole(currentUser);
        }

        User managedUser = new UserDAO(dataPath()).findUserByUsername(username);
        if (managedUser == null) {
            return "redirect:/users";
        }
        model.addAttribute("managedUser", managedUser);
        return "edit-user";
    }

    @GetMapping("/edit-ride-booking")
    public String editRideBookingPage(@RequestParam int id, HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }

        RideBooking booking = new RideBookingDAO(dataPath()).getBookingById(id);
        if (booking == null) {
            return "redirect:/rideBookings";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) && !currentUser.getUsername().equals(booking.getUsername())) {
            return "redirect:/rideBookings";
        }
        model.addAttribute("booking", booking);
        return "edit-ride-booking";
    }
}
