public enum VehicleType {
    SEDAN("Sedan", 39.99), TRUCK_OR_VAN("Truck or van", 49.99), SUV("SUV", 42.99);

    private final String name;
    private final double fee;

    private VehicleType(String name, double fee) {
        this.name = name;
        this.fee = fee;
    }

    public String getName() {
        return name;
    }

    public double getFee() {
        return fee;
    }
}
