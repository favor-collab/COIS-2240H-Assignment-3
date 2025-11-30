
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentalSystemGUI extends Application {

    // Data models
    static class Vehicle {
        private String id;
        private String make;
        private String model;
        private int year;
        private boolean rented;

        public Vehicle(String id, String make, String model, int year) {
            this.id = id;
            this.make = make;
            this.model = model;
            this.year = year;
            this.rented = false;
        }

        public String getId() { return id; }
        public String getMake() { return make; }
        public String getModel() { return model; }
        public int getYear() { return year; }
        public boolean isRented() { return rented; }
        public void setRented(boolean rented) { this.rented = rented; }

        @Override
        public String toString() {
            return id + " - " + make + " " + model + " (" + year + ")" + (rented ? " [Rented]" : "");
        }
    }

    static class Customer {
        private String id;
        private String name;
        private String phone;

        public Customer(String id, String name, String phone) {
            this.id = id;
            this.name = name;
            this.phone = phone;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getPhone() { return phone; }

        @Override
        public String toString() {
            return id + " - " + name + " (" + phone + ")";
        }
    }

    static class RentalRecord {
        private Vehicle vehicle;
        private Customer customer; 
        private LocalDate rentDate;
        private LocalDate returnDate;

        public RentalRecord(Vehicle vehicle, Customer customer, LocalDate rentDate) {
            this.vehicle = vehicle;
            this.customer = customer;
            this.rentDate = rentDate;
            this.returnDate = null;
        }

        public Vehicle getVehicle() { return vehicle; }
        public Customer getCustomer() { return customer; }
        public LocalDate getRentDate() { return rentDate; }
        public LocalDate getReturnDate() { return returnDate; }
        public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

        @Override
        public String toString() {
            return "Vehicle: " + vehicle.getId() + " | Customer: " + customer.getName() +
                    " | Rented: " + rentDate + " | Returned: " + (returnDate == null ? "Not yet" : returnDate);
        }
    }

    // Data storage
    private final List<Vehicle> vehicles = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<RentalRecord> rentalHistory = new ArrayList<>();

    // GUI components
    private ListView<Vehicle> vehicleListView;
    private ListView<Customer> customerListView;
    private ListView<RentalRecord> rentalHistoryListView;

    private TextField vehicleIdField;
    private TextField vehicleMakeField;
    private TextField vehicleModelField;
    private TextField vehicleYearField;

    private TextField customerIdField;
    private TextField customerNameField;
    private TextField customerPhoneField;

    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Vehicle Rental System");

        // Vehicle input pane
        GridPane vehicleInputPane = new GridPane();
        vehicleInputPane.setHgap(10);
        vehicleInputPane.setVgap(10);
        vehicleInputPane.setPadding(new Insets(10));

        vehicleIdField = new TextField();
        vehicleMakeField = new TextField();
        vehicleModelField = new TextField();
        vehicleYearField = new TextField();

        vehicleInputPane.add(new Label("Vehicle ID:"), 0, 0);
        vehicleInputPane.add(vehicleIdField, 1, 0);
        vehicleInputPane.add(new Label("Make:"), 0, 1);
        vehicleInputPane.add(vehicleMakeField, 1, 1);
        vehicleInputPane.add(new Label("Model:"), 0, 2);
        vehicleInputPane.add(vehicleModelField, 1, 2);
        vehicleInputPane.add(new Label("Year:"), 0, 3);
        vehicleInputPane.add(vehicleYearField, 1, 3);

        Button addVehicleBtn = new Button("Add Vehicle");
        vehicleInputPane.add(addVehicleBtn, 1, 4);

        // Customer input pane
        GridPane customerInputPane = new GridPane();
        customerInputPane.setHgap(10);
        customerInputPane.setVgap(10);
        customerInputPane.setPadding(new Insets(10));

        customerIdField = new TextField();
        customerNameField = new TextField();
        customerPhoneField = new TextField();

        customerInputPane.add(new Label("Customer ID:"), 0, 0);
        customerInputPane.add(customerIdField, 1, 0);
        customerInputPane.add(new Label("Name:"), 0, 1);
        customerInputPane.add(customerNameField, 1, 1);
        customerInputPane.add(new Label("Phone:"), 0, 2);
        customerInputPane.add(customerPhoneField, 1, 2);

        Button addCustomerBtn = new Button("Add Customer");
        customerInputPane.add(addCustomerBtn, 1, 3);

        // Vehicle list view
        vehicleListView = new ListView<>();
        vehicleListView.setPrefHeight(150);
        VBox vehicleListPane = new VBox(new Label("Vehicles:"), vehicleListView);
        vehicleListPane.setPadding(new Insets(10));

        // Customer list view
        customerListView = new ListView<>();
        customerListView.setPrefHeight(150);
        VBox customerListPane = new VBox(new Label("Customers:"), customerListView);
        customerListPane.setPadding(new Insets(10));

        // Rental history list view
        rentalHistoryListView = new ListView<>();
        rentalHistoryListView.setPrefHeight(150);
        VBox rentalHistoryPane = new VBox(new Label("Rental History:"), rentalHistoryListView);
        rentalHistoryPane.setPadding(new Insets(10));

        // Rent and return buttons
        Button rentVehicleBtn = new Button("Rent Vehicle");
        Button returnVehicleBtn = new Button("Return Vehicle");

        HBox rentReturnPane = new HBox(10, rentVehicleBtn, returnVehicleBtn);
        rentReturnPane.setPadding(new Insets(10));

        // Available vehicles button
        Button showAvailableBtn = new Button("Show Available Vehicles");

        // Status label
        statusLabel = new Label();
        statusLabel.setPadding(new Insets(10));

        // Layout main pane
        BorderPane mainPane = new BorderPane();

        // Left side: input forms
        VBox leftPane = new VBox(20, vehicleInputPane, customerInputPane);
        leftPane.setPrefWidth(300);
        mainPane.setLeft(leftPane);

        // Center: lists and buttons
        VBox centerPane = new VBox(10, vehicleListPane, customerListPane, rentReturnPane, showAvailableBtn, rentalHistoryPane, statusLabel);
        centerPane.setPadding(new Insets(10));
        mainPane.setCenter(centerPane);

        // Event handlers
        addVehicleBtn.setOnAction(e -> addVehicle());
        addCustomerBtn.setOnAction(e -> addCustomer());
        rentVehicleBtn.setOnAction(e -> addVehicle());
        returnVehicleBtn.setOnAction(e -> addVehicle());
        showAvailableBtn.setOnAction(e -> displayAvailableVehicles());

        // Scene and stage setup
        Scene scene = new Scene(mainPane, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Object displayAvailableVehicles() {
		// TODO Auto-generated method stub
		return null;
	}

	private void addVehicle() {
        String id = vehicleIdField.getText().trim();
        String make = vehicleMakeField.getText().trim();
        String model = vehicleModelField.getText().trim();
        String yearStr = vehicleYearField.getText().trim();

        if (id.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty()) {
            statusLabel.setText("Please fill all vehicle fields.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            statusLabel.setText("Year must be a valid number.");
            return;
        }

        // Check for duplicate vehicle ID
        if (vehicles.stream().anyMatch(v -> v.getId().equals(id))) {
            statusLabel.setText("Vehicle ID already exists.");
            return;
        }

        Vehicle vehicle = new Vehicle(id, make, model, year);
        vehicles.add(vehicle);
        updateVehicleList();
        statusLabel.setText("Vehicle added successfully.");

        vehicleIdField.clear();
        vehicleMakeField.clear();
        vehicleModelField.clear();
        vehicleYearField.clear();
    }

    private void updateVehicleList() {
	}

	private void addCustomer() {
}
}