package servlet;

import dao.BikeDAO;
import dao.RideBookingDAO;
import model.Bike;
import model.RideBooking;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/rideBookings")
public class RideBookingListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("users");
            return;
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));
        BikeDAO bikeDAO = new BikeDAO(getServletContext().getRealPath("/data"));

        List<Bike> bikes = bikeDAO.getAllBikes();
        request.setAttribute("bikes", bikes);

        List<RideBooking> allBookings = bookingDAO.getAllBookings();
        request.setAttribute("bookedBikeIds", getBookedBikeIds(allBookings));

        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            request.setAttribute("rideBookings", getBookingsForOperatorBikes(allBookings, bikes, currentUser.getUsername()));
        } else {
            request.setAttribute("rideBookings", bookingDAO.getBookingsByUsername(currentUser.getUsername()));
            request.setAttribute("shareableBookings", getShareableBookings(allBookings, currentUser.getUsername()));
        }

        request.getRequestDispatcher("ride-bookings.jsp").forward(request, response);
    }

    private List<RideBooking> getShareableBookings(List<RideBooking> allBookings, String currentUsername) {
        List<RideBooking> shareableBookings = new ArrayList<>();
        for (RideBooking booking : allBookings) {
            if (booking.isOngoing() && !booking.isOwner(currentUsername)) {
                shareableBookings.add(booking);
            }
        }
        return shareableBookings;
    }

    private Set<Integer> getBookedBikeIds(List<RideBooking> allBookings) {
        Set<Integer> bookedBikeIds = new HashSet<>();
        for (RideBooking booking : allBookings) {
            if (booking.isOngoing()) {
                bookedBikeIds.add(booking.getBikeId());
            }
        }
        return bookedBikeIds;
    }

    private List<RideBooking> getBookingsForOperatorBikes(List<RideBooking> allBookings, List<Bike> bikes, String operatorUsername) {
        Set<Integer> operatorBikeIds = new HashSet<>();
        for (Bike bike : bikes) {
            if (bike.getOperator() != null && bike.getOperator().equalsIgnoreCase(operatorUsername)) {
                operatorBikeIds.add(bike.getId());
            }
        }

        List<RideBooking> operatorBookings = new ArrayList<>();
        for (RideBooking booking : allBookings) {
            if (operatorBikeIds.contains(booking.getBikeId())) {
                operatorBookings.add(booking);
            }
        }
        return operatorBookings;
    }
}
