package com.byt.s30062;

import com.byt.s30062.model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;

import static org.junit.jupiter.api.Assertions.*;

class WarrantyTest {

    @BeforeEach
    void setup() {
        Warranty.clearExtent();
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        Unit.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Warranty.clearExtent();
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        Unit.clearExtent();
        new File("warranty_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create dummy warranty with null endDate via addToCart")
    void testDummyWarrantyCreation() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        p.addToCart(purchase, u);
        
        // Warranty created as dummy (null endDate)
        assertEquals(1, purchase.getWarranties().size());
        Warranty w = purchase.getWarranties().get(0);
        assertNull(w.getEndDate(), "Dummy warranty should have null endDate");
        assertEquals(purchase, w.getPurchase());
        assertEquals(u, w.getUnit());
    }

    @Test
    @DisplayName("Should finalize warranty with endDate on Purchase.finalizePurchase()")
    void testWarrantyFinalization() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        p.addToCart(purchase, u);
        Warranty w = purchase.getWarranties().get(0);
        
        // Before finalization, endDate is null
        assertNull(w.getEndDate());
        
        // Finalize purchase
        purchase.finalizePurchase();
        
        // After finalization, endDate is set
        assertNotNull(w.getEndDate());
        LocalDate expectedEndDate = purchase.getPurchaseDate().toLocalDate().plusYears(Warranty.getMinimumPeriod());
        assertEquals(expectedEndDate, w.getEndDate());
    }

    @Test
    @DisplayName("Should reject setting endDate on finalized warranty")
    void testCannotModifyFinalizedWarrantyEndDate() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        p.addToCart(purchase, u);
        Warranty w = purchase.getWarranties().get(0);
        
        purchase.finalizePurchase();
        
        // Try to modify endDate on finalized warranty
        assertThrows(IllegalStateException.class,
                () -> w.setEndDate(LocalDate.now().plusYears(3)),
                "Cannot modify endDate on finalized warranty");
    }

    @Test
    @DisplayName("Should set valid endDate on dummy warranty")
    void testSetEndDateOnDummyWarranty() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        Warranty w = new Warranty(purchase, u);
        assertNull(w.getEndDate());
        
        // Set valid endDate
        LocalDate customEndDate = LocalDate.now().plusYears(2);
        w.setEndDate(customEndDate);
        
        assertEquals(customEndDate, w.getEndDate());
    }

    @Test
    @DisplayName("Should reject invalid endDate on dummy warranty")
    void testSetInvalidEndDateOnDummyWarranty() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        Warranty w = new Warranty(purchase, u);
        
        // Less than minimum period
        assertThrows(IllegalArgumentException.class,
                () -> w.setEndDate(LocalDate.now().plusMonths(6)));
        
        // More than 10 years
        assertThrows(IllegalArgumentException.class,
                () -> w.setEndDate(LocalDate.now().plusYears(11)));
    }

    @Test
    @DisplayName("Should calculate derived attributes correctly")
    void testDerivedAttributes() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Purchase purchase = new Purchase(c);
        
        p.addToCart(purchase, u);
        Warranty w = purchase.getWarranties().get(0);
        purchase.finalizePurchase();
        
        // getStartDate is derived from purchase date
        assertEquals(purchase.getPurchaseDate().toLocalDate(), w.getStartDate());
        
        // isValid is derived
        assertTrue(w.isValid());
    }
}
