package app.controller;

import app.support.BikeStatusHelper;
import dao.BikeDAO;
import dao.RideBookingDAO;
import model.Bike;
import model.RideBooking;
import model.ShareRideRequest;
import model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class RideBookingController extends BaseWebController {

    @GetMapping("/rideBookings")
    public String rideBookings(HttpSession session, Model model) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        BikeDAO bikeDAO = new BikeDAO(dataPath());
        List<Bike> bikes = bikeDAO.getAllBikes();
        List<RideBooking> allBookings = bookingDAO.getAllBookings();

        model.addAttribute("bikes", bikes);
        model.addAttribute("bookedBikeIds", getBookedBikeIds(allBookings));

        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            model.addAttribute("rideBookings", getBookingsForOperatorBikes(allBookings, bikes, currentUser.getUsername()));
        } else {
            model.addAttribute("rideBookings", bookingDAO.getBookingsByUsername(currentUser.getUsername()));
            model.addAttribute("shareableBookings", getShareableBookings(allBookings, currentUser.getUsername()));
        }

        return "ride-bookings";
    }

    @PostMapping("/addRideBooking")
    public String addRideBooking(@RequestParam int bikeId,
                                 @RequestParam int durationHours,
                                 @RequestParam String pickupPoint,
                                 @RequestParam String dropPoint,
                                 HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/bikes";
        }

        BikeDAO bikeDAO = new BikeDAO(dataPath());
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        Bike bike = bikeDAO.getBikeById(bikeId);
        if (bike == null || !"AVAILABLE".equalsIgnoreCase(bike.getStatus()) || bookingDAO.hasOngoingBookingForBike(bikeId)) {
            return "redirect:/rideBookings";
        }

        RideBooking booking = new RideBooking(0, bike.getId(), bike.getBikeName(), currentUser.getUsername(),
                durationHours, pickupPoint, dropPoint, "REQUESTED");
        bookingDAO.addBooking(booking);
        bike.setStatus("BOOKED");
        bikeDAO.updateBike(bike);
        return "redirect:/rideBookings";
    }

    @PostMapping("/updateRideBooking")
    public String updateRideBooking(@RequestParam int id,
                                    @RequestParam int bikeId,
                                    @RequestParam String bikeName,
                                    @RequestParam String username,
                                    @RequestParam int durationHours,
                                    @RequestParam String pickupPoint,
                                    @RequestParam String dropPoint,
                                    @RequestParam String status,
                                    HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) && !currentUser.getUsername().equals(username)) {
            return "redirect:/rideBookings";
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking existingBooking = bookingDAO.getBookingById(id);
        RideBooking updatedBooking = new RideBooking(id, bikeId, bikeName, username, durationHours, pickupPoint, dropPoint, status);
        if (existingBooking != null) {
            updatedBooking.setShareRequests(existingBooking.getShareRequests());
        }
        bookingDAO.updateBooking(updatedBooking);
        BikeStatusHelper.syncBikeStatus(dataPath(), bikeId);
        return "redirect:/rideBookings";
    }

    @GetMapping("/deleteRideBooking")
    public String deleteRideBooking(@RequestParam int id, HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/users";
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(id);
        if (booking == null) {
            return "redirect:/rideBookings";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) && !currentUser.getUsername().equals(booking.getUsername())) {
            return "redirect:/rideBookings";
        }

        bookingDAO.deleteBooking(id);
        BikeStatusHelper.syncBikeStatus(dataPath(), booking.getBikeId());
        return "redirect:/rideBookings";
    }

    @PostMapping("/requestShareRide")
    public String requestShareRide(@RequestParam int bookingId,
                                   @RequestParam String sharePickupPoint,
                                   @RequestParam String shareStopPoint,
                                   HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        String pickupPoint = sharePickupPoint == null ? null : sharePickupPoint.trim();
        String stopPoint = shareStopPoint == null ? null : shareStopPoint.trim();
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(bookingId);

        if (pickupPoint == null || pickupPoint.isEmpty() || stopPoint == null || stopPoint.isEmpty()
                || booking == null || !booking.isOngoing() || booking.isOwner(currentUser.getUsername())
                || booking.hasShareRequestFrom(currentUser.getUsername())) {
            return "redirect:/rideBookings";
        }

        booking.addShareRequest(new ShareRideRequest(currentUser.getUsername(), pickupPoint, stopPoint, "PENDING"));
        bookingDAO.updateBooking(booking);
        return "redirect:/rideBookings";
    }

    @PostMapping("/respondShareRideRequest")
    public String respondShareRideRequest(@RequestParam int bookingId,
                                          @RequestParam String requesterUsername,
                                          @RequestParam String decision,
                                          HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return "redirect:/rideBookings";
        }

        String nextStatus = "accept".equalsIgnoreCase(decision) ? "ACCEPTED" : "DECLINED";
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || !booking.isOwner(currentUser.getUsername()) || !booking.isOngoing()) {
            return "redirect:/rideBookings";
        }

        if (booking.updateShareRequestStatus(requesterUsername, nextStatus)) {
            if ("ACCEPTED".equalsIgnoreCase(nextStatus)) {
                booking.setStatus("CONFIRMED");
            }
            bookingDAO.updateBooking(booking);
        }
        return "redirect:/rideBookings";
    }

    private List<RideBooking> getShareableBookings(List<RideBooking> allBookings, String currentUsername) {
        List<RideBooking> shareableBookings = new ArrayList<RideBooking>();
        for (RideBooking booking : allBookings) {
            if (booking.isOngoing() && !booking.isOwner(currentUsername)) {
                shareableBookings.add(booking);
            }
        }
        return shareableBookings;
    }

    private Set<Integer> getBookedBikeIds(List<RideBooking> allBookings) {
        Set<Integer> bookedBikeIds = new HashSet<Integer>();
        for (RideBooking booking : allBookings) {
            if (booking.isOngoing()) {
                bookedBikeIds.add(booking.getBikeId());
            }
        }
        return bookedBikeIds;
    }

    private List<RideBooking> getBookingsForOperatorBikes(List<RideBooking> allBookings, List<Bike> bikes, String operatorUsername) {
        Set<Integer> operatorBikeIds = new HashSet<Integer>();
        for (Bike bike : bikes) {
            if (bike.getOperator() != null && bike.getOperator().equalsIgnoreCase(operatorUsername)) {
                operatorBikeIds.add(bike.getId());
            }
        }

        List<RideBooking> operatorBookings = new ArrayList<RideBooking>();
        for (RideBooking booking : allBookings) {
            if (operatorBikeIds.contains(booking.getBikeId())) {
                operatorBookings.add(booking);
            }
        }
        return operatorBookings;
    }
}
