package servlet;

import dao.UserDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/addUser")
public class AddUserServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User currentUser = (User) session.getAttribute("currentUser");
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        UserDAO userDAO = new UserDAO(getServletContext().getRealPath("/data"));
        boolean created = userDAO.registerUser(new User(username, password, role));

        if (!created) {
            request.setAttribute("error", "Username already exists.");
            request.setAttribute("users", userDAO.getAllUsers());
            request.setAttribute("keyword", "");
            request.getRequestDispatcher("users.jsp").forward(request, response);
            return;
        }

        response.sendRedirect("users");
    }
}
