package servlet;

import dao.BikeDAO;
import model.Bike;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/deleteBike")
public class DeleteBikeServlet extends HttpServlet {

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
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("rideBookings");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        BikeDAO bikeDAO = new BikeDAO(getServletContext().getRealPath("/data"));
        Bike existingBike = bikeDAO.getBikeById(id);

        if (existingBike == null || !currentUser.getUsername().equals(existingBike.getOperator())) {
            response.sendRedirect("bikes");
            return;
        }

        bikeDAO.deleteBike(id);

        response.sendRedirect("bikes");
    }
}
