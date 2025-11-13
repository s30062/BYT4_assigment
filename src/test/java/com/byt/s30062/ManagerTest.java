package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Report;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ManagerTest {

    @BeforeEach
    void setup() {
        Staff.clearExtent();
        Report.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Staff.clearExtent();
        Report.clearExtent();
        new File("staff_extent.ser").delete();
        new File("report_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create manager with valid attributes")
    void testValidManager() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        
        assertEquals("Alice", m.getFirstName());
        assertEquals("Johnson", m.getLastName());
        assertEquals(80000.0, m.getBaseSalary());
        assertFalse(m.isIntern());
    }

    @Test
    @DisplayName("Should inherit Staff validations")
    void testInheritedValidations() {
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new Manager(null, "Johnson", 80000.0, false));
        
        // Blank firstName
        assertThrows(IllegalArgumentException.class,
            () -> new Manager("", "Johnson", 80000.0, false));
        
        // Negative salary
        assertThrows(IllegalArgumentException.class,
            () -> new Manager("Alice", "Johnson", -1000.0, false));
        
        // NaN salary
        assertThrows(IllegalArgumentException.class,
            () -> new Manager("Alice", "Johnson", Double.NaN, false));
    }

    @Test
    @DisplayName("Should store Manager in Staff extent (inheritance)")
    void testInheritance() {
        Staff regularStaff = new Staff("Bob", "Smith", 50000.0, true);
        Manager manager = new Manager("Alice", "Johnson", 80000.0, false);
        
        // Both should be in Staff extent
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(regularStaff));
        assertTrue(Staff.getExtent().contains(manager));
        
        // Verify Manager type is preserved
        assertTrue(Staff.getExtent().stream().anyMatch(s -> s instanceof Manager));
        assertTrue(Staff.getExtent().stream().anyMatch(s -> !(s instanceof Manager)));
    }

    @Test
    @DisplayName("Should persist Manager with Staff extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Staff regularStaff = new Staff("Bob", "Smith", 50000.0, true);
        Manager manager = new Manager("Alice", "Johnson", 80000.0, false);
        
        Staff.saveExtent();
        Staff.clearExtent();
        Staff.loadExtent();
        
        assertEquals(2, Staff.getExtent().size());
        
        // Verify Manager type and attributes are preserved
        Manager loadedManager = (Manager) Staff.getExtent().stream()
            .filter(s -> s instanceof Manager)
            .findFirst()
            .orElseThrow();
        
        assertEquals("Alice", loadedManager.getFirstName());
        assertEquals("Johnson", loadedManager.getLastName());
        assertEquals(80000.0, loadedManager.getBaseSalary());
    }

    @Test
    @DisplayName("Should allow Manager to generate reports")
    void testReportGeneration() {
        Manager m = new Manager("Alice", "Johnson", 80000.0, false);
        
        Report r1 = new Report(m, "Q1 Sales Report: Revenue increased by 15%");
        Report r2 = new Report(m, "Q2 Performance Report: Team exceeded targets");
        
        assertEquals(2, Report.getExtent().size());
        assertTrue(Report.getExtent().contains(r1));
        assertTrue(Report.getExtent().contains(r2));
    }
}
