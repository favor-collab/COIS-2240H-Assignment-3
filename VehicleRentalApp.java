import java.util.Scanner;
import java.time.LocalDate;

public class VehicleRentalApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RentalSystem rentalSystem = RentalSystem.getInstance(); // singleton

        while (true) {
            System.out.println("\n1: Add Vehicle\n" +
                               "2: Add Customer\n" +
                               "3: Rent Vehicle\n" +
                               "4: Return Vehicle\n" +
                               "5: Display Available Vehicles\n" +
                               "6: Show Rental History\n" +
                               "0: Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.println("Select vehicle type:\n1: Car\n2: Minibus\n3: Pickup Truck");
                    int type = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter license plate: ");
                    String plate = scanner.nextLine().toUpperCase();
                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();
                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();
                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;
                    switch (type) {
                        case 1:
                            System.out.print("Enter number of seats: ");
                            int seats = scanner.nextInt();
                            scanner.nextLine();
                            vehicle = new Car(make, model, year, seats);
                            break;
                        case 2:
                            System.out.print("Is accessible? (true/false): ");
                            boolean accessible = scanner.nextBoolean();
                            scanner.nextLine();
                            vehicle = new Minibus(make, model, year, accessible);
                            break;
                        case 3:
                            System.out.print("Enter cargo size: ");
                            double cargo = scanner.nextDouble();
                            scanner.nextLine();
                            System.out.print("Has trailer? (true/false): ");
                            boolean trailer = scanner.nextBoolean();
                            scanner.nextLine();
                            vehicle = new PickupTruck(make, model, year, cargo, trailer);
                            break;
                        default:
                            System.out.println("Invalid vehicle type.");
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        rentalSystem.addVehicle(vehicle);
                        System.out.println("Vehicle added successfully.");
                    }
                    break;

                case 2:
                    System.out.print("Enter customer ID: ");
                    int cid = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter customer name: ");
                    String cname = scanner.nextLine();

                    Customer customer = new Customer(cid, cname);
                    rentalSystem.addCustomer(customer);
                    System.out.println("Customer added successfully.");
                    break;

                case 3: // Rent
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    System.out.print("Enter license plate to rent: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    rentalSystem.displayAllCustomers();
                    System.out.print("Enter customer ID: ");
                    int rentCid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle rentVehicle = rentalSystem.findVehicleByPlate(rentPlate);
                    Customer rentCustomer = rentalSystem.findCustomerById(rentCid);

                    if (rentVehicle != null && rentCustomer != null) {
                        rentalSystem.rentVehicle(rentVehicle, rentCustomer, LocalDate.now(), rentAmount);
                    } else {
                        System.out.println("Vehicle or customer not found.");
                    }
                    break;

                case 4: // Return
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Rented);
                    System.out.print("Enter license plate to return: ");
                    String returnPlate = scanner.nextLine().toUpperCase();

                    rentalSystem.displayAllCustomers();
                    System.out.print("Enter customer ID: ");
                    int returnCid = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter any additional fees: ");
                    double extraFees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle returnVehicle = rentalSystem.findVehicleByPlate(returnPlate);
                    Customer returnCustomer = rentalSystem.findCustomerById(returnCid);

                    if (returnVehicle != null && returnCustomer != null) {
                        rentalSystem.returnVehicle(returnVehicle, returnCustomer, LocalDate.now(), extraFees);
                    } else {
                        System.out.println("Vehicle or customer not found.");
                    }
                    break;

                case 5:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    break;

                case 6:
                    rentalSystem.displayRentalHistory();
                    break;

                case 0:
                    System.out.println("Exiting program...");
                    scanner.close();
                    System.exit(0);
            }
        }
    }
}
