<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
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

    List<User> users = (List<User>) request.getAttribute("users");
    String keyword = (String) request.getAttribute("keyword");
    if(keyword == null) {
        keyword = "";
    }
%>
<html>
<head>
    <title>User Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
    <div class="app-hero d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
            <h2 class="page-title">Admin User Management</h2>
            <p class="mb-0">Create, search, update, and delete platform accounts.</p>
        </div>
        <div class="d-flex gap-2 flex-wrap">
            <a href="bikes" class="btn btn-light">Bikes</a>
            <a href="rideBookings" class="btn btn-outline-light">Ride Bookings</a>
            <a href="logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <% if(request.getAttribute("error") != null) { %>
    <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
    <% } %>

    <div class="row g-4">
        <div class="col-lg-4">
            <div class="card app-card border-0">
                <div class="card-header app-gradient-success">
                    <h5 class="mb-0">Create User</h5>
                </div>
                <div class="card-body p-4">
                    <form action="addUser" method="post">
                        <div class="mb-3">
                            <label class="form-label">Username</label>
                            <input type="text" name="username" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Password</label>
                            <input type="text" name="password" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Role</label>
                            <select name="role" class="form-select" required>
                                <option value="RIDER">Rider</option>
                                <option value="OPERATOR">Operator</option>
                                <option value="ADMIN">Admin</option>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-success w-100">Create User</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-lg-8">
            <div class="card app-card border-0">
                <div class="card-header app-gradient-primary">
                    <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                        <h5 class="mb-0">Users</h5>
                        <form action="users" method="get" class="d-flex gap-2 flex-wrap">
                            <input type="text" name="keyword" value="<%= keyword %>" class="form-control form-control-sm" placeholder="Search username or role">
                            <button type="submit" class="btn btn-light btn-sm">Search</button>
                        </form>
                    </div>
                </div>
                <div class="card-body p-4">
                    <div class="table-responsive">
                        <table class="table table-striped align-middle">
                            <thead>
                            <tr>
                                <th>Username</th>
                                <th>Password</th>
                                <th>Role</th>
                                <th>Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%
                                if(users != null && !users.isEmpty()) {
                                    for(User user : users) {
                            %>
                            <tr>
                                <td><%= user.getUsername() %></td>
                                <td><%= user.getPassword() %></td>
                                <td><span class="app-chip"><%= user.getRole() %></span></td>
                                <td>
                                    <a href="edit-user.jsp?username=<%= user.getUsername() %>" class="btn btn-warning btn-sm">Edit</a>
                                    <% if(!currentUser.getUsername().equals(user.getUsername())) { %>
                                    <a href="deleteUser?username=<%= user.getUsername() %>"
                                       class="btn btn-danger btn-sm"
                                       onclick="return confirm('Delete this user?')">Delete</a>
                                    <% } %>
                                </td>
                            </tr>
                            <%
                                    }
                                } else {
                            %>
                            <tr>
                                <td colspan="4" class="text-center"><div class="app-empty">No users found.</div></td>
                            </tr>
                            <% } %>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
