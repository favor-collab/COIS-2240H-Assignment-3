import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class VehicleRentalTest {
private RentalSystem rentalSystem;

@BeforeEach
public void setUp() {
    rentalSystem = RentalSystem.getInstance();
}

@Test
public void testLicensePlateValidation() {
    // Valid plates
    assertDoesNotThrow(() -> {
        Vehicle v = new Car("Toyota", "Corolla", 2019, 5);
        v.setLicensePlate("AAA100");
    });
    assertDoesNotThrow(() -> {
        Vehicle v = new Car("Honda", "Civic", 2020, 5);
        v.setLicensePlate("ABC567");
    });
    assertDoesNotThrow(() -> {
        Vehicle v = new Car("Ford", "Focus", 2021, 5);
        v.setLicensePlate("ZZZ999");
    });

    // Invalid plates
    assertThrows(IllegalArgumentException.class, () -> {
        Vehicle v = new Car("Toyota", "Corolla", 2019, 5);
        v.setLicensePlate("");
    });
    assertThrows(IllegalArgumentException.class, () -> {
        Vehicle v = new Car("Honda", "Civic", 2020, 5);
        v.setLicensePlate(null);
    });
    assertThrows(IllegalArgumentException.class, () -> {
        Vehicle v = new Car("Ford", "Focus", 2021, 5);
        v.setLicensePlate("AAA1000");
    });
    assertThrows(IllegalArgumentException.class, () -> {
        Vehicle v = new Car("Chevy", "Malibu", 2022, 5);
        v.setLicensePlate("ZZZ99");
    });
}

@Test
public void testRentAndReturnVehicle() {
    Vehicle car = new Car("Toyota", "Corolla", 2019, 5);
    car.setLicensePlate("XYZ123");
    Customer customer = new Customer(100, "John");

    rentalSystem.addVehicle(car);
    rentalSystem.addCustomer(customer);

    // Vehicle should be initially AVAILABLE
    assertEquals(Vehicle.VehicleStatus.Available, car.getStatus(), "Vehicle should be available initially");

    // Rent the vehicle
    boolean rented = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
    assertTrue(rented, "Renting the vehicle should succeed");
    assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus(), "Vehicle status should be Rented");

    // Try renting again, should fail
    boolean rentedAgain = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 100.0);
    assertFalse(rentedAgain, "Renting the same vehicle again should fail");

    // Return the vehicle
    boolean returned = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
    assertTrue(returned, "Returning the vehicle should succeed");
    assertEquals(Vehicle.VehicleStatus.Available, car.getStatus(), "Vehicle status should be Available after return");

    // Try returning again, should fail
    boolean returnedAgain = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
    assertFalse(returnedAgain, "Returning the same vehicle again should fail");
}

@Test
public void testSingletonRentalSystem() throws Exception {
    Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
    int modifiers = constructor.getModifiers();
    assertTrue(Modifier.isPrivate(modifiers), "Constructor should be private to enforce Singleton");

    RentalSystem instance = RentalSystem.getInstance();
    assertNotNull(instance, "Singleton instance should not be null");

    RentalSystem anotherInstance = RentalSystem.getInstance();
    assertSame(instance, anotherInstance, "getInstance should always return the same instance");
}
}
