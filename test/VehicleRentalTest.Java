import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class VehicleRentalTest {

    private RentalSystem rentalSystem;

    @BeforeEach
    public void setUp() {
        rentalSystem = RentalSystem.getInstance();
        // no clearData(), just rely on unique test data
    }

    @Test
    public void testLicensePlate() {
        // Valid plates
        assertDoesNotThrow(() -> {
            Vehicle v1 = new Car("Toyota", "Corolla", 2019, 5);
            v1.setLicensePlate("AAA100");
        });
        assertDoesNotThrow(() -> {
            Vehicle v2 = new Car("Honda", "Civic", 2020, 5);
            v2.setLicensePlate("ABC567");
        });
        assertDoesNotThrow(() -> {
            Vehicle v3 = new Car("Ford", "Focus", 2021, 5);
            v3.setLicensePlate("ZZZ999");
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
            v.setLicensePlate("AAA1000"); // too long
        });
        assertThrows(IllegalArgumentException.class, () -> {
            Vehicle v = new Car("Chevy", "Malibu", 2022, 5);
            v.setLicensePlate("ZZZ99"); // too short
        });
    }
}
