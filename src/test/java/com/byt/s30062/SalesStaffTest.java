package com.byt.s30062;

import com.byt.s30062.model.SalesStaff;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SalesStaffTest {

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
    @DisplayName("Should create sales staff with valid attributes")
    void testValidSalesStaff() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 5000.0);
        
        assertEquals("John", salesperson.getFirstName());
        assertEquals("Salesman", salesperson.getLastName());
        assertEquals(birthDate, salesperson.getDateOfBirth());
        assertEquals(40000.0, salesperson.getBaseSalary());
        assertFalse(salesperson.isIntern());
        assertEquals(5000.0, salesperson.getSalesBonus());
    }

    @Test
    @DisplayName("Should reject negative salesBonus")
    void testNegativeSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, 40000.0, false, -1000.0));
    }

    @Test
    @DisplayName("Should reject NaN salesBonus")
    void testNaNSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, 40000.0, false, Double.NaN));
    }

    @Test
    @DisplayName("Should reject infinite salesBonus")
    void testInfiniteSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, 40000.0, false, Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Should reject salesBonus exceeding maximum")
    void testExceedsMaxSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 20_000_000.0));
    }

    @Test
    @DisplayName("Should allow zero salesBonus")
    void testZeroSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 0.0);
        assertEquals(0.0, salesperson.getSalesBonus());
    }

    @Test
    @DisplayName("Should set salesBonus with validation")
    void testSetSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 5000.0);
        
        salesperson.setSalesBonus(7500.0);
        assertEquals(7500.0, salesperson.getSalesBonus());
        
        assertThrows(IllegalArgumentException.class, () -> salesperson.setSalesBonus(-100.0));
        assertThrows(IllegalArgumentException.class, () -> salesperson.setSalesBonus(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> salesperson.setSalesBonus(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Should store SalesStaff in Staff extent (inheritance)")
    void testInheritance() {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate salesBirth = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", salesBirth, 40000.0, false, 5000.0);
        
        // Both should be in Staff extent
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(regularStaff));
        assertTrue(Staff.getExtent().contains(salesperson));
        
        // Verify SalesStaff type is preserved
        assertTrue(Staff.getExtent().stream().anyMatch(s -> s instanceof SalesStaff));
        assertTrue(Staff.getExtent().stream().anyMatch(s -> !(s instanceof SalesStaff)));
    }

    @Test
    @DisplayName("Should inherit Staff validations")
    void testInheritedValidations() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff(null, "Salesman", birthDate, 40000.0, false, 5000.0));
        
        // Negative base salary
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, -1000.0, false, 5000.0));
        
        // NaN base salary
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", birthDate, Double.NaN, false, 5000.0));
    }

    @Test
    @DisplayName("Should inherit Person validations")
    void testInheritedPersonValidations() {
        // Future birthDate
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", LocalDate.now().plusDays(1), 40000.0, false, 5000.0));
        
        // BirthDate before 1900
        assertThrows(IllegalArgumentException.class,
            () -> new SalesStaff("John", "Salesman", LocalDate.of(1899, 12, 31), 40000.0, false, 5000.0));
    }

    @Test
    @DisplayName("Should implement equals correctly with different bonuses")
    void testEqualsWithBonuses() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff sales1 = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 5000.0);
        SalesStaff sales2 = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 7500.0);
        
        // Different bonuses - not equal
        assertNotEquals(sales1, sales2);
        
        SalesStaff sales3 = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 5000.0);
        // Same bonus - equal
        assertEquals(sales1, sales3);
    }

    @Test
    @DisplayName("Should persist SalesStaff with Staff extent")
    void testPersistence() throws Exception {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate salesBirth = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", salesBirth, 40000.0, false, 5000.0);
        
        Staff.saveExtent();
        Staff.clearExtent();
        Staff.loadExtent();
        
        assertEquals(2, Staff.getExtent().size());
        
        // Verify SalesStaff type and attributes are preserved
        SalesStaff loadedSales = (SalesStaff) Staff.getExtent().stream()
            .filter(s -> s instanceof SalesStaff)
            .findFirst()
            .orElseThrow();
        
        assertEquals("John", loadedSales.getFirstName());
        assertEquals("Salesman", loadedSales.getLastName());
        assertEquals(40000.0, loadedSales.getBaseSalary());
        assertEquals(5000.0, loadedSales.getSalesBonus());
    }

    @Test
    @DisplayName("Should handle edge case: maximum valid salesBonus")
    void testMaxSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 9_999_999.99);
        assertEquals(9_999_999.99, salesperson.getSalesBonus());
    }

    @Test
    @DisplayName("Should handle edge case: very small salesBonus")
    void testVerySmallSalesBonus() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff salesperson = new SalesStaff("John", "Salesman", birthDate, 40000.0, false, 0.01);
        assertEquals(0.01, salesperson.getSalesBonus());
    }

    @Test
    @DisplayName("Should handle multiple sales staff with different bonuses")
    void testMultipleSalesStaff() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        
        SalesStaff sales1 = new SalesStaff("John", "Salesman1", birthDate, 40000.0, false, 5000.0);
        SalesStaff sales2 = new SalesStaff("Jane", "Salesman2", birthDate, 45000.0, false, 7500.0);
        SalesStaff sales3 = new SalesStaff("Bob", "Salesman3", birthDate, 50000.0, false, 10000.0);
        
        assertEquals(3, Staff.getExtent().size());
        assertEquals(5000.0, sales1.getSalesBonus());
        assertEquals(7500.0, sales2.getSalesBonus());
        assertEquals(10000.0, sales3.getSalesBonus());
    }
}
