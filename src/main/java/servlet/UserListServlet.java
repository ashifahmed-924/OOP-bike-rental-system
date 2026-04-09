package servlet;

import dao.UserDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/users")
public class UserListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            redirectByRole(response, currentUser);
            return;
        }

        String keyword = request.getParameter("keyword");
        UserDAO userDAO = new UserDAO(getServletContext().getRealPath("/data"));
        List<User> users = userDAO.searchUsers(keyword);

        request.setAttribute("users", users);
        request.setAttribute("keyword", keyword == null ? "" : keyword);
        request.getRequestDispatcher("users.jsp").forward(request, response);
    }

    private void redirectByRole(HttpServletResponse response, User currentUser) throws IOException {
        if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("bikes");
        } else {
            response.sendRedirect("rideBookings");
        }
    }
}
