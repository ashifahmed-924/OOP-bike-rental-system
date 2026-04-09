<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="dao.BikeDAO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Bike" %>
<%@ page import="model.User" %>
<%
    User currentUser = (User) session.getAttribute("currentUser");
    BikeDAO bikeDAO = new BikeDAO(application.getRealPath("/data"));
    List<Bike> allBikes = bikeDAO.getAllBikes();
    List<Bike> availableBikes = new ArrayList<>();

    for (Bike bike : allBikes) {
        if ("AVAILABLE".equalsIgnoreCase(bike.getStatus())) {
            availableBikes.add(bike);
        }
    }

    String dashboardLink = "login.jsp";
    if (currentUser != null) {
        if ("ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            dashboardLink = "users";
        } else if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
            dashboardLink = "bikes";
        } else {
            dashboardLink = "rideBookings";
        }
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Bike Rental and Ride-Sharing Platform</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="assets/app-theme.css" rel="stylesheet">
    <style>
        :root {
            --brand-dark: #0f172a;
            --brand-blue: #0f4c81;
            --brand-gold: #f59e0b;
            --brand-mist: #e0f2fe;
            --brand-ice: #f8fbff;
            --text-soft: #475569;
        }

        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            color: var(--brand-dark);
            background:
                radial-gradient(circle at top left, rgba(245, 158, 11, 0.16), transparent 28%),
                radial-gradient(circle at top right, rgba(14, 116, 144, 0.18), transparent 30%),
                linear-gradient(180deg, #fdfdfd 0%, var(--brand-ice) 100%);
        }

        .navbar-shell {
            background: rgba(255, 255, 255, 0.92);
            backdrop-filter: blur(14px);
            box-shadow: 0 12px 30px rgba(15, 23, 42, 0.07);
        }

        .brand-mark {
            width: 42px;
            height: 42px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border-radius: 14px;
            background: linear-gradient(135deg, var(--brand-gold), #fb7185);
            color: #fff;
            font-weight: 700;
        }

        .hero {
            padding: 5rem 0 3rem;
        }

        .hero-panel {
            background: linear-gradient(135deg, rgba(15, 76, 129, 0.96), rgba(15, 23, 42, 0.96));
            color: #fff;
            border-radius: 32px;
            padding: 3rem;
            box-shadow: 0 26px 60px rgba(15, 23, 42, 0.22);
            position: relative;
            overflow: hidden;
        }

        .hero-panel::after {
            content: "";
            position: absolute;
            inset: auto -10% -35% auto;
            width: 260px;
            height: 260px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.08);
        }

        .hero-kicker {
            display: inline-block;
            background: rgba(255, 255, 255, 0.14);
            border: 1px solid rgba(255, 255, 255, 0.16);
            border-radius: 999px;
            padding: 0.45rem 0.9rem;
            font-size: 0.85rem;
            letter-spacing: 0.04em;
            text-transform: uppercase;
        }

        .hero h1 {
            font-size: clamp(2.4rem, 5vw, 4.6rem);
            line-height: 1;
            margin: 1rem 0;
            max-width: 10ch;
        }

        .hero-copy {
            color: rgba(255, 255, 255, 0.82);
            max-width: 56ch;
            font-size: 1.05rem;
        }

        .stat-strip {
            margin-top: 2rem;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 1rem;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.12);
            border: 1px solid rgba(255, 255, 255, 0.14);
            border-radius: 20px;
            padding: 1rem 1.1rem;
        }

        .stat-card strong {
            display: block;
            font-size: 1.7rem;
        }

        .glass-card {
            background: rgba(255, 255, 255, 0.94);
            border: 1px solid rgba(148, 163, 184, 0.18);
            border-radius: 28px;
            box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
        }

        .section-heading {
            margin-bottom: 2rem;
        }

        .bike-card {
            height: 100%;
            border: 0;
            border-radius: 24px;
            box-shadow: 0 18px 35px rgba(15, 23, 42, 0.07);
            overflow: hidden;
            background: #fff;
        }

        .bike-card .card-top {
            background: linear-gradient(135deg, #fff7ed 0%, #dbeafe 100%);
            padding: 1.25rem 1.25rem 0.75rem;
        }

        .bike-card .status-pill {
            background: #dcfce7;
            color: #166534;
            border-radius: 999px;
            padding: 0.3rem 0.75rem;
            font-size: 0.8rem;
            font-weight: 600;
        }

        .bike-detail {
            color: var(--text-soft);
            margin-bottom: 0.45rem;
        }

        .cta-band {
            background: linear-gradient(135deg, #082f49 0%, #0f766e 100%);
            color: #fff;
            border-radius: 28px;
            padding: 2rem;
        }

        @media (max-width: 767px) {
            .hero-panel {
                padding: 2rem;
            }
        }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg sticky-top navbar-shell">
    <div class="container py-2">
        <a class="navbar-brand d-flex align-items-center gap-2 fw-semibold" href="index.jsp">
            <span class="brand-mark">BR</span>
            <span>BikeRentalSystem</span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
                <li class="nav-item"><a class="nav-link" href="#bike-fleet">Bike Fleet</a></li>
                <li class="nav-item"><a class="nav-link" href="<%= dashboardLink %>">Book Now</a></li>
                <% if (currentUser == null) { %>
                <li class="nav-item"><a class="nav-link" href="login.jsp">Login</a></li>
                <li class="nav-item"><a class="btn btn-warning ms-lg-2" href="register.jsp">Create Account</a></li>
                <% } else { %>
                <li class="nav-item"><a class="nav-link" href="<%= dashboardLink %>"><%= currentUser.getUsername() %></a></li>
                <li class="nav-item"><a class="btn btn-danger ms-lg-2" href="logout">Logout</a></li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<section class="hero">
    <div class="container">
        <div class="hero-panel">
            <span class="hero-kicker">City rides, ready to go</span>
            <div class="row align-items-center g-4">
                <div class="col-lg-7">
                    <h1>Find an available bike and book your next ride.</h1>
                    <p class="hero-copy">
                        Browse the live fleet, compare stations and hourly pricing, then sign in to reserve a bike in a few clicks.
                    </p>
                    <div class="d-flex flex-wrap gap-3 mt-4">
                        <a href="#bike-fleet" class="btn btn-warning btn-lg">Explore Bikes</a>
                        <% if (currentUser == null) { %>
                        <a href="login.jsp" class="btn btn-outline-light btn-lg">Login to Book</a>
                        <% } else { %>
                        <a href="<%= dashboardLink %>" class="btn btn-outline-light btn-lg">Open Dashboard</a>
                        <% } %>
                    </div>
                </div>
                <div class="col-lg-5">
                    <div class="glass-card p-4 text-dark">
                        <h5 class="mb-3">Booking flow</h5>
                        <div class="bike-detail"><strong>1.</strong> Choose a bike from the fleet below.</div>
                        <div class="bike-detail"><strong>2.</strong> Log in or create a rider account.</div>
                        <div class="bike-detail"><strong>3.</strong> Confirm pickup, drop-off, and duration.</div>
                        <div class="bike-detail mb-0"><strong>4.</strong> Track your booking from the ride dashboard.</div>
                    </div>
                </div>
            </div>
            <div class="stat-strip">
                <div class="stat-card">
                    <strong><%= availableBikes.size() %></strong>
                    <span>Available bikes</span>
                </div>
                <div class="stat-card">
                    <strong><%= allBikes.size() %></strong>
                    <span>Total fleet</span>
                </div>
                <div class="stat-card">
                    <strong><%= currentUser == null ? "Guest" : currentUser.getRole() %></strong>
                    <span>Current access</span>
                </div>
            </div>
        </div>
    </div>
</section>

<section id="bike-fleet" class="pb-5">
    <div class="container">
        <div class="section-heading d-flex flex-column flex-lg-row justify-content-between align-items-lg-end gap-3">
            <div>
                <h2 class="mb-1">Bike Fleet</h2>
                <p class="text-secondary mb-0">Every bike is listed here. Only bikes marked available can be booked immediately.</p>
            </div>
            <% if (currentUser == null) { %>
            <a href="register.jsp" class="btn btn-outline-primary">Register as Rider</a>
            <% } else { %>
            <a href="<%= dashboardLink %>" class="btn btn-outline-primary">Continue to Booking</a>
            <% } %>
        </div>

        <div class="row g-4">
            <% if (!allBikes.isEmpty()) {
                for (Bike bike : allBikes) {
                    boolean available = "AVAILABLE".equalsIgnoreCase(bike.getStatus());
            %>
            <div class="col-md-6 col-xl-4">
                <div class="card bike-card">
                    <div class="card-top">
                        <div class="d-flex justify-content-between align-items-start gap-3">
                            <div>
                                <div class="text-uppercase small text-secondary">Bike #<%= bike.getId() %></div>
                                <h4 class="mb-1"><%= bike.getBikeName() %></h4>
                                <div class="text-secondary"><%= bike.getBikeType() %></div>
                            </div>
                            <span class="status-pill" style="<%= available ? "" : "background:#fee2e2;color:#991b1b;" %>"><%= bike.getStatus() %></span>
                        </div>
                    </div>
                    <div class="card-body p-4">
                        <div class="bike-detail"><strong>Station:</strong> <%= bike.getStation() %></div>
                        <div class="bike-detail"><strong>Operator:</strong> <%= bike.getOperator() %></div>
                        <div class="bike-detail"><strong>Rate:</strong> $<%= bike.getHourlyRate() %>/hour</div>
                        <% if (currentUser == null) { %>
                        <div class="d-grid mt-4">
                            <a href="login.jsp" class="btn btn-primary"><%= available ? "Login to Book" : "Login to View Booking Options" %></a>
                        </div>
                        <% } else if ("RIDER".equalsIgnoreCase(currentUser.getRole()) && available) { %>
                        <form action="addRideBooking" method="post" class="row g-2 mt-3">
                            <input type="hidden" name="bikeId" value="<%= bike.getId() %>">
                            <div class="col-12">
                                <label class="form-label mb-1">Duration (hours)</label>
                                <input type="number" name="durationHours" class="form-control" min="1" value="1" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label mb-1">Pickup Point</label>
                                <input type="text" name="pickupPoint" class="form-control" placeholder="Enter pickup point" required>
                            </div>
                            <div class="col-12">
                                <label class="form-label mb-1">Drop-off Point</label>
                                <input type="text" name="dropPoint" class="form-control" placeholder="Enter drop-off point" required>
                            </div>
                            <div class="col-12 d-grid">
                                <button type="submit" class="btn btn-primary">Book This Bike</button>
                            </div>
                        </form>
                        <% } else if ("RIDER".equalsIgnoreCase(currentUser.getRole())) { %>
                        <div class="alert alert-secondary mt-4 mb-0">
                            This bike is currently <strong><%= bike.getStatus() %></strong> and cannot be booked yet.
                        </div>
                        <% } else if ("OPERATOR".equalsIgnoreCase(currentUser.getRole())) { %>
                        <div class="d-grid mt-4">
                            <a href="bikes" class="btn btn-primary">Manage Fleet</a>
                        </div>
                        <% } else { %>
                        <div class="d-grid mt-4">
                            <a href="users" class="btn btn-primary">Open Admin Panel</a>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
            <%  }
            } else { %>
            <div class="col-12">
                <div class="glass-card p-5 text-center">
                    <h4 class="mb-2">No bikes found yet</h4>
                    <p class="text-secondary mb-0">Operators can add bikes from the dashboard, and they will appear here once saved.</p>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</section>

<section class="pb-5">
    <div class="container">
        <div class="cta-band d-flex flex-column flex-lg-row justify-content-between align-items-lg-center gap-3">
            <div>
                <h3 class="mb-1">Ready to reserve a ride?</h3>
                <p class="mb-0 text-white-50">Riders can log in and book instantly. Operators can manage the bike fleet from their own dashboard.</p>
            </div>
            <div class="d-flex flex-wrap gap-2">
                <% if (currentUser == null) { %>
                <a href="login.jsp" class="btn btn-warning">Login</a>
                <a href="register.jsp" class="btn btn-outline-light">Register</a>
                <% } else { %>
                <a href="<%= dashboardLink %>" class="btn btn-warning">Go to Dashboard</a>
                <% } %>
            </div>
        </div>
    </div>
</section>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
