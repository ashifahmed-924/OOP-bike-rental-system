package servlet;

import dao.RideBookingDAO;
import model.RideBooking;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/respondShareRideRequest")
public class RespondShareRideRequestServlet extends HttpServlet {

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
        String requesterUsername = request.getParameter("requesterUsername");
        String decision = request.getParameter("decision");
        String nextStatus = "DECLINED";

        if ("accept".equalsIgnoreCase(decision)) {
            nextStatus = "ACCEPTED";
        }

        RideBookingDAO bookingDAO = new RideBookingDAO(getServletContext().getRealPath("/data"));
        RideBooking booking = bookingDAO.getBookingById(bookingId);

        if (booking == null || !booking.isOwner(currentUser.getUsername()) || !booking.isOngoing()) {
            response.sendRedirect("rideBookings");
            return;
        }

        if (booking.updateShareRequestStatus(requesterUsername, nextStatus)) {
            if ("ACCEPTED".equalsIgnoreCase(nextStatus)) {
                booking.setStatus("CONFIRMED");
            }
            bookingDAO.updateBooking(booking);
        }

        response.sendRedirect("rideBookings");
    }
}
