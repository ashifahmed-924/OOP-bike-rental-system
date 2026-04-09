<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Login - Bike Rental and Ride-Sharing Platform</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
  <div class="app-hero mb-4">
    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
      <div>
        <h2 class="page-title">Welcome Back</h2>
        <p class="mb-0">Sign in to access bookings, ride sharing, bike management, or the admin dashboard.</p>
      </div>
      <a href="index.jsp" class="btn btn-outline-light">Back to Home</a>
    </div>
  </div>

  <div class="app-form-card">
    <div class="card app-card border-0">
      <div class="card-header text-center app-gradient-primary">
        <h4 class="mb-0">Bike Rental and Ride-Sharing Platform</h4>
      </div>
      <div class="card-body p-4 p-lg-5">
        <% if(request.getAttribute("error") != null) { %>
        <div class="alert alert-danger">
          <%= request.getAttribute("error") %>
        </div>
        <% } %>
        <% if(request.getAttribute("success") != null) { %>
        <div class="alert alert-success">
          <%= request.getAttribute("success") %>
        </div>
        <% } %>

        <form action="login" method="post">
          <div class="mb-3">
            <label class="form-label">Username</label>
            <input type="text" name="username" class="form-control" required>
          </div>
          <div class="mb-3">
            <label class="form-label">Password</label>
            <input type="password" name="password" class="form-control" required>
          </div>
          <button type="submit" class="btn btn-primary w-100">Login</button>
        </form>

        <p class="app-muted small mt-3 mb-0">
          Admins go to user management, operators go to fleet management, and riders go to ride bookings.
        </p>

        <div class="text-center mt-4">
          <p class="mb-0">Don't have an account? <a href="register.jsp">Register here</a></p>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
