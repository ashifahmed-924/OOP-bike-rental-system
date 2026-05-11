package model;

import java.util.ArrayList;
import java.util.List;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class RideBooking {
    private int id;
    private int bikeId;
    private String bikeName;
    private String username;
    private int durationHours;
    private String pickupPoint;
    private String dropPoint;
    private String status;
    private String rentalPickupTime;
    private String rentalReturnTime;
    private String renterPhoneNumber;
    private List<ShareRideRequest> shareRequests;

    public RideBooking() {
        this.shareRequests = new ArrayList<>();
    }

    public RideBooking(int id, int bikeId, String bikeName, String username, int durationHours, String pickupPoint, String dropPoint, String status) {
        this(id, bikeId, bikeName, username, durationHours, pickupPoint, dropPoint, status, new ArrayList<ShareRideRequest>());
    }

    public RideBooking(int id, int bikeId, String bikeName, String username, int durationHours, String pickupPoint, String dropPoint, String status, List<ShareRideRequest> shareRequests) {
        this(id, bikeId, bikeName, username, durationHours, pickupPoint, dropPoint, status, "", "", "", shareRequests);
    }

    public RideBooking(int id, int bikeId, String bikeName, String username, int durationHours, String pickupPoint,
                       String dropPoint, String status, String rentalPickupTime, String rentalReturnTime,
                       String renterPhoneNumber, List<ShareRideRequest> shareRequests) {
        this.id = id;
        this.bikeId = bikeId;
        this.bikeName = bikeName;
        this.username = username;
        this.durationHours = durationHours;
        this.pickupPoint = pickupPoint;
        this.dropPoint = dropPoint;
        this.status = status;
        this.rentalPickupTime = rentalPickupTime;
        this.rentalReturnTime = rentalReturnTime;
        this.renterPhoneNumber = renterPhoneNumber;
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

    public String getRentalPickupTime() { return rentalPickupTime; }
    public void setRentalPickupTime(String rentalPickupTime) { this.rentalPickupTime = rentalPickupTime; }

    public String getRentalReturnTime() { return rentalReturnTime; }
    public void setRentalReturnTime(String rentalReturnTime) { this.rentalReturnTime = rentalReturnTime; }

    public String getRenterPhoneNumber() { return renterPhoneNumber; }
    public void setRenterPhoneNumber(String renterPhoneNumber) { this.renterPhoneNumber = renterPhoneNumber; }

    public String getStatusDisplayName() {
        if ("PENDING_OWNER_APPROVAL".equalsIgnoreCase(status)) {
            return "Waiting for Owner Approval";
        }
        if ("OPEN_TO_SHARE".equalsIgnoreCase(status)) {
            return "Open to Share";
        }
        if ("CONFIRMED".equalsIgnoreCase(status)) {
            return "Confirmed";
        }
        if ("COMPLETED".equalsIgnoreCase(status)) {
            return "Completed";
        }
        if ("CANCELLED".equalsIgnoreCase(status)) {
            return "Cancelled";
        }
        return "Requested";
    }

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

    public boolean isShareable() {
        return "OPEN_TO_SHARE".equalsIgnoreCase(status) || "CONFIRMED".equalsIgnoreCase(status);
    }

    public void addShareRequest(ShareRideRequest shareRequest) {
        if (shareRequest != null) {
            shareRequests.add(shareRequest);
        }
    }

    public boolean updateShareRequestStatus(String requesterUsername, String nextStatus, String passengerPickupTime) {
        for (ShareRideRequest shareRequest : shareRequests) {
            if (shareRequest.getRequesterUsername().equalsIgnoreCase(requesterUsername)
                    && "PENDING".equalsIgnoreCase(shareRequest.getStatus())) {
                shareRequest.setStatus(nextStatus);
                shareRequest.setPassengerPickupTime(passengerPickupTime);
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
        return id + ":" + bikeId + ":" + bikeName + ":" + username + ":" + durationHours + ":" + pickupPoint + ":"
                + dropPoint + ":" + status + ":" + encode(rentalPickupTime) + ":" + encode(rentalReturnTime) + ":"
                + encode(renterPhoneNumber) + ":" + ShareRideRequest.serializeList(shareRequests);
    }

    public static String decodeField(String value) {
        return decode(value);
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value == null ? "" : value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }

    private static String decode(String value) {
        try {
            return URLDecoder.decode(value == null ? "" : value, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            return "";
        }
    }
}
