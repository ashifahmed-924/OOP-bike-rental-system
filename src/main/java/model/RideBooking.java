package model;

import java.util.ArrayList;
import java.util.List;

public class RideBooking {
    private int id;
    private int bikeId;
    private String bikeName;
    private String username;
    private int durationHours;
    private String pickupPoint;
    private String dropPoint;
    private String status;
    private List<ShareRideRequest> shareRequests;

    public RideBooking() {
        this.shareRequests = new ArrayList<>();
    }

    public RideBooking(int id, int bikeId, String bikeName, String username, int durationHours, String pickupPoint, String dropPoint, String status) {
        this(id, bikeId, bikeName, username, durationHours, pickupPoint, dropPoint, status, new ArrayList<ShareRideRequest>());
    }

    public RideBooking(int id, int bikeId, String bikeName, String username, int durationHours, String pickupPoint, String dropPoint, String status, List<ShareRideRequest> shareRequests) {
        this.id = id;
        this.bikeId = bikeId;
        this.bikeName = bikeName;
        this.username = username;
        this.durationHours = durationHours;
        this.pickupPoint = pickupPoint;
        this.dropPoint = dropPoint;
        this.status = status;
        this.shareRequests = shareRequests == null ? new ArrayList<ShareRideRequest>() : new ArrayList<>(shareRequests);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBikeId() { return bikeId; }
    public void setBikeId(int bikeId) { this.bikeId = bikeId; }

    public String getBikeName() { return bikeName; }
    public void setBikeName(String bikeName) { this.bikeName = bikeName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getDurationHours() { return durationHours; }
    public void setDurationHours(int durationHours) { this.durationHours = durationHours; }

    public String getPickupPoint() { return pickupPoint; }
    public void setPickupPoint(String pickupPoint) { this.pickupPoint = pickupPoint; }

    public String getDropPoint() { return dropPoint; }
    public void setDropPoint(String dropPoint) { this.dropPoint = dropPoint; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<ShareRideRequest> getShareRequests() { return new ArrayList<>(shareRequests); }
    public void setShareRequests(List<ShareRideRequest> shareRequests) {
        this.shareRequests = shareRequests == null ? new ArrayList<ShareRideRequest>() : new ArrayList<>(shareRequests);
    }

    public List<ShareRideRequest> getPendingShareRequests() {
        return filterShareRequestsByStatus("PENDING");
    }

    public List<ShareRideRequest> getAcceptedShareRequests() {
        return filterShareRequestsByStatus("ACCEPTED");
    }

    public List<ShareRideRequest> getDeclinedShareRequests() {
        return filterShareRequestsByStatus("DECLINED");
    }

    public boolean hasShareRequestFrom(String requesterUsername) {
        for (ShareRideRequest shareRequest : shareRequests) {
            if (shareRequest.getRequesterUsername().equalsIgnoreCase(requesterUsername)) {
                return true;
            }
        }
        return false;
    }

    public ShareRideRequest getShareRequestByRequester(String requesterUsername) {
        for (ShareRideRequest shareRequest : shareRequests) {
            if (shareRequest.getRequesterUsername().equalsIgnoreCase(requesterUsername)) {
                return shareRequest;
            }
        }
        return null;
    }

    public boolean isOwner(String candidateUsername) {
        return username != null && username.equalsIgnoreCase(candidateUsername);
    }

    public boolean isOngoing() {
        return !"COMPLETED".equalsIgnoreCase(status) && !"CANCELLED".equalsIgnoreCase(status);
    }

    public void addShareRequest(ShareRideRequest shareRequest) {
        if (shareRequest != null) {
            shareRequests.add(shareRequest);
        }
    }

    public boolean updateShareRequestStatus(String requesterUsername, String nextStatus) {
        for (ShareRideRequest shareRequest : shareRequests) {
            if (shareRequest.getRequesterUsername().equalsIgnoreCase(requesterUsername)
                    && "PENDING".equalsIgnoreCase(shareRequest.getStatus())) {
                shareRequest.setStatus(nextStatus);
                return true;
            }
        }
        return false;
    }

    private List<ShareRideRequest> filterShareRequestsByStatus(String targetStatus) {
        List<ShareRideRequest> filtered = new ArrayList<>();
        for (ShareRideRequest shareRequest : shareRequests) {
            if (targetStatus.equalsIgnoreCase(shareRequest.getStatus())) {
                filtered.add(shareRequest);
            }
        }
        return filtered;
    }

    @Override
    public String toString() {
        return id + ":" + bikeId + ":" + bikeName + ":" + username + ":" + durationHours + ":" + pickupPoint + ":" + dropPoint + ":" + status + ":" + ShareRideRequest.serializeList(shareRequests);
    }
}
