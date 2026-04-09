<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Bike" %>
<%@ page import="model.User" %>
<%
    if(session.getAttribute("currentUser") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    User currentUser = (User) session.getAttribute("currentUser");
    if("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("users");
        return;
    }
    if(!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("rideBookings");
        return;
    }
%>
<html>
<head>
    <title>Bikes - Bike Rental and Ride-Sharing Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
    <style>
        .section-card {
            overflow: hidden;
        }
        .bike-tile {
            border: 0;
            border-radius: 20px;
            box-shadow: 0 18px 36px rgba(15, 23, 42, 0.10);
            height: 100%;
            background: rgba(255, 255, 255, 0.92);
        }
        .bike-tile.mine .card-header {
            background: linear-gradient(135deg, #0f766e 0%, #2563eb 100%);
        }
        .bike-tile.shared .card-header {
            background: linear-gradient(135deg, #475569 0%, #0f172a 100%);
        }
        .bike-tile .card-header {
            color: #fff;
        }
        .meta-line {
            color: #4b5c6d;
            font-size: 0.95rem;
        }
    </style>
</head>
<body>
<div class="container app-shell">
    <div class="app-hero d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
            <h2 class="page-title">Welcome, <%= currentUser.getUsername() %>!</h2>
            <p class="mb-0">Manage your fleet inventory, pricing, and bike availability from one place.</p>
        </div>
        <div class="d-flex gap-2 flex-wrap">
            <a href="rideBookings" class="btn btn-outline-light">Ride Bookings</a>
            <a href="logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <%
        List<Bike> bikes = (List<Bike>) request.getAttribute("bikes");
        boolean hasBikes = bikes != null && !bikes.isEmpty();
    %>

    <div class="section-card app-card mb-4">
        <div class="card-header app-gradient-primary d-flex justify-content-between align-items-center p-3 flex-wrap gap-2">
            <div>
                <h5 class="mb-0">My Bikes</h5>
                <small>Bikes you added and can fully manage.</small>
            </div>
            <a href="add-bike.jsp" class="btn btn-light btn-sm">+ Add New Bike</a>
        </div>
        <div class="card-body p-4">
            <div class="row g-4">
                <%
                    boolean hasMyBikes = false;
                    if(hasBikes) {
                        for(Bike bike : bikes) {
                            if(currentUser.getUsername().equals(bike.getOperator())) {
                                hasMyBikes = true;
                %>
                <div class="col-lg-6">
                    <div class="card bike-tile mine">
                        <div class="card-header d-flex justify-content-between align-items-start">
                            <div>
                                <h5 class="mb-1"><%= bike.getBikeName() %></h5>
                                <small>Bike ID: <%= bike.getId() %></small>
                            </div>
                            <span class="badge bg-light text-dark">Owner</span>
                        </div>
                        <div class="card-body p-4">
                            <div class="meta-line mb-2"><strong>Type:</strong> <%= bike.getBikeType() %></div>
                            <div class="meta-line mb-2"><strong>Station:</strong> <%= bike.getStation() %></div>
                            <div class="meta-line mb-2"><strong>Rate:</strong> $<%= bike.getHourlyRate() %>/hour</div>
                            <div class="meta-line mb-3"><strong>Status:</strong> <%= bike.getStatus() %></div>
                            <div class="d-flex gap-2 flex-wrap">
                                <a href="edit-bike.jsp?id=<%= bike.getId() %>" class="btn btn-warning btn-sm">Edit</a>
                                <a href="deleteBike?id=<%= bike.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Delete this bike?')">Delete</a>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                            }
                        }
                    }
                    if(!hasMyBikes) {
                %>
                <div class="col-12">
                    <div class="app-empty mb-0">You have not added any bikes yet.</div>
                </div>
                <% } %>
            </div>
        </div>
    </div>

    <div class="section-card app-card">
        <div class="card-header app-gradient-primary p-3">
            <h5 class="mb-0">All Bikes</h5>
            <small>Inventory across the platform. Only your bikes can be edited or deleted.</small>
        </div>
        <div class="card-body p-4">
            <div class="row g-4">
                <%
                    if(hasBikes) {
                        for(Bike bike : bikes) {
                            boolean isOwner = currentUser.getUsername().equals(bike.getOperator());
                %>
                <div class="col-lg-6">
                    <div class="card bike-tile <%= isOwner ? "mine" : "shared" %>">
                        <div class="card-header d-flex justify-content-between align-items-start">
                            <div>
                                <h5 class="mb-1"><%= bike.getBikeName() %></h5>
                                <small>Bike ID: <%= bike.getId() %></small>
                            </div>
                            <span class="badge <%= isOwner ? "bg-light text-dark" : "bg-secondary" %>">
                                <%= isOwner ? "My Bike" : "View Only" %>
                            </span>
                        </div>
                        <div class="card-body p-4">
                            <div class="meta-line mb-2"><strong>Type:</strong> <%= bike.getBikeType() %></div>
                            <div class="meta-line mb-2"><strong>Station:</strong> <%= bike.getStation() %></div>
                            <div class="meta-line mb-2"><strong>Rate:</strong> $<%= bike.getHourlyRate() %>/hour</div>
                            <div class="meta-line mb-3"><strong>Operator:</strong> <%= bike.getOperator() %></div>
                            <div class="meta-line mb-4"><strong>Status:</strong> <%= bike.getStatus() %></div>
                            <div class="d-flex gap-2 flex-wrap">
                                <% if(isOwner) { %>
                                <a href="edit-bike.jsp?id=<%= bike.getId() %>" class="btn btn-warning btn-sm">Edit</a>
                                <a href="deleteBike?id=<%= bike.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Delete this bike?')">Delete</a>
                                <% } else { %>
                                <span class="app-muted small align-self-center">Managed by another operator</span>
                                <% } %>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                        }
                    } else {
                %>
                <div class="col-12">
                    <div class="app-empty mb-0">No bikes found. Add some!</div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
</div>
</body>
</html>
