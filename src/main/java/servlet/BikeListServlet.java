package servlet;

import dao.BikeDAO;
import model.Bike;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/bikes")
public class BikeListServlet extends HttpServlet {

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

        BikeDAO bikeDAO = new BikeDAO(getServletContext().getRealPath("/data"));
        List<Bike> bikes = bikeDAO.getAllBikes();

        request.setAttribute("bikes", bikes);
        request.getRequestDispatcher("bikes.jsp").forward(request, response);
    }
}
