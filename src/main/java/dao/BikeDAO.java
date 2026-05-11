package dao;

import model.Bike;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BikeDAO {
    private final File file;

    public BikeDAO(String basePath) {
        File dataDirectory = DataDirectoryResolver.resolve(basePath);
        this.file = new File(dataDirectory, "bikes.txt");
    }

    public void addBike(Bike bike) throws IOException {
        bike.setId(getNextId());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(bike.toString());
            writer.newLine();
        }
    }

    public List<Bike> getAllBikes() throws IOException {
        List<Bike> bikes = new ArrayList<>();
        if (!file.exists()) {
            return bikes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(":", 8);
                if (parts.length >= 7) {
                    try {
                        bikes.add(new Bike(
                                Integer.parseInt(parts[0]),
                                parts[1],
                                parts[2],
                                parts[3],
                                Double.parseDouble(parts[4]),
                                parts[5],
                                parts[6],
                                parts.length == 8 ? parts[7] : ""
                        ));
                    } catch (Exception ex) {
                        // Skip corrupted line
                    }
                }
            }
        }

        return bikes;
    }

    public Bike getBikeById(int id) throws IOException {
        for (Bike bike : getAllBikes()) {
            if (bike.getId() == id) {
                return bike;
            }
        }
        return null;
    }

    public void updateBike(Bike updatedBike) throws IOException {
        List<Bike> bikes = getAllBikes();
        for (int i = 0; i < bikes.size(); i++) {
            if (bikes.get(i).getId() == updatedBike.getId()) {
                bikes.set(i, updatedBike);
                break;
            }
        }
        rewriteFile(bikes);
    }

    public void deleteBike(int id) throws IOException {
        List<Bike> bikes = getAllBikes();
        bikes.removeIf(bike -> bike.getId() == id);
        rewriteFile(bikes);
    }

    private int getNextId() throws IOException {
        List<Bike> bikes = getAllBikes();
        return bikes.isEmpty() ? 1 : bikes.get(bikes.size() - 1).getId() + 1;
    }

    private void rewriteFile(List<Bike> bikes) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Bike bike : bikes) {
                writer.write(bike.toString());
                writer.newLine();
            }
        }
    }
}
