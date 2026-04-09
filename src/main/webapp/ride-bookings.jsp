<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="model.RideBooking" %>
<%@ page import="model.Bike" %>
<%@ page import="model.ShareRideRequest" %>
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
    boolean operator = "OPERATOR".equalsIgnoreCase(currentUser.getRole());
    List<RideBooking> rideBookings = (List<RideBooking>) request.getAttribute("rideBookings");
    List<Bike> bikes = (List<Bike>) request.getAttribute("bikes");
    List<RideBooking> shareableBookings = (List<RideBooking>) request.getAttribute("shareableBookings");
    Set<Integer> bookedBikeIds = (Set<Integer>) request.getAttribute("bookedBikeIds");
%>
<html>
<head>
    <title>Ride Bookings - Bike Rental and Ride-Sharing Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
    <style>
        .bike-card, .booking-table-card {
            border: 0;
            border-radius: 22px;
            overflow: hidden;
            box-shadow: 0 20px 44px rgba(32, 52, 89, 0.10);
            background: rgba(255, 255, 255, 0.92);
        }
        .bike-banner {
            background: linear-gradient(135deg, #059669 0%, #2563eb 100%);
            color: #fff;
            padding: 1rem 1.25rem;
        }
        .bike-meta { font-size: 0.92rem; color: #5c6b7a; }
        .section-card {
            border-radius: 22px;
            overflow: hidden;
        }
        .section-header {
            background: linear-gradient(135deg, #0f172a 0%, #1d4ed8 100%);
            color: #fff;
            padding: 1rem 1.25rem;
        }
        .request-box {
            border: 1px solid #dbeafe;
            border-radius: 14px;
            padding: 1rem;
            background: #f8fbff;
        }
    </style>
</head>
<body>
<div class="container app-shell">
    <div class="app-hero d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
            <h2 class="page-title"><%= operator ? "Ride Booking Overview" : "My Rides" %></h2>
            <p class="mb-0">
                <%= operator ? "See booking activity only for bikes you added." : "Book a bike, review ongoing rides, and manage ride sharing." %>
            </p>
        </div>
        <div class="d-flex gap-2 flex-wrap">
            <% if(operator) { %>
            <a href="bikes" class="btn btn-outline-light">Manage Bikes</a>
            <% } %>
            <a href="logout" class="btn btn-danger">Logout</a>
        </div>
    </div>

    <% if(!operator) { %>
    <div class="mb-4">
        <div class="mb-3">
            <h4 class="mb-1">Available Bikes</h4>
            <p class="app-muted mb-0">Browse bikes and reserve one directly when it is still open for booking.</p>
        </div>

        <div class="row g-4">
            <%
                    if(bikes != null && !bikes.isEmpty()) {
                        for(Bike bike : bikes) {
                            boolean availableForBooking = "AVAILABLE".equalsIgnoreCase(bike.getStatus()) &&
                                    (bookedBikeIds == null || !bookedBikeIds.contains(bike.getId()));
            %>
            <div class="col-lg-6">
                <div class="card bike-card">
                    <div class="bike-banner">
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <h5 class="mb-1"><%= bike.getBikeName() %></h5>
                                <div class="small opacity-75">Bike ID: <%= bike.getId() %></div>
                            </div>
                            <span class="badge bg-light text-dark"><%= bike.getStatus() %></span>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <div class="bike-meta mb-3">
                            <div><strong>Type:</strong> <%= bike.getBikeType() %></div>
                            <div><strong>Station:</strong> <%= bike.getStation() %></div>
                            <div><strong>Rate:</strong> $<%= bike.getHourlyRate() %>/hour</div>
                            <div><strong>Operator:</strong> <%= bike.getOperator() %></div>
                        </div>

                        <% if(availableForBooking) { %>
                        <form action="addRideBooking" method="post" class="row g-2">
                            <input type="hidden" name="bikeId" value="<%= bike.getId() %>">
                            <div class="col-md-4">
                                <label class="form-label mb-1">Hours</label>
                                <input type="number" name="durationHours" class="form-control" min="1" value="1" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label mb-1">Pickup</label>
                                <input type="text" name="pickupPoint" class="form-control" placeholder="Pickup point" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label mb-1">Drop-off</label>
                                <input type="text" name="dropPoint" class="form-control" placeholder="Drop-off point" required>
                            </div>
                            <div class="col-12">
                                <button type="submit" class="btn btn-success w-100">Book This Ride</button>
                            </div>
                        </form>
                        <% } else { %>
                        <div class="alert alert-warning mb-0">
                            This bike is already booked. You cannot book it directly right now.
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            <%
                        }
                    } else {
            %>
            <div class="col-12">
                <div class="app-empty">No bikes are available yet.</div>
            </div>
            <% } %>
        </div>
    </div>
    <% } %>

    <% if(!operator) { %>
    <div class="card section-card app-card mb-4">
        <div class="section-header">
            <h5 class="mb-1">Ongoing Rides You Can Join</h5>
            <p class="mb-0 opacity-75">See active bookings from other riders and request to share the ride with your own pickup and stop.</p>
        </div>
        <div class="card-body p-4">
            <div class="row g-4">
                <%
                    if(shareableBookings != null && !shareableBookings.isEmpty()) {
                        for(RideBooking booking : shareableBookings) {
                            ShareRideRequest myShareRequest = booking.getShareRequestByRequester(currentUser.getUsername());
                            boolean alreadyRequested = myShareRequest != null;
                            String effectiveRideStatus = booking.getStatus();
                            if ("REQUESTED".equalsIgnoreCase(effectiveRideStatus) && !booking.getAcceptedShareRequests().isEmpty()) {
                                effectiveRideStatus = "CONFIRMED";
                            }
                            String rideBadgeText = alreadyRequested ? "SHARE " + myShareRequest.getStatus() : booking.getStatus();
                            String rideBadgeClass = "bg-light text-dark";
                            if (alreadyRequested) {
                                if ("ACCEPTED".equalsIgnoreCase(myShareRequest.getStatus())) {
                                    rideBadgeClass = "bg-success";
                                } else if ("DECLINED".equalsIgnoreCase(myShareRequest.getStatus())) {
                                    rideBadgeClass = "bg-danger";
                                } else {
                                    rideBadgeClass = "bg-info text-dark";
                                }
                            }
                %>
                <div class="col-lg-6">
                    <div class="card bike-card h-100">
                        <div class="bike-banner">
                            <div class="d-flex justify-content-between align-items-start">
                                <div>
                                    <h5 class="mb-1"><%= booking.getBikeName() %></h5>
                                    <div class="small opacity-75">Ride #<%= booking.getId() %> by <%= booking.getUsername() %></div>
                                </div>
                                <span class="badge <%= rideBadgeClass %>"><%= rideBadgeText %></span>
                            </div>
                        </div>
                        <div class="card-body p-4">
                            <div class="bike-meta mb-3">
                                <div><strong>Main ride status:</strong> <%= effectiveRideStatus %></div>
                                <div><strong>Hours:</strong> <%= booking.getDurationHours() %></div>
                                <div><strong>Main pickup:</strong> <%= booking.getPickupPoint() %></div>
                                <div><strong>Main drop-off:</strong> <%= booking.getDropPoint() %></div>
                                <div><strong>Accepted riders:</strong> <%= booking.getAcceptedShareRequests().size() %></div>
                            </div>

                            <% if(!booking.getAcceptedShareRequests().isEmpty()) { %>
                            <div class="mb-3">
                                <label class="form-label fw-semibold mb-2">Joined Riders</label>
                                <div class="d-flex flex-column gap-2">
                                    <% for(ShareRideRequest acceptedRequest : booking.getAcceptedShareRequests()) { %>
                                    <div class="request-box py-2">
                                        <strong><%= acceptedRequest.getRequesterUsername() %></strong>
                                        <div class="small text-muted">Pickup: <%= acceptedRequest.getPickupPoint() %> | Stop: <%= acceptedRequest.getStopPoint() %></div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                            <% } %>

                            <% if(alreadyRequested) { %>
                            <div class="alert <%= "ACCEPTED".equalsIgnoreCase(myShareRequest.getStatus()) ? "alert-success" : ("DECLINED".equalsIgnoreCase(myShareRequest.getStatus()) ? "alert-danger" : "alert-info") %> mb-0">
                                Share request status: <strong><%= myShareRequest.getStatus() %></strong><br>
                                Your pickup: <%= myShareRequest.getPickupPoint() %> | Your stop: <%= myShareRequest.getStopPoint() %>
                            </div>
                            <% } else { %>
                            <form action="requestShareRide" method="post" class="row g-2">
                                <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                <div class="col-md-6">
                                    <label class="form-label mb-1">Your pickup</label>
                                    <input type="text" name="sharePickupPoint" class="form-control" placeholder="Your pickup point" required>
                                </div>
                                <div class="col-md-6">
                                    <label class="form-label mb-1">Your stop</label>
                                    <input type="text" name="shareStopPoint" class="form-control" placeholder="Your stop point" required>
                                </div>
                                <div class="col-12">
                                    <button type="submit" class="btn btn-primary w-100">Share Ride</button>
                                </div>
                            </form>
                            <% } %>
                        </div>
                    </div>
                </div>
                <%
                        }
                    } else {
                %>
                <div class="col-12">
                    <div class="app-empty">No ongoing rides from other users are available to join right now.</div>
                </div>
                <% } %>
            </div>
        </div>
    </div>
    <% } %>

    <div class="card booking-table-card app-card">
        <div class="card-header app-gradient-primary">
            <h5 class="mb-0"><%= operator ? "Bookings For Your Bikes" : "Your Ride Bookings" %></h5>
        </div>
        <div class="card-body p-4">
            <div class="table-responsive">
                <table class="table table-striped align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Bike ID</th>
                        <th>Bike</th>
                        <th>User</th>
                        <th>Hours</th>
                        <th>Pickup</th>
                        <th>Drop-off</th>
                        <th>Status</th>
                        <% if(!operator) { %>
                        <th>Ride Share</th>
                        <th>Actions</th>
                        <% } %>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                    if(rideBookings != null && !rideBookings.isEmpty()) {
                            for(RideBooking booking : rideBookings) {
                                String effectiveBookingStatus = booking.getStatus();
                                if ("REQUESTED".equalsIgnoreCase(effectiveBookingStatus) && !booking.getAcceptedShareRequests().isEmpty()) {
                                    effectiveBookingStatus = "CONFIRMED";
                                }
                    %>
                    <tr>
                        <td><%= booking.getId() %></td>
                        <td><%= booking.getBikeId() %></td>
                        <td><%= booking.getBikeName() %></td>
                        <td><%= booking.getUsername() %></td>
                        <td><%= booking.getDurationHours() %></td>
                        <td><%= booking.getPickupPoint() %></td>
                        <td><%= booking.getDropPoint() %></td>
                        <td><span class="app-chip"><%= effectiveBookingStatus %></span></td>
                        <% if(!operator) { %>
                        <td style="min-width: 280px;">
                            <div class="small text-muted mb-2">
                                Pending: <%= booking.getPendingShareRequests().size() %> | Joined: <%= booking.getAcceptedShareRequests().size() %>
                            </div>
                            <%
                                if(!booking.getPendingShareRequests().isEmpty()) {
                                    for(ShareRideRequest shareRequest : booking.getPendingShareRequests()) {
                            %>
                            <div class="request-box mb-2">
                                <div class="fw-semibold"><%= shareRequest.getRequesterUsername() %></div>
                                <div class="small text-muted mb-2">Pickup: <%= shareRequest.getPickupPoint() %> | Stop: <%= shareRequest.getStopPoint() %></div>
                                <form action="respondShareRideRequest" method="post" class="d-flex gap-2">
                                    <input type="hidden" name="bookingId" value="<%= booking.getId() %>">
                                    <input type="hidden" name="requesterUsername" value="<%= shareRequest.getRequesterUsername() %>">
                                    <button type="submit" name="decision" value="accept" class="btn btn-success btn-sm">Accept</button>
                                    <button type="submit" name="decision" value="decline" class="btn btn-outline-danger btn-sm">Decline</button>
                                </form>
                            </div>
                            <%
                                    }
                                }
                                if(!booking.getAcceptedShareRequests().isEmpty()) {
                                    for(ShareRideRequest shareRequest : booking.getAcceptedShareRequests()) {
                            %>
                            <div class="request-box mb-2">
                                <div class="fw-semibold"><%= shareRequest.getRequesterUsername() %></div>
                                <div class="small text-muted">Joined from <%= shareRequest.getPickupPoint() %> to <%= shareRequest.getStopPoint() %></div>
                            </div>
                            <%
                                    }
                                }
                                if(booking.getPendingShareRequests().isEmpty() && booking.getAcceptedShareRequests().isEmpty()) {
                            %>
                            <span class="app-muted small">No share requests yet.</span>
                            <% } %>
                        </td>
                        <% } %>
                        <% if(!operator) { %>
                        <td>
                            <a href="edit-ride-booking.jsp?id=<%= booking.getId() %>" class="btn btn-warning btn-sm">Edit</a>
                            <a href="deleteRideBooking?id=<%= booking.getId() %>" class="btn btn-danger btn-sm" onclick="return confirm('Delete this ride booking?')">Delete</a>
                        </td>
                        <% } %>
                    </tr>
                    <%
                            }
                        } else {
                    %>
                    <tr>
                        <td colspan="<%= operator ? 8 : 10 %>" class="text-center"><div class="app-empty">No ride bookings found.</div></td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
