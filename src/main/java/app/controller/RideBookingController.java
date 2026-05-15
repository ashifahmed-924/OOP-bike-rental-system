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
// OOP: Inheritance - RideBookingController reuses common helper methods from BaseWebController.
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

        model.addAttribute("canBookBikes", canBookBikes(currentUser));
        model.addAttribute("canJoinSharedRides", canJoinSharedRides(currentUser));
        model.addAttribute("canManageShareRequests", canManageShareRequests(currentUser));

        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            List<RideBooking> operatorBookings = getBookingsForOperatorBikes(allBookings, bikes, currentUser.getUsername());
            model.addAttribute("rideBookings", operatorBookings);
            model.addAttribute("pendingRentalRequests", getBookingsByStatus(operatorBookings, "PENDING_OWNER_APPROVAL"));
        } else if ("RIDE_BOOKER".equalsIgnoreCase(currentUser.getRole())) {
            model.addAttribute("rideBookings", getJoinedBookings(allBookings, currentUser.getUsername()));
            model.addAttribute("shareableBookings", getShareableBookings(allBookings, currentUser.getUsername()));
        } else {
            List<RideBooking> userBookings = bookingDAO.getBookingsByUsername(currentUser.getUsername());
            model.addAttribute("rideBookings", userBookings);
            model.addAttribute("pendingRentalRequests", getBookingsByStatus(userBookings, "PENDING_OWNER_APPROVAL"));
            model.addAttribute("shareableBookings", getShareableBookings(allBookings, currentUser.getUsername()));
        }

        return "ride-bookings";
    }

    @PostMapping("/addRideBooking")
    public String addRideBooking(@RequestParam int bikeId,
                                 @RequestParam int durationHours,
                                 @RequestParam String pickupPoint,
                                 @RequestParam String dropPoint,
                                 @RequestParam String rentalPickupTime,
                                 @RequestParam String rentalReturnTime,
                                 @RequestParam String renterPhoneNumber,
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
        if (!canBookBikes(currentUser)) {
            return "redirect:/rideBookings";
        }

        BikeDAO bikeDAO = new BikeDAO(dataPath());
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        Bike bike = bikeDAO.getBikeById(bikeId);
        if (bike == null || !"AVAILABLE".equalsIgnoreCase(bike.getStatus()) || bookingDAO.hasOngoingBookingForBike(bikeId)
                || isBlank(rentalPickupTime) || isBlank(rentalReturnTime) || isBlank(renterPhoneNumber)) {
            return "redirect:/rideBookings";
        }

        RideBooking booking = new RideBooking(0, bike.getId(), bike.getBikeName(), currentUser.getUsername(),
                durationHours, pickupPoint, dropPoint, "PENDING_OWNER_APPROVAL",
                rentalPickupTime.trim(), rentalReturnTime.trim(), renterPhoneNumber.trim(), new ArrayList<ShareRideRequest>());
        bookingDAO.addBooking(booking);
        bike.setStatus("BOOKED");
        bikeDAO.updateBike(bike);
        return "redirect:/rideBookings";
    }

    @PostMapping("/respondBikeRentalRequest")
    public String respondBikeRentalRequest(@RequestParam int bookingId,
                                           @RequestParam String decision,
                                           HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            return redirectByRole(currentUser);
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        BikeDAO bikeDAO = new BikeDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || !"PENDING_OWNER_APPROVAL".equalsIgnoreCase(booking.getStatus())) {
            return "redirect:/rideBookings";
        }

        Bike bike = bikeDAO.getBikeById(booking.getBikeId());
        if (bike == null || bike.getOperator() == null || !bike.getOperator().equalsIgnoreCase(currentUser.getUsername())) {
            return "redirect:/rideBookings";
        }

        if ("accept".equalsIgnoreCase(decision)) {
            booking.setStatus("OPEN_TO_SHARE");
            bookingDAO.updateBooking(booking);
            bike.setStatus("BOOKED");
            bikeDAO.updateBike(bike);
        } else {
            booking.setStatus("CANCELLED");
            bookingDAO.updateBooking(booking);
            BikeStatusHelper.syncBikeStatus(dataPath(), booking.getBikeId());
        }

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
            updatedBooking.setRentalPickupTime(existingBooking.getRentalPickupTime());
            updatedBooking.setRentalReturnTime(existingBooking.getRentalReturnTime());
            updatedBooking.setRenterPhoneNumber(existingBooking.getRenterPhoneNumber());
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
                                   @RequestParam String sharePhoneNumber,
                                   HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "OPERATOR".equalsIgnoreCase(currentUser.getRole())
                || !canJoinSharedRides(currentUser)) {
            return "redirect:/rideBookings";
        }

        String pickupPoint = sharePickupPoint == null ? null : sharePickupPoint.trim();
        String stopPoint = shareStopPoint == null ? null : shareStopPoint.trim();
        String phoneNumber = sharePhoneNumber == null ? null : sharePhoneNumber.trim();
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(bookingId);

        if (pickupPoint == null || pickupPoint.isEmpty() || stopPoint == null || stopPoint.isEmpty()
                || phoneNumber == null || phoneNumber.isEmpty()
                || booking == null || !booking.isShareable() || booking.isOwner(currentUser.getUsername())
                || booking.hasShareRequestFrom(currentUser.getUsername())) {
            return "redirect:/rideBookings";
        }

        booking.addShareRequest(new ShareRideRequest(currentUser.getUsername(), pickupPoint, stopPoint, "PENDING", phoneNumber, ""));
        bookingDAO.updateBooking(booking);
        return "redirect:/rideBookings";
    }

    @PostMapping("/respondShareRideRequest")
    public String respondShareRideRequest(@RequestParam int bookingId,
                                          @RequestParam String requesterUsername,
                                          @RequestParam String decision,
                                          @RequestParam(required = false) String passengerPickupTime,
                                          HttpSession session) throws IOException {
        User currentUser = currentUser(session);
        if (currentUser == null) {
            return "redirect:/login";
        }
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "OPERATOR".equalsIgnoreCase(currentUser.getRole())
                || !canManageShareRequests(currentUser)) {
            return "redirect:/rideBookings";
        }

        String nextStatus = "accept".equalsIgnoreCase(decision) ? "ACCEPTED" : "DECLINED";
        String pickupTime = passengerPickupTime == null ? "" : passengerPickupTime.trim();
        if ("ACCEPTED".equalsIgnoreCase(nextStatus) && pickupTime.isEmpty()) {
            return "redirect:/rideBookings";
        }
        RideBookingDAO bookingDAO = new RideBookingDAO(dataPath());
        RideBooking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null || !booking.isOwner(currentUser.getUsername()) || !booking.isOngoing()) {
            return "redirect:/rideBookings";
        }

        if (booking.updateShareRequestStatus(requesterUsername, nextStatus, pickupTime)) {
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
            if (booking.isShareable() && !booking.isOwner(currentUsername)) {
                shareableBookings.add(booking);
            }
        }
        return shareableBookings;
    }

    private List<RideBooking> getJoinedBookings(List<RideBooking> allBookings, String currentUsername) {
        List<RideBooking> joinedBookings = new ArrayList<RideBooking>();
        for (RideBooking booking : allBookings) {
            if (booking.hasShareRequestFrom(currentUsername)) {
                joinedBookings.add(booking);
            }
        }
        return joinedBookings;
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

    private List<RideBooking> getBookingsByStatus(List<RideBooking> bookings, String status) {
        List<RideBooking> matches = new ArrayList<RideBooking>();
        for (RideBooking booking : bookings) {
            if (status.equalsIgnoreCase(booking.getStatus())) {
                matches.add(booking);
            }
        }
        return matches;
    }

    private boolean canBookBikes(User user) {
        return user != null && ("RIDE_SHARER".equalsIgnoreCase(user.getRole()) || "RIDER".equalsIgnoreCase(user.getRole()));
    }

    private boolean canJoinSharedRides(User user) {
        return user != null && ("RIDE_BOOKER".equalsIgnoreCase(user.getRole()) || "RIDER".equalsIgnoreCase(user.getRole()));
    }

    private boolean canManageShareRequests(User user) {
        return user != null && ("RIDE_SHARER".equalsIgnoreCase(user.getRole()) || "RIDER".equalsIgnoreCase(user.getRole()));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
