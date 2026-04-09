package app.controller;

import model.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

abstract class BaseWebController {

    @Autowired
    private ServletContext servletContext;

    protected String dataPath() {
        String realPath = servletContext.getRealPath("/data");
        return realPath == null ? null : realPath;
    }

    protected User currentUser(HttpSession session) {
        return session == null ? null : (User) session.getAttribute("currentUser");
    }

    protected String redirectByRole(User user) {
        if (user == null) {
            return "redirect:/login";
        }
        return "redirect:" + user.getDashboardPath();
    }

    protected String dashboardLink(User user) {
        if (user == null) {
            return "/login";
        }
        return user.getDashboardPath();
    }
}
