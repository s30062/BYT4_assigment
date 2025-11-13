package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

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
        Staff s = new Staff("Alice", "Johnson", 50000.0, false);
        
        assertEquals("Alice", s.getFirstName());
        assertEquals("Johnson", s.getLastName());
        assertEquals(50000.0, s.getBaseSalary());
        assertFalse(s.isIntern());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        // Null/blank names
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff(null, "Doe", 50000.0, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("", "Doe", 50000.0, false));
        
        // Invalid salary
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", -1000.0, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", Double.NaN, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", Double.POSITIVE_INFINITY, false));
        assertThrows(IllegalArgumentException.class, 
            () -> new Staff("John", "Doe", 20_000_000.0, false));
    }

    @Test
    @DisplayName("Should store staff in extent")
    void testExtent() {
        assertEquals(0, Staff.getExtent().size());
        
        Staff s1 = new Staff("Alice", "Johnson", 50000.0, false);
        assertEquals(1, Staff.getExtent().size());
        
        Staff s2 = new Staff("Bob", "Smith", 60000.0, true);
        assertEquals(2, Staff.getExtent().size());
        
        assertTrue(Staff.getExtent().contains(s1));
        assertTrue(Staff.getExtent().contains(s2));
    }

    @Test
    @DisplayName("Should store Manager in Staff extent (inheritance)")
    void testManagerInheritance() {
        Staff s = new Staff("Alice", "Johnson", 50000.0, false);
        Manager m = new Manager("Bob", "Smith", 80000.0, false);
        
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(s));
        assertTrue(Staff.getExtent().contains(m));
        
        // Verify Manager is in extent
        assertTrue(Staff.getExtent().stream().anyMatch(staff -> staff instanceof Manager));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        Staff s = new Staff("Alice", "Johnson", 50000.0, false);
        
        var extent1 = Staff.getExtent();
        var extent2 = Staff.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Staff.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent with polymorphism")
    void testPersistence() throws IOException, ClassNotFoundException {
        Staff s = new Staff("Alice", "Johnson", 50000.0, false);
        Manager m = new Manager("Bob", "Smith", 80000.0, false);
        
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
