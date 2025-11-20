package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

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
    @DisplayName("Should create staff with valid attributes")
    void testValidStaff() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Staff s = new Staff("Alice", "Johnson", birthDate, 50000.0, false);
        
        assertEquals("Alice", s.getFirstName());
        assertEquals("Johnson", s.getLastName());
        assertEquals(birthDate, s.getDateOfBirth());
        assertEquals(50000.0, s.getBaseSalary());
        assertFalse(s.isIntern());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        
        // Null/blank names
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff(null, "Doe", birthDate, 50000.0, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("", "Doe", birthDate, 50000.0, false));
        
        // Invalid salary
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", birthDate, -1000.0, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", birthDate, Double.NaN, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", birthDate, Double.POSITIVE_INFINITY, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", birthDate, 20_000_000.0, false));
    }

    @Test
    @DisplayName("Should store staff in extent")
    void testExtent() {
        assertEquals(0, Staff.getExtent().size());
        
        LocalDate birthDate1 = LocalDate.of(1990, 5, 15);
        Staff s1 = new Staff("Alice", "Johnson", birthDate1, 50000.0, false);
        assertEquals(1, Staff.getExtent().size());
        
        LocalDate birthDate2 = LocalDate.of(1985, 3, 20);
        Staff s2 = new Staff("Bob", "Smith", birthDate2, 60000.0, true);
        assertEquals(2, Staff.getExtent().size());
        
        assertTrue(Staff.getExtent().contains(s1));
        assertTrue(Staff.getExtent().contains(s2));
    }

    @Test
    @DisplayName("Should store Manager in Staff extent (inheritance)")
    void testManagerInheritance() {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff s = new Staff("Alice", "Johnson", staffBirth, 50000.0, false);
        
        LocalDate managerBirth = LocalDate.of(1985, 3, 20);
        Manager m = new Manager("Bob", "Smith", managerBirth, 80000.0, false);
        
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(s));
        assertTrue(Staff.getExtent().contains(m));
        
        // Verify Manager is in extent
        assertTrue(Staff.getExtent().stream().anyMatch(staff -> staff instanceof Manager));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Staff s = new Staff("Alice", "Johnson", birthDate, 50000.0, false);
        
        var extent1 = Staff.getExtent();
        var extent2 = Staff.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Staff.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent with polymorphism")
    void testPersistence() throws IOException, ClassNotFoundException {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff s = new Staff("Alice", "Johnson", staffBirth, 50000.0, false);
        
        LocalDate managerBirth = LocalDate.of(1985, 3, 20);
        Manager m = new Manager("Bob", "Smith", managerBirth, 80000.0, false);
        
        Staff.saveExtent();
        assertTrue(new File("staff_extent.ser").exists());
        
        Staff.clearExtent();
        assertEquals(0, Staff.getExtent().size());
        
        Staff.loadExtent();
        assertEquals(2, Staff.getExtent().size());
        
        // Verify Manager type is preserved
        assertTrue(Staff.getExtent().stream().anyMatch(staff -> staff instanceof Manager));
        assertTrue(Staff.getExtent().stream().anyMatch(staff -> staff.getFirstName().equals("Alice")));
    }
}
