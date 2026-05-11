package model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ShareRideRequest {
    private String requesterUsername;
    private String pickupPoint;
    private String stopPoint;
    private String status;
    private String passengerPickupTime;

    public ShareRideRequest() {
    }

    public ShareRideRequest(String requesterUsername, String pickupPoint, String stopPoint, String status) {
        this(requesterUsername, pickupPoint, stopPoint, status, "");
    }

    public ShareRideRequest(String requesterUsername, String pickupPoint, String stopPoint, String status, String passengerPickupTime) {
        this.requesterUsername = requesterUsername;
        this.pickupPoint = pickupPoint;
        this.stopPoint = stopPoint;
        this.status = status;
        this.passengerPickupTime = passengerPickupTime;
    }

    public String getRequesterUsername() {
        return requesterUsername;
    }

    public void setRequesterUsername(String requesterUsername) {
        this.requesterUsername = requesterUsername;
    }

    public String getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public String getStopPoint() {
        return stopPoint;
    }

    public void setStopPoint(String stopPoint) {
        this.stopPoint = stopPoint;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassengerPickupTime() {
        return passengerPickupTime;
    }

    public void setPassengerPickupTime(String passengerPickupTime) {
        this.passengerPickupTime = passengerPickupTime;
    }

    public String toRecord() {
        return encode(requesterUsername) + "," + encode(pickupPoint) + "," + encode(stopPoint) + ","
                + encode(status) + "," + encode(passengerPickupTime);
    }

    public static ShareRideRequest fromRecord(String record) {
        String[] parts = record.split(",", 5);
        if (parts.length < 4) {
            return null;
        }

        return new ShareRideRequest(
                decode(parts[0]),
                decode(parts[1]),
                decode(parts[2]),
                decode(parts[3]),
                parts.length == 5 ? decode(parts[4]) : ""
        );
    }

    public static List<ShareRideRequest> parseList(String serialized) {
        List<ShareRideRequest> requests = new ArrayList<>();
        if (serialized == null || serialized.trim().isEmpty()) {
            return requests;
        }

        String[] records = serialized.split(";");
        for (String record : records) {
            if (record.trim().isEmpty()) {
                continue;
            }

            ShareRideRequest request = fromRecord(record);
            if (request != null) {
                requests.add(request);
            }
        }

        return requests;
    }

    public static String serializeList(List<ShareRideRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (ShareRideRequest request : requests) {
            if (request == null) {
                continue;
            }

            if (builder.length() > 0) {
                builder.append(';');
            }
            builder.append(request.toRecord());
        }
        return builder.toString();
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
