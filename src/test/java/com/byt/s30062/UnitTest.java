package com.byt.s30062;

import com.byt.s30062.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    @BeforeEach
    void setup() {
        Unit.clearExtent();
        Product.clearExtent();
        Purchase.clearExtent();
        Customer.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Unit.clearExtent();
        Product.clearExtent();
        Purchase.clearExtent();
        Customer.clearExtent();
        new File("unit_extent.ser").delete();
    }


    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        
        Unit u1 = new Unit(mfgDate, "SN123456789", p);
        Unit u2 = new Unit(mfgDate, "SN123456789", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 16), "SN123456789", p);
        
        assertEquals(u1, u2);
        assertNotEquals(u1, u3); // Different manufacturing date
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        
        Unit u1 = new Unit(mfgDate, "SN123456789", p);
        Unit u2 = new Unit(mfgDate, "SN123456789", p);
        
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    @DisplayName("Should generate proper toString")
    void testToString() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "SN123456789", p);
        
        String result = unit.toString();
        assertTrue(result.contains("SN123456789"));
        assertTrue(result.contains("2024-01-15"));
        assertTrue(result.contains("iPhone"));
        assertTrue(result.contains("false")); // Not purchased
    }

    @Test
    @DisplayName("Should handle maximum valid serialNumber length")
    void testMaxSerialNumberLength() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        String maxSN = "A".repeat(100);
        
        Unit unit = new Unit(mfgDate, maxSN, p);
        assertEquals(100, unit.getSerialNumber().length());
    }

    @Test
    @DisplayName("Should handle units from same product")
    void testMultipleUnitsOfSameProduct() {
        Product p = new Product("iPhone", "Black", 999.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p);
        
        assertEquals(3, Unit.getExtent().size());
        assertEquals(p, u1.getProduct());
        assertEquals(p, u2.getProduct());
        assertEquals(p, u3.getProduct());
    }

    @Test
    @DisplayName("Should persist units with extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        Unit.saveExtent();
        Unit.clearExtent();
        Unit.loadExtent();
        
        assertEquals(2, Unit.getExtent().size());
        
        Unit loadedU1 = Unit.getExtent().get(0);
        assertEquals("SN001", loadedU1.getSerialNumber());
        assertEquals(LocalDate.of(2024, 1, 15), loadedU1.getManufacturingDate());
    }

    @Test
    @DisplayName("Should track purchased and unpurchased units")
    void testPurchasedStatus() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Purchase purchase = new Purchase(c);
        p.addToCart(purchase, u1);
        
        assertTrue(u1.isPurchased());
        assertFalse(u2.isPurchased());
    }

    @Test
    @DisplayName("Should handle boundary manufacturing dates")
    void testBoundaryManufacturingDates() {
        Product p = new Product("iPhone", "Black", 999.0);
        
        // Minimum valid date
        Unit u1 = new Unit(LocalDate.of(1900, 1, 1), "SN001", p);
        assertEquals(LocalDate.of(1900, 1, 1), u1.getManufacturingDate());
        
        // Today
        Unit u2 = new Unit(LocalDate.now(), "SN002", p);
        assertEquals(LocalDate.now(), u2.getManufacturingDate());
    }
}
