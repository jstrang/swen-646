import com.google.gson.Gson;
import exceptions.InvalidVehicleException;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class Vehicle {

    private final String make;
    private final String model;
    private final String vin;
    private final int year;
    private final float price;
    private final VehicleType type;

    public Vehicle(String make, String model, String vin, int year, float price, VehicleType type) {
        this.make = make;
        this.model = model;
        if (vin.length() != 17) {
            throw new InvalidVehicleException("VIN was " + vin + ", must be 17 characters long");
        }
        this.vin = vin;
        this.year = year;
        this.price = price;
        this.type = type;
    }

    public Vehicle(String fileName) {
        Gson gson = new Gson();
        try {
            // Create parse vehicle from JSON file
            Vehicle v = gson.fromJson(new FileReader(fileName), Vehicle.class);

            // This does make one redundant object, but we're doing it this way to expose a normal constructor to the
            // user instead of a static method
            this.make = v.getMake();
            this.model = v.getModel();
            this.vin = v.getVin();
            // We need to validate vin length here too
            if (vin.length() != 17) {
                throw new InvalidVehicleException("VIN was " + vin + ", must be 17 characters long");
            }
            this.year = v.getYear();
            this.price = v.getPrice();
            this.type = v.getType();

        } catch (FileNotFoundException e) {
            throw new InvalidVehicleException("Failed to create vehicle from file, file " + fileName + " not found");
        }
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getVin() {
        return vin;
    }

    public int getYear() {
        return year;
    }

    public float getPrice() {
        return price;
    }

    public VehicleType getType() {
        return type;
    }

    /**
     * Override of toString to give human readable description of the vehicle
     * @return String representation of attributes
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Make: ").append(make).append("\n")
                .append("Model: ").append(model).append("\n")
                .append("VIN: ").append(vin).append("\n")
                .append("Year: ").append(year).append("\n")
                .append("Price: ").append(price).append("\n")
                .append("Type: ").append(type.getName());
        return builder.toString();
    }

}
