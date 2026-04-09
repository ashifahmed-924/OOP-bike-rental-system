package app.controller;

import dao.BikeDAO;
import model.Bike;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class BikeController extends BaseWebController {

    @GetMapping("/bikes")
    public String bikes(HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        java.util.List<Bike> bikes = new BikeDAO(dataPath()).getAllBikes();
        int myBikeCount = 0;
        for (Bike bike : bikes) {
            if (currentUser.getUsername().equals(bike.getOperator())) {
                myBikeCount++;
            }
        }

        model.addAttribute("bikes", bikes);
        model.addAttribute("myBikeCount", myBikeCount);
        return "bikes";
    }

    @PostMapping("/addBike")
    public String addBike(@RequestParam String bikeName,
                          @RequestParam String bikeType,
                          @RequestParam String station,
                          @RequestParam double hourlyRate,
                          @RequestParam String status,
                          HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        Bike bike = new Bike(0, bikeName, bikeType, station, hourlyRate, status, currentUser.getUsername());
        new BikeDAO(dataPath()).addBike(bike);
        return "redirect:/bikes";
    }

    @PostMapping("/updateBike")
    public String updateBike(@RequestParam int id,
                             @RequestParam String bikeName,
                             @RequestParam String bikeType,
                             @RequestParam String station,
                             @RequestParam double hourlyRate,
                             @RequestParam String status,
                             HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        BikeDAO bikeDAO = new BikeDAO(dataPath());
        Bike existingBike = bikeDAO.getBikeById(id);
        if (existingBike == null || !currentUser.getUsername().equals(existingBike.getOperator())) {
            return "redirect:/bikes";
        }

        bikeDAO.updateBike(new Bike(id, bikeName, bikeType, station, hourlyRate, status, existingBike.getOperator()));
        return "redirect:/bikes";
    }

    @GetMapping("/deleteBike")
    public String deleteBike(@RequestParam int id, HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        BikeDAO bikeDAO = new BikeDAO(dataPath());
        Bike existingBike = bikeDAO.getBikeById(id);
        if (existingBike == null || !currentUser.getUsername().equals(existingBike.getOperator())) {
            return "redirect:/bikes";
        }

        bikeDAO.deleteBike(id);
        return "redirect:/bikes";
    }
}
