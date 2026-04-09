package servlet;

import dao.BikeDAO;
import model.Bike;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/updateBike")
public class UpdateBikeServlet extends HttpServlet {

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
        if (!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("rideBookings");
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));
        String bikeName = request.getParameter("bikeName");
        String bikeType = request.getParameter("bikeType");
        String station = request.getParameter("station");
        double hourlyRate = Double.parseDouble(request.getParameter("hourlyRate"));
        String status = request.getParameter("status");

        BikeDAO bikeDAO = new BikeDAO(getServletContext().getRealPath("/data"));
        Bike existingBike = bikeDAO.getBikeById(id);

        if (existingBike == null || !currentUser.getUsername().equals(existingBike.getOperator())) {
            response.sendRedirect("bikes");
            return;
        }

        Bike updatedBike = new Bike(id, bikeName, bikeType, station, hourlyRate, status, existingBike.getOperator());
        bikeDAO.updateBike(updatedBike);

        response.sendRedirect("bikes");
    }
}
