package com.byt.s30062;

import com.byt.s30062.model.Administrator;
import com.byt.s30062.model.Staff;
import com.byt.s30062.model.enums.LevelOfPermission;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class AdministratorTest {

    @BeforeEach
    void setup() {
        Staff.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Staff.clearExtent();
        new File("staff_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create administrator with valid attributes")
    void testValidAdministrator() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Alice", "Johnson", birthDate, 80000.0, false, LevelOfPermission.Senior);
        
        assertEquals("Alice", admin.getFirstName());
        assertEquals("Johnson", admin.getLastName());
        assertEquals(birthDate, admin.getDateOfBirth());
        assertEquals(80000.0, admin.getBaseSalary());
        assertFalse(admin.isIntern());
        assertEquals(LevelOfPermission.Senior, admin.getLevelOfPermission());
    }

    @Test
    @DisplayName("Should reject null levelOfPermission")
    void testNullLevelOfPermission() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator("Alice", "Johnson", birthDate, 80000.0, false, null));
    }

    @Test
    @DisplayName("Should set levelOfPermission with validation")
    void testSetLevelOfPermission() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Alice", "Johnson", birthDate, 80000.0, false, LevelOfPermission.Middle);
        
        admin.setLevelOfPermission(LevelOfPermission.Senior);
        assertEquals(LevelOfPermission.Senior, admin.getLevelOfPermission());
        
        assertThrows(IllegalArgumentException.class, () -> admin.setLevelOfPermission(null));
    }

    @Test
    @DisplayName("Should store Administrator in Staff extent (inheritance)")
    void testInheritance() {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate adminBirth = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Alice", "Johnson", adminBirth, 80000.0, false, LevelOfPermission.Senior);
        
        // Both should be in Staff extent
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(regularStaff));
        assertTrue(Staff.getExtent().contains(admin));
        
        // Verify Administrator type is preserved
        assertTrue(Staff.getExtent().stream().anyMatch(s -> s instanceof Administrator));
        assertTrue(Staff.getExtent().stream().anyMatch(s -> !(s instanceof Administrator)));
    }

    @Test
    @DisplayName("Should inherit Staff validations")
    void testInheritedValidations() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator(null, "Johnson", birthDate, 80000.0, false, LevelOfPermission.Middle));
        
        // Negative salary
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator("Alice", "Johnson", birthDate, -1000.0, false, LevelOfPermission.Middle));
        
        // NaN salary
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator("Alice", "Johnson", birthDate, Double.NaN, false, LevelOfPermission.Middle));
    }

    @Test
    @DisplayName("Should inherit Person validations")
    void testInheritedPersonValidations() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        // Future birthDate
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator("Alice", "Johnson", LocalDate.now().plusDays(1), 80000.0, false, LevelOfPermission.Middle));
        
        // BirthDate before 1900
        assertThrows(IllegalArgumentException.class,
            () -> new Administrator("Alice", "Johnson", LocalDate.of(1899, 12, 31), 80000.0, false, LevelOfPermission.Middle));
    }

    @Test
    @DisplayName("Should implement equals correctly with all permission levels")
    void testEqualsWithPermissions() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Administrator admin1 = new Administrator("Alice", "Johnson", birthDate, 80000.0, false, LevelOfPermission.Senior);
        Administrator admin2 = new Administrator("Alice", "Johnson", birthDate, 80000.0, false, LevelOfPermission.Middle);
        
        // Different permission levels - not equal
        assertNotEquals(admin1, admin2);
        
        Administrator admin3 = new Administrator("Alice", "Johnson", birthDate, 80000.0, false, LevelOfPermission.Senior);
        // Same permission level - equal
        assertEquals(admin1, admin3);
    }

    @Test
    @DisplayName("Should persist Administrator with Staff extent")
    void testPersistence() throws Exception {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate adminBirth = LocalDate.of(1985, 3, 20);
        Administrator admin = new Administrator("Alice", "Johnson", adminBirth, 80000.0, false, LevelOfPermission.Senior);
        
        Staff.saveExtent();
        Staff.clearExtent();
        Staff.loadExtent();
        
        assertEquals(2, Staff.getExtent().size());
        
        // Verify Administrator type and attributes are preserved
        Administrator loadedAdmin = (Administrator) Staff.getExtent().stream()
            .filter(s -> s instanceof Administrator)
            .findFirst()
            .orElseThrow();
        
        assertEquals("Alice", loadedAdmin.getFirstName());
        assertEquals("Johnson", loadedAdmin.getLastName());
        assertEquals(80000.0, loadedAdmin.getBaseSalary());
        assertEquals(LevelOfPermission.Senior, loadedAdmin.getLevelOfPermission());
    }

    @Test
    @DisplayName("Should handle all permission levels")
    void testAllPermissionLevels() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        
        Administrator junior = new Administrator("Alice", "Johnson", birthDate, 50000.0, false, LevelOfPermission.Junior);
        assertEquals(LevelOfPermission.Junior, junior.getLevelOfPermission());
        
        Administrator middle = new Administrator("Bob", "Smith", birthDate, 60000.0, false, LevelOfPermission.Middle);
        assertEquals(LevelOfPermission.Middle, middle.getLevelOfPermission());
        
        Administrator senior = new Administrator("Charlie", "Brown", birthDate, 80000.0, false, LevelOfPermission.Senior);
        assertEquals(LevelOfPermission.Senior, senior.getLevelOfPermission());
        
        assertEquals(3, Staff.getExtent().size());
    }
}
