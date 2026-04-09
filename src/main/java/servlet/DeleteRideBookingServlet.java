package servlet;

import dao.RideBookingDAO;
import model.RideBooking;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/deleteRideBooking")
public class DeleteRideBookingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        User currentUser = (User) session.getAttribute("currentUser");
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("users");
            return;
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));
        RideBooking booking = bookingDAO.getBookingById(id);

        if (booking == null) {
            response.sendRedirect("rideBookings");
            return;
        }

        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) &&
                !currentUser.getUsername().equals(booking.getUsername())) {
            response.sendRedirect("rideBookings");
            return;
        }

        int bikeId = booking.getBikeId();
        bookingDAO.deleteBooking(id);
        BikeStatusHelper.syncBikeStatus(getServletContext().getRealPath("/data"), bikeId);
        response.sendRedirect("rideBookings");
    }
}
