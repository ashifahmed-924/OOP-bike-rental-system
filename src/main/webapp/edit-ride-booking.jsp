<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.RideBookingDAO" %>
<%@ page import="model.RideBooking" %>
<%@ page import="model.User" %>
<%
    if(session.getAttribute("currentUser") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    User currentUser = (User) session.getAttribute("currentUser");
    String idParam = request.getParameter("id");
    RideBooking booking = null;

    if(idParam != null) {
        try {
            int id = Integer.parseInt(idParam);
            RideBookingDAO dao = new RideBookingDAO(application.getRealPath("/data"));
            booking = dao.getBookingById(id);
        } catch(Exception ex) {
            booking = null;
        }
    }

    if(booking == null) {
        response.sendRedirect("rideBookings");
        return;
    }

    if(!"OPERATOR".equalsIgnoreCase(currentUser.getRole()) &&
            !currentUser.getUsername().equals(booking.getUsername())) {
        response.sendRedirect("rideBookings");
        return;
    }
%>
<html>
<head>
    <title>Edit Ride Booking</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
    <div class="app-hero mb-4">
        <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
            <div>
                <h2 class="page-title">Edit Ride Booking</h2>
                <p class="mb-0">Adjust the ride details for booking <strong>#<%= booking.getId() %></strong>.</p>
            </div>
            <a href="rideBookings" class="btn btn-outline-light">Back to Rides</a>
        </div>
    </div>
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card app-card border-0">
                <div class="card-header app-gradient-warm">
                    <h4 class="mb-0">Edit Ride Booking</h4>
                </div>
                <div class="card-body p-4">
                    <form action="updateRideBooking" method="post">
                        <input type="hidden" name="id" value="<%= booking.getId() %>">
                        <input type="hidden" name="bikeId" value="<%= booking.getBikeId() %>">
                        <input type="hidden" name="bikeName" value="<%= booking.getBikeName() %>">
                        <input type="hidden" name="username" value="<%= booking.getUsername() %>">

                        <div class="mb-3">
                            <label class="form-label">Bike</label>
                            <input type="text" class="form-control" value="<%= booking.getBikeName() %>" disabled>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Booked By</label>
                            <input type="text" class="form-control" value="<%= booking.getUsername() %>" disabled>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Hours</label>
                            <input type="number" name="durationHours" class="form-control" min="1" value="<%= booking.getDurationHours() %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Pickup Point</label>
                            <input type="text" name="pickupPoint" class="form-control" value="<%= booking.getPickupPoint() %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Drop-off Point</label>
                            <input type="text" name="dropPoint" class="form-control" value="<%= booking.getDropPoint() %>" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select" required>
                                <option value="REQUESTED" <%= "REQUESTED".equalsIgnoreCase(booking.getStatus()) ? "selected" : "" %>>Requested</option>
                                <option value="CONFIRMED" <%= "CONFIRMED".equalsIgnoreCase(booking.getStatus()) ? "selected" : "" %>>Confirmed</option>
                                <option value="COMPLETED" <%= "COMPLETED".equalsIgnoreCase(booking.getStatus()) ? "selected" : "" %>>Completed</option>
                                <option value="CANCELLED" <%= "CANCELLED".equalsIgnoreCase(booking.getStatus()) ? "selected" : "" %>>Cancelled</option>
                            </select>
                        </div>

                        <div class="d-flex justify-content-end gap-2">
                            <a href="rideBookings" class="btn btn-secondary">Cancel</a>
                            <button type="submit" class="btn btn-warning">Update Ride Booking</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
