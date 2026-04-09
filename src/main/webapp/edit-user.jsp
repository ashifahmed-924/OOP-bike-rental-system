<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.UserDAO" %>
<%@ page import="model.User" %>
<%
    if(session.getAttribute("currentUser") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User currentUser = (User) session.getAttribute("currentUser");
    if(!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("login.jsp");
        return;
    }

    String usernameParam = request.getParameter("username");
    User managedUser = null;
    if(usernameParam != null) {
        UserDAO dao = new UserDAO(application.getRealPath("/data"));
        managedUser = dao.findUserByUsername(usernameParam);
    }

    if(managedUser == null) {
        response.sendRedirect("users");
        return;
    }
%>
<html>
<head>
    <title>Edit User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
    <div class="app-hero mb-4">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <h2 class="page-title">Edit User</h2>
                <p class="mb-0">Update account details and platform access for <strong><%= managedUser.getUsername() %></strong>.</p>
            </div>
            <a href="users" class="btn btn-outline-light">Back to Users</a>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-md-7">
            <div class="card app-card border-0">
                <div class="card-header app-gradient-warm">
                    <h4 class="mb-0">Edit User</h4>
                </div>
                <div class="card-body p-4">
                    <form action="updateUser" method="post">
                        <input type="hidden" name="originalUsername" value="<%= managedUser.getUsername() %>">

                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" name="username" class="form-control" value="<%= managedUser.getUsername() %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="text" name="password" class="form-control" value="<%= managedUser.getPassword() %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Role</label>
                            <select name="role" class="form-select" required>
                                <option value="RIDER" <%= "RIDER".equalsIgnoreCase(managedUser.getRole()) ? "selected" : "" %>>Rider</option>
                                <option value="OPERATOR" <%= "OPERATOR".equalsIgnoreCase(managedUser.getRole()) ? "selected" : "" %>>Operator</option>
                                <option value="ADMIN" <%= "ADMIN".equalsIgnoreCase(managedUser.getRole()) ? "selected" : "" %>>Admin</option>
                            </select>
                        </div>
                        <div class="d-flex justify-content-end gap-2">
                            <a href="users" class="btn btn-secondary">Cancel</a>
                            <button type="submit" class="btn btn-warning">Update User</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
