import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException; 

public class RentalSystem {

    private static RentalSystem instance;

    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private List<RentalRecord> rentalHistory;

    private RentalSystem() {
        vehicles = new ArrayList<>();
        customers = new ArrayList<>();
        rentalHistory = new ArrayList<>();
        loadData();  // load previously saved data at startup
    }

    public static synchronized RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // ------------------ VEHICLES ------------------
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Duplicate vehicle license plate. Vehicle not added.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        System.out.println("Vehicle added successfully.");
        return true;
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public void displayVehicles(Vehicle.VehicleStatus status) {
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                System.out.println(v); // assumes Vehicle.toString() prints nicely
            }
        }
    }

    private void saveVehicle(Vehicle vehicle) {
        try (FileWriter fw = new FileWriter("vehicles.txt", true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {

            out.println(vehicle.getInfo() + "," +
                        vehicle.getLicensePlate() + "," +
                        vehicle.getMake() + "," +
                        vehicle.getModel() + "," +
                        vehicle.getYear() + "," +
                        vehicle.getStatus());

        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    // ------------------ CUSTOMERS ------------------
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Duplicate customer ID. Customer not added.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        System.out.println("Customer added successfully.");
        return true;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) return c;
        }
        return null;
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println(c); // assumes Customer.toString() prints nicely
        }
    }

    private void saveCustomer(Customer customer) {
        try (FileWriter fw = new FileWriter("customers.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(customer.getCustomerId() + "," + customer.getCustomerName());
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    // ------------------ RENTAL ------------------
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.add(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.add(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.isEmpty()) {
            System.out.println("No rental history.");
        } else {
            for (RentalRecord r : rentalHistory) {
                System.out.println(r); // assumes RentalRecord.toString() prints nicely
            }
        }
    }

    private void saveRecord(RentalRecord record) {
        try (FileWriter fw = new FileWriter("rental_records.txt", true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(record.getRecordType() + "," +
                     record.getVehicle().getLicensePlate() + "," +
                     record.getCustomer().getCustomerName() + "," +
                     record.getRecordDate() + "," +
                     record.getTotalAmount());
            bw.newLine();

        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    // ------------------ LOAD DATA ------------------
    private void loadData() {
        loadVehicles();
        loadCustomers();
        loadRentalRecords();
    }

    private void loadVehicles() {
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int id = Integer.parseInt(parts[0]);
                String plate = parts[1];
                String make = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[5]);

                Vehicle vehicle = new Car(make, model, year, 4); // default 4 seats; adjust as needed
                vehicle.setLicensePlate(plate);
                vehicle.setStatus(status);
                vehicles.add(vehicle);
            }
        } catch (IOException e) {
            System.out.println("No vehicles data found.");
        }
    }

    private void loadCustomers() {
        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                customers.add(new Customer(id, name));
            }
        } catch (IOException e) {
            System.out.println("No customers data found.");
        }
    }

    private void loadRentalRecords() {
        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 5);
                String type = parts[0];
                String plate = parts[1];
                String customerName = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                double amount = Double.parseDouble(parts[4]);

                Vehicle vehicle = findVehicleByPlate(plate);
                Customer customer = null;
                for (Customer c : customers) {
                    if (c.getCustomerName().equals(customerName)) {
                        customer = c;
                        break;
                    }
                }

                if (vehicle != null && customer != null) {
                    rentalHistory.add(new RentalRecord(vehicle, customer, date, amount, type));
                }
            }
        } catch (IOException e) {
            System.out.println("No rental records found.");
        }
    }
}



