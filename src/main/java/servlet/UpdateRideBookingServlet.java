package servlet;

import dao.RideBookingDAO;
import model.RideBooking;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/updateRideBooking")
public class UpdateRideBookingServlet extends HttpServlet {

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

        int id = Integer.parseInt(request.getParameter("id"));
        int bikeId = Integer.parseInt(request.getParameter("bikeId"));
        String bikeName = request.getParameter("bikeName");
        String username = request.getParameter("username");
        int durationHours = Integer.parseInt(request.getParameter("durationHours"));
        String pickupPoint = request.getParameter("pickupPoint");
        String dropPoint = request.getParameter("dropPoint");
        String status = request.getParameter("status");

        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) &&
                !currentUser.getUsername().equals(username)) {
            response.sendRedirect("rideBookings");
            return;
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));
        RideBooking existingBooking = bookingDAO.getBookingById(id);
        RideBooking updatedBooking = new RideBooking(id, bikeId, bikeName, username, durationHours, pickupPoint, dropPoint, status);
        if (existingBooking != null) {
            updatedBooking.setShareRequests(existingBooking.getShareRequests());
        }
        bookingDAO.updateBooking(updatedBooking);
        BikeStatusHelper.syncBikeStatus(getServletContext().getRealPath("/data"), bikeId);

        response.sendRedirect("rideBookings");
    }
}
