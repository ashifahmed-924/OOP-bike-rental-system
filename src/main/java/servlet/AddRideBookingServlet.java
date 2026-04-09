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

@WebServlet("/addRideBooking")
public class AddRideBookingServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("bikes");
            return;
        }

        int bikeId = Integer.parseInt(request.getParameter("bikeId"));
        int durationHours = Integer.parseInt(request.getParameter("durationHours"));
        String pickupPoint = request.getParameter("pickupPoint");
        String dropPoint = request.getParameter("dropPoint");

        BikeDAO bikeDAO = new BikeDAO(getServletContext().getRealPath("/data"));
        Bike bike = bikeDAO.getBikeById(bikeId);

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));

        if (bike == null || !"AVAILABLE".equalsIgnoreCase(bike.getStatus()) || bookingDAO.hasOngoingBookingForBike(bikeId)) {
            response.sendRedirect("rideBookings");
            return;
        }

        RideBooking booking = new RideBooking(
                0,
                bike.getId(),
                bike.getBikeName(),
                currentUser.getUsername(),
                durationHours,
                pickupPoint,
                dropPoint,
                "REQUESTED"
        );

        bookingDAO.addBooking(booking);
        bike.setStatus("BOOKED");
        bikeDAO.updateBike(bike);

        response.sendRedirect("rideBookings");
    }
}
