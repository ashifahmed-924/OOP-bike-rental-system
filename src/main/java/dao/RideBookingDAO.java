package dao;

import model.RideBooking;
import model.ShareRideRequest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RideBookingDAO {
    private final File file;

    public RideBookingDAO(String basePath) {
        File dataDirectory = DataDirectoryResolver.resolve(basePath);
        this.file = new File(dataDirectory, "ride-bookings.txt");
    }

    public void addBooking(RideBooking booking) throws IOException {
        booking.setId(getNextId());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(booking.toString());
            writer.newLine();
        }
    }

    public List<RideBooking> getBookingsByUsername(String username) throws IOException {
        List<RideBooking> userBookings = new ArrayList<>();
        for (RideBooking booking : getAllBookings()) {
            if (booking.getUsername().equals(username)) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public List<RideBooking> getAllBookings() throws IOException {
        List<RideBooking> bookings = new ArrayList<>();
        if (!file.exists()) {
            return bookings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(":", 12);
                if (parts.length >= 8) {
                    try {
                        List<ShareRideRequest> shareRequests;
                        String rentalPickupTime = "";
                        String rentalReturnTime = "";
                        String renterPhoneNumber = "";
                        if (parts.length >= 12) {
                            rentalPickupTime = RideBooking.decodeField(parts[8]);
                            rentalReturnTime = RideBooking.decodeField(parts[9]);
                            renterPhoneNumber = RideBooking.decodeField(parts[10]);
                            shareRequests = ShareRideRequest.parseList(parts[11]);
                        } else {
                            shareRequests = parts.length == 9 ? ShareRideRequest.parseList(parts[8]) : new ArrayList<ShareRideRequest>();
                        }

                        bookings.add(new RideBooking(
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1]),
                                parts[2],
                                parts[3],
                                Integer.parseInt(parts[4]),
                                parts[5],
                                parts[6],
                                parts[7],
                                rentalPickupTime,
                                rentalReturnTime,
                                renterPhoneNumber,
                                shareRequests
                        ));
                    } catch (Exception ex) {
                        // Skip corrupted line
                    }
                }
            }
        }

        return bookings;
    }

    public RideBooking getBookingById(int id) throws IOException {
        for (RideBooking booking : getAllBookings()) {
            if (booking.getId() == id) {
                return booking;
            }
        }
        return null;
    }

    public boolean hasOngoingBookingForBike(int bikeId) throws IOException {
        for (RideBooking booking : getAllBookings()) {
            if (booking.getBikeId() == bikeId && booking.isOngoing()) {
                return true;
            }
        }
        return false;
    }

    public void updateBooking(RideBooking updatedBooking) throws IOException {
        List<RideBooking> bookings = getAllBookings();
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId() == updatedBooking.getId()) {
                bookings.set(i, updatedBooking);
                break;
            }
        }
        rewriteFile(bookings);
    }

    public void deleteBooking(int id) throws IOException {
        List<RideBooking> bookings = getAllBookings();
        bookings.removeIf(booking -> booking.getId() == id);
        rewriteFile(bookings);
    }

    private int getNextId() throws IOException {
        List<RideBooking> bookings = getAllBookings();
        return bookings.isEmpty() ? 1 : bookings.get(bookings.size() - 1).getId() + 1;
    }

    private void rewriteFile(List<RideBooking> bookings) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (RideBooking booking : bookings) {
                writer.write(booking.toString());
                writer.newLine();
            }
        }
    }
}
