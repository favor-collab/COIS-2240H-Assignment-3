import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;

public class RentalSystem {

    private static RentalSystem instance;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    private RentalSystem() { 
        loadData();   // STEP 3: load files at startup
    }

    public static synchronized RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Duplicate vehicle NOT added: Plate already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);  // save immediately
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Duplicate customer NOT added: ID already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);  // save immediately
        return true;
    }

    // Rent vehicle
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord r = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    // Return vehicle
    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord r = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(r);
            saveRecord(r);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    // Display Vehicles
    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null)
            System.out.println("\n=== All Vehicles ===");
        else
            System.out.println("\n=== " + status + " Vehicles ===");

        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");

        boolean found = false;
        for (Vehicle vehicle : vehicles) {

            if (status == null || vehicle.getStatus() == status) {
                found = true;

                String vehicleType =
                        (vehicle instanceof Car) ? "Car" :
                        (vehicle instanceof Minibus) ? "Minibus" :
                        (vehicle instanceof PickupTruck) ? "PickupTruck" :
                        "Unknown";

                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n",
                    vehicleType,
                    vehicle.getLicensePlate(),
                    vehicle.getMake(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    vehicle.getStatus());
            }
        }

        if (!found) {
            System.out.println("  No vehicles found.");
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");

            for (RentalRecord record : rentalHistory.getRentalHistory()) {
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(),
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount());
            }
            System.out.println();
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate() != null &&
                v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id)
                return c;
        }
        return null;
    }

    private void loadData() {

        // ----- Load Vehicles -----
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                // Format: type,plate,make,model,year
                String[] p = line.split(",");

                String type = p[0];
                String plate = p[1];
                String make = p[2];
                String model = p[3];
                int year = Integer.parseInt(p[4]);

                Vehicle v = null;
                if (type.equals("Car"))
                    v = new Car(make, model, year);
                else if (type.equals("Minibus"))
                    v = new Minibus(make, model, year, false);
                else if (type.equals("PickupTruck"))
                    v = new PickupTruck(make, model, year, 0.0, false);

                if (v != null) {
                    v.setLicensePlate(plate);
                    vehicles.add(v);
                }
            }
        } catch (Exception e) { }

        // ----- Load Customers -----
        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Format: id,name
                String[] p = line.split(",");
                int id = Integer.parseInt(p[0]);
                String name = p[1];
                customers.add(new Customer(id, name));
            }
        } catch (Exception e) { }

        // ----- Load Rental Records -----
        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {

                // Format: type,plate,id,date,amount
                String[] p = line.split(",");

                String type = p[0];
                String plate = p[1];
                int custId = Integer.parseInt(p[2]);
                LocalDate date = LocalDate.parse(p[3]);
                double amount = Double.parseDouble(p[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(custId);

                if (v != null && c != null) {
                    rentalHistory.addRecord(new RentalRecord(v, c, date, amount, type));
                }
            }
        } catch (Exception e) { }
    }

    private void saveVehicle(Vehicle v) {
        try (FileWriter fw = new FileWriter("vehicles.txt", true)) {
            fw.write(v.getClass().getSimpleName() + "," +
                     v.getLicensePlate() + "," +
                     v.getMake() + "," +
                     v.getModel() + "," +
                     v.getYear() + "\n");
        } catch (Exception e) { }
    }

    private void saveCustomer(Customer c) {
        try (FileWriter fw = new FileWriter("customers.txt", true)) {
            fw.write(c.getCustomerId() + "," + c.getCustomerName() + "\n");
        } catch (Exception e) { }
    }

    private void saveRecord(RentalRecord r) {
        try (FileWriter fw = new FileWriter("rental_records.txt", true)) {
            fw.write(r.getRecordType() + "," +
                     r.getVehicle().getLicensePlate() + "," +
                     r.getCustomer().getCustomerId() + "," +
                     r.getRecordDate() + "," +
                     r.getTotalAmount() + "\n");
        } catch (Exception e) { }
    }
}

