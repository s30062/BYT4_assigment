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
    @DisplayName("Should create unit with valid attributes")
    void testValidUnit() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "SN123456789", p);
        
        assertEquals(mfgDate, unit.getManufacturingDate());
        assertEquals("SN123456789", unit.getSerialNumber());
        assertEquals(p, unit.getProduct());
        assertNull(unit.getPurchase());
        assertFalse(unit.isPurchased());
    }

    @Test
    @DisplayName("Should reject null manufacturingDate")
    void testNullManufacturingDate() {
        Product p = new Product("iPhone", "Black", 999.0);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(null, "SN123456789", p));
    }

    @Test
    @DisplayName("Should reject future manufacturingDate")
    void testFutureManufacturingDate() {
        Product p = new Product("iPhone", "Black", 999.0);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(LocalDate.now().plusDays(1), "SN123456789", p));
    }

    @Test
    @DisplayName("Should reject manufacturingDate before 1900")
    void testManufacturingDateBeforeLimit() {
        Product p = new Product("iPhone", "Black", 999.0);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(LocalDate.of(1899, 12, 31), "SN123456789", p));
    }

    @Test
    @DisplayName("Should reject null serialNumber")
    void testNullSerialNumber() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(mfgDate, null, p));
    }

    @Test
    @DisplayName("Should reject blank serialNumber")
    void testBlankSerialNumber() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(mfgDate, "   ", p));
    }

    @Test
    @DisplayName("Should reject serialNumber exceeding 100 characters")
    void testSerialNumberTooLong() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        String longSN = "A".repeat(101);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(mfgDate, longSN, p));
    }

    @Test
    @DisplayName("Should reject null product")
    void testNullProduct() {
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(mfgDate, "SN123456789", null));
    }

    @Test
    @DisplayName("Should trim whitespace from serialNumber")
    void testSerialNumberTrimming() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "  SN123456789  ", p);
        
        assertEquals("SN123456789", unit.getSerialNumber());
    }

    @Test
    @DisplayName("Should set purchase with validation")
    void testSetPurchase() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "SN123456789", p);
        
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Purchase purchase = new Purchase(c, Arrays.asList(unit));
        
        unit.setPurchase(purchase);
        assertEquals(purchase, unit.getPurchase());
        assertTrue(unit.isPurchased());
    }

    @Test
    @DisplayName("Should reject purchase if product not in purchase items")
    void testSetPurchaseWithMissingProduct() {
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "Silver", 599.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "SN123456789", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Purchase purchase = new Purchase(c, Arrays.asList(u2)); // u2, not unit
        
        assertThrows(IllegalArgumentException.class,
            () -> unit.setPurchase(purchase));
    }

    @Test
    @DisplayName("Should allow clearing purchase by setting to null")
    void testClearPurchase() {
        Product p = new Product("iPhone", "Black", 999.0);
        LocalDate mfgDate = LocalDate.of(2024, 1, 15);
        Unit unit = new Unit(mfgDate, "SN123456789", p);
        
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Purchase purchase = new Purchase(c, Arrays.asList(unit));
        
        unit.setPurchase(purchase);
        assertTrue(unit.isPurchased());
        
        unit.setPurchase(null);
        assertNull(unit.getPurchase());
        assertFalse(unit.isPurchased());
    }

    @Test
    @DisplayName("Should store units in extent")
    void testExtent() {
        assertEquals(0, Unit.getExtent().size());
        
        Product p1 = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        assertEquals(1, Unit.getExtent().size());
        
        Product p2 = new Product("iPad", "Silver", 599.0);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        assertEquals(2, Unit.getExtent().size());
        
        assertTrue(Unit.getExtent().contains(u1));
        assertTrue(Unit.getExtent().contains(u2));
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
        Purchase purchase = new Purchase(c, Arrays.asList(u1));
        
        u1.setPurchase(purchase);
        
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
