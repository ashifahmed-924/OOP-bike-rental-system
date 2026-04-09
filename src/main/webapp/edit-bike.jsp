<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.BikeDAO" %>
<%@ page import="model.Bike" %>
<%@ page import="model.User" %>
<%
    if(session.getAttribute("currentUser") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    User currentUser = (User) session.getAttribute("currentUser");
    if(!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
        response.sendRedirect("rideBookings");
        return;
    }

    String idParam = request.getParameter("id");
    Bike bike = null;
    if(idParam != null) {
        try {
            int id = Integer.parseInt(idParam);
            BikeDAO dao = new BikeDAO(application.getRealPath("/data"));
            bike = dao.getBikeById(id);
        } catch(Exception ex) {
            bike = null;
        }
    }

    if(bike == null) {
        response.sendRedirect("bikes");
        return;
    }
    if(!currentUser.getUsername().equals(bike.getOperator())) {
        response.sendRedirect("bikes");
        return;
    }
%>
<html>
<head>
    <title>Edit Bike</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
    <div class="app-hero mb-4">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <h2 class="page-title">Edit Bike</h2>
                <p class="mb-0">Update the fleet entry for <strong><%= bike.getBikeName() %></strong>.</p>
            </div>
            <a href="bikes" class="btn btn-outline-light">Back to Bikes</a>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card app-card border-0">
                <div class="card-header app-gradient-warm">
                    <h4 class="mb-0">Edit Bike</h4>
                </div>
                <div class="card-body p-4">
                    <form action="updateBike" method="post">
                        <input type="hidden" name="id" value="<%= bike.getId() %>">

                        <div class="mb-3">
                            <label class="form-label">Bike Name</label>
                            <input type="text" name="bikeName" value="<%= bike.getBikeName() %>" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Bike Type</label>
                            <input type="text" name="bikeType" value="<%= bike.getBikeType() %>" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Station</label>
                            <input type="text" name="station" value="<%= bike.getStation() %>" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hourly Rate</label>
                            <input type="number" step="0.01" min="0" name="hourlyRate" value="<%= bike.getHourlyRate() %>" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select" required>
                                <option value="AVAILABLE" <%= "AVAILABLE".equalsIgnoreCase(bike.getStatus()) ? "selected" : "" %>>Available</option>
                                <option value="BOOKED" <%= "BOOKED".equalsIgnoreCase(bike.getStatus()) ? "selected" : "" %>>Booked</option>
                                <option value="IN_SERVICE" <%= "IN_SERVICE".equalsIgnoreCase(bike.getStatus()) ? "selected" : "" %>>In Service</option>
                                <option value="MAINTENANCE" <%= "MAINTENANCE".equalsIgnoreCase(bike.getStatus()) ? "selected" : "" %>>Maintenance</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Managed By</label>
                            <input type="text" value="<%= bike.getOperator() %>" class="form-control" disabled>
                        </div>

                        <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                            <a href="bikes" class="btn btn-secondary me-2">Cancel</a>
                            <button type="submit" class="btn btn-warning">Update Bike</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
