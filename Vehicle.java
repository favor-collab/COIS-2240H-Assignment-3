public abstract class Vehicle {

    private int id;
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Rented }

    // -------------------------
    //   REFACTORED CONSTRUCTOR
    // -------------------------
    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
    }

    // -------------------------------------------------------
    //   NEW HELPER METHOD (no duplication in constructor now)
    // -------------------------------------------------------
    private String capitalize(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        input = input.trim();
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // ----------------------------------
    //   ADD setId() TO FIX YOUR ERROR
    // ----------------------------------
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    // ----------------------------------
    //   Other existing getters/setters
    // ----------------------------------

    public void setLicensePlate(String plate) {
        this.licensePlate = plate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }
}

