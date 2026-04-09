<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  if(session.getAttribute("currentUser") == null) {
    response.sendRedirect("login.jsp");
    return;
  }
  model.User currentUser = (model.User) session.getAttribute("currentUser");
  if(!"OPERATOR".equalsIgnoreCase(currentUser.getRole())) {
    response.sendRedirect("rideBookings");
    return;
  }
%>
<html>
<head>
  <title>Add New Bike</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="assets/app-theme.css" rel="stylesheet">
</head>
<body>
<div class="container app-shell">
  <div class="app-hero mb-4">
    <div class="d-flex justify-content-between align-items-start flex-wrap gap-3">
      <div>
        <h2 class="page-title">Add a New Bike</h2>
        <p class="mb-0">Create a new fleet item with station, pricing, and current availability.</p>
      </div>
      <a href="bikes" class="btn btn-outline-light">Back to Bikes</a>
    </div>
  </div>
  <div class="row justify-content-center">
    <div class="col-md-8">
      <div class="card app-card border-0">
        <div class="card-header app-gradient-primary">
          <h4 class="mb-0">Add New Bike</h4>
        </div>
        <div class="card-body p-4">
          <form action="addBike" method="post">
            <div class="mb-3">
              <label class="form-label">Bike Name</label>
              <input type="text" name="bikeName" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Bike Type</label>
              <input type="text" name="bikeType" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Station</label>
              <input type="text" name="station" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Hourly Rate</label>
              <input type="number" step="0.01" min="0" name="hourlyRate" class="form-control" required>
            </div>
            <div class="mb-3">
              <label class="form-label">Status</label>
              <select name="status" class="form-select" required>
                <option value="AVAILABLE">Available</option>
                <option value="IN_SERVICE">In Service</option>
                <option value="MAINTENANCE">Maintenance</option>
              </select>
            </div>
            <div class="mb-3">
              <label class="form-label">Managed By</label>
              <input type="text" class="form-control" value="<%= currentUser.getUsername() %>" disabled>
            </div>

            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
              <a href="bikes" class="btn btn-secondary me-2">Cancel</a>
              <button type="submit" class="btn btn-success">Create Bike</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
