package servlet;

import dao.UserDAO;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/updateUser")
public class UpdateUserServlet extends HttpServlet {

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

        String originalUsername = request.getParameter("originalUsername");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        UserDAO userDAO = new UserDAO(getServletContext().getRealPath("/data"));
        boolean updated = userDAO.updateUser(originalUsername, new User(username, password, role));

        if (!updated) {
            response.sendRedirect("edit-user.jsp?username=" + originalUsername);
            return;
        }

        if (currentUser.getUsername().equals(originalUsername)) {
            session.setAttribute("currentUser", new User(username, password, role));
        }

        response.sendRedirect("users");
    }
}
