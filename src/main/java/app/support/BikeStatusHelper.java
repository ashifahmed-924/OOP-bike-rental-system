package app.support;

import dao.BikeDAO;
import dao.RideBookingDAO;
import model.Bike;
import model.RideBooking;

import java.io.IOException;
import java.util.List;

public final class BikeStatusHelper {

    private BikeStatusHelper() {
    }

    public static void syncBikeStatus(String basePath, int bikeId) throws IOException {
        BikeDAO bikeDAO = new BikeDAO(basePath);
        RideBookingDAO bookingDAO = new RideBookingDAO(basePath);

        Bike bike = bikeDAO.getBikeById(bikeId);
        if (bike == null) {
            return;
        }

        boolean hasOngoingBooking = hasOngoingBooking(bookingDAO.getAllBookings(), bikeId);
        String nextStatus = hasOngoingBooking ? "BOOKED" : "AVAILABLE";

        if (!nextStatus.equalsIgnoreCase(bike.getStatus())) {
            bike.setStatus(nextStatus);
            bikeDAO.updateBike(bike);
        }
    }

    private static boolean hasOngoingBooking(List<RideBooking> bookings, int bikeId) {
        for (RideBooking booking : bookings) {
            if (booking.getBikeId() == bikeId && booking.isOngoing()) {
                return true;
            }
        }
        return false;
    }
}
