package servlet;

import dao.RideBookingDAO;
import model.RideBooking;
import model.ShareRideRequest;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/requestShareRide")
public class RequestShareRideServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole()) || "OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("rideBookings");
            return;
        }

        int bookingId = Integer.parseInt(request.getParameter("bookingId"));
        String pickupPoint = request.getParameter("sharePickupPoint");
        String stopPoint = request.getParameter("shareStopPoint");

        if (pickupPoint != null) {
            pickupPoint = pickupPoint.trim();
        }
        if (stopPoint != null) {
            stopPoint = stopPoint.trim();
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));
        RideBooking booking = bookingDAO.getBookingById(bookingId);

        if (pickupPoint == null || pickupPoint.isEmpty() || stopPoint == null || stopPoint.isEmpty()
                || booking == null || !booking.isOngoing() || booking.isOwner(currentUser.getUsername())
                || booking.hasShareRequestFrom(currentUser.getUsername())) {
            response.sendRedirect("rideBookings");
            return;
        }

        booking.addShareRequest(new ShareRideRequest(
                currentUser.getUsername(),
                pickupPoint,
                stopPoint,
                "PENDING"
        ));

        bookingDAO.updateBooking(booking);
        response.sendRedirect("rideBookings");
    }
}
