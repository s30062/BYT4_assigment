package com.byt.s30062;

import com.byt.s30062.model.Product;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @BeforeEach
    void setup() {
        Product.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Product.clearExtent();
        new File("product_extent.ser").delete();
    }

    // ========== ATTRIBUTE TESTS - Getting Correct Information ==========

    @Test
    @DisplayName("Should create product with valid attributes and retrieve them correctly")
    void testCreateProductValid() {
        Product p = new Product("iPhone 15", "Blue", 999.99);
        
        assertEquals("iPhone 15", p.getName());
        assertEquals(999.99, p.getCurrentPrice());
        assertNotNull(p.getPriceHistory());
        assertEquals(1, p.getPriceHistory().size());
        assertEquals(999.99, p.getPriceHistory().get(0));
    }

    @Test
    @DisplayName("Should retrieve price history correctly (multi-value attribute)")
    void testPriceHistoryMultiValue() {
        Product p = new Product("MacBook", "Silver", 2000.0);
        p.updatePrice(1900.0);
        p.updatePrice(1850.0);
        
        List<Double> history = p.getPriceHistory();
        assertEquals(3, history.size());
        assertEquals(2000.0, history.get(0));
        assertEquals(1900.0, history.get(1));
        assertEquals(1850.0, history.get(2));
    }

    @Test
    @DisplayName("Should calculate current price correctly (derived attribute)")
    void testGetCurrentPriceDerived() {
        Product p = new Product("iPad", "White", 500.0);
        assertEquals(500.0, p.getCurrentPrice());
        
        p.updatePrice(450.0);
        assertEquals(450.0, p.getCurrentPrice());
        
        p.updatePrice(480.0);
        assertEquals(480.0, p.getCurrentPrice());
    }

    // ========== VALIDATION TESTS - Exception Throwing ==========

    @Test
    @DisplayName("Should throw exception when name is null")
    void testNameNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product(null, "Red", 100.0));
        assertEquals("name cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when name is blank")
    void testNameBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("   ", "Red", 100.0));
        assertEquals("name cannot be empty or blank", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when name exceeds 100 characters")
    void testNameTooLong() {
        String longName = "A".repeat(101);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product(longName, "Red", 100.0));
        assertEquals("name cannot exceed 100 characters", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when color is null")
    void testColorNull() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", null, 100.0));
        assertEquals("color cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when color is blank")
    void testColorBlank() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", "", 100.0));
        assertEquals("color cannot be empty or blank", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when initial price is zero")
    void testPriceZero() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", "Black", 0.0));
        assertEquals("initial price must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when initial price is negative")
    void testPriceNegative() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", "Black", -50.0));
        assertEquals("initial price must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when initial price is NaN")
    void testPriceNaN() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", "Black", Double.NaN));
        assertEquals("initial price cannot be NaN", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when initial price is Infinity")
    void testPriceInfinity() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new Product("iPhone", "Black", Double.POSITIVE_INFINITY));
        assertEquals("initial price cannot be infinite", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating price to zero")
    void testUpdatePriceZero() {
        Product p = new Product("iPhone", "Black", 100.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> p.updatePrice(0.0));
        assertEquals("price must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating price to negative")
    void testUpdatePriceNegative() {
        Product p = new Product("iPhone", "Black", 100.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> p.updatePrice(-10.0));
        assertEquals("price must be positive", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating price to NaN")
    void testUpdatePriceNaN() {
        Product p = new Product("iPhone", "Black", 100.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> p.updatePrice(Double.NaN));
        assertEquals("price cannot be NaN", ex.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating price to Infinity")
    void testUpdatePriceInfinity() {
        Product p = new Product("iPhone", "Black", 100.0);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> p.updatePrice(Double.POSITIVE_INFINITY));
        assertEquals("price cannot be infinite", ex.getMessage());
    }

    @Test
    @DisplayName("Should trim whitespace from name and color")
    void testTrimming() {
        Product p = new Product("  iPhone  ", "  Black  ", 100.0);
        assertEquals("iPhone", p.getName());
    }

    // ========== CLASS EXTENT TESTS ==========

    @Test
    @DisplayName("Should add products to extent correctly")
    void testExtentStorage() {
        assertEquals(0, Product.getExtent().size());
        
        Product p1 = new Product("iPhone", "Black", 999.0);
        assertEquals(1, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(p1));
        
        Product p2 = new Product("iPad", "White", 599.0);
        assertEquals(2, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(p1));
        assertTrue(Product.getExtent().contains(p2));
        
        Product p3 = new Product("MacBook", "Silver", 1999.0);
        assertEquals(3, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(p1));
        assertTrue(Product.getExtent().contains(p2));
        assertTrue(Product.getExtent().contains(p3));
    }

    @Test
    @DisplayName("Should store only Product instances in extent")
    void testExtentStoresCorrectClasses() {
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        List<Product> extent = Product.getExtent();
        assertEquals(2, extent.size());
        
        for (Object obj : extent) {
            assertTrue(obj instanceof Product);
        }
    }

    // ========== ENCAPSULATION TESTS ==========

    @Test
    @DisplayName("Should return defensive copy of extent (encapsulation)")
    void testExtentEncapsulation() {
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        List<Product> extent1 = Product.getExtent();
        List<Product> extent2 = Product.getExtent();
        
        // Should return different list instances
        assertNotSame(extent1, extent2);
        
        // Modifying returned list should not affect extent
        extent1.clear();
        assertEquals(2, Product.getExtent().size());
        
        // Adding to returned list should not affect extent
        Product p3 = new Product("MacBook", "Silver", 1999.0);
        extent2.add(p3); // extent2 has 2 elements (p1, p2), we add p3
        assertEquals(3, extent2.size()); // extent2 now has 3
        assertEquals(3, Product.getExtent().size()); // actual extent has 3 (p1, p2, p3 from constructor)
    }

    @Test
    @DisplayName("Should return defensive copy of price history (encapsulation)")
    void testPriceHistoryEncapsulation() {
        Product p = new Product("iPhone", "Black", 999.0);
        p.updatePrice(899.0);
        
        List<Double> history1 = p.getPriceHistory();
        List<Double> history2 = p.getPriceHistory();
        
        // Should return different list instances
        assertNotSame(history1, history2);
        
        // Modifying returned list should not affect internal state
        history1.clear();
        assertEquals(2, p.getPriceHistory().size());
        
        history2.add(799.0);
        assertEquals(2, p.getPriceHistory().size());
    }

    @Test
    @DisplayName("Modifying product attributes should not affect extent references")
    void testExtentReferenceIntegrity() {
        Product p = new Product("iPhone", "Black", 999.0);
        
        List<Product> extent = Product.getExtent();
        Product pFromExtent = extent.get(0);
        
        // Original product and extent reference should be same object
        assertSame(p, pFromExtent);
        
        // Updating price should affect both references
        p.updatePrice(899.0);
        assertEquals(899.0, pFromExtent.getCurrentPrice());
    }

    // ========== EXTENT PERSISTENCE TESTS ==========

    @Test
    @DisplayName("Should save and load extent correctly")
    void testExtentPersistence() throws IOException, ClassNotFoundException {
        // Create products
        Product p1 = new Product("iPhone 15", "Blue", 999.0);
        Product p2 = new Product("MacBook Pro", "Silver", 2499.0);
        Product p3 = new Product("AirPods", "White", 249.0);
        
        assertEquals(3, Product.getExtent().size());
        
        // Save extent
        Product.saveExtent();
        
        // Verify file exists
        File extentFile = new File("product_extent.ser");
        assertTrue(extentFile.exists());
        assertTrue(extentFile.length() > 0);
        
        // Clear extent (simulate program restart)
        Product.clearExtent();
        assertEquals(0, Product.getExtent().size());
        
        // Load extent
        Product.loadExtent();
        
        // Verify products restored
        assertEquals(3, Product.getExtent().size());
        
        List<Product> loadedExtent = Product.getExtent();
        assertTrue(loadedExtent.stream().anyMatch(p -> p.getName().equals("iPhone 15")));
        assertTrue(loadedExtent.stream().anyMatch(p -> p.getName().equals("MacBook Pro")));
        assertTrue(loadedExtent.stream().anyMatch(p -> p.getName().equals("AirPods")));
    }

    @Test
    @DisplayName("Should persist product attributes correctly")
    void testAttributePersistence() throws IOException, ClassNotFoundException {
        Product original = new Product("iPhone", "Black", 999.0);
        original.updatePrice(899.0);
        original.updatePrice(849.0);
        
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        
        Product loaded = Product.getExtent().get(0);
        assertEquals("iPhone", loaded.getName());
        assertEquals(849.0, loaded.getCurrentPrice());
        assertEquals(3, loaded.getPriceHistory().size());
        assertEquals(999.0, loaded.getPriceHistory().get(0));
        assertEquals(899.0, loaded.getPriceHistory().get(1));
        assertEquals(849.0, loaded.getPriceHistory().get(2));
    }

    @Test
    @DisplayName("Should handle empty extent persistence")
    void testEmptyExtentPersistence() throws IOException, ClassNotFoundException {
        Product.saveExtent();
        
        assertTrue(new File("product_extent.ser").exists());
        
        Product.loadExtent();
        assertEquals(0, Product.getExtent().size());
    }

    @Test
    @DisplayName("Should handle multiple save-load cycles")
    void testMultiplePersistenceCycles() throws IOException, ClassNotFoundException {
        // First cycle
        new Product("Product1", "Red", 100.0);
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        assertEquals(1, Product.getExtent().size());
        
        // Second cycle - add more products
        new Product("Product2", "Blue", 200.0);
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        assertEquals(2, Product.getExtent().size());
        
        // Third cycle - add even more
        new Product("Product3", "Green", 300.0);
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        assertEquals(3, Product.getExtent().size());
    }

    // ========== EDGE CASES ==========

    @Test
    @DisplayName("Should handle product with maximum valid name length")
    void testMaxNameLength() {
        String maxName = "A".repeat(100);
        Product p = new Product(maxName, "Black", 100.0);
        assertEquals(100, p.getName().length());
    }

    @Test
    @DisplayName("Should handle very small positive price")
    void testVerySmallPrice() {
        Product p = new Product("iPhone", "Black", 0.01);
        assertEquals(0.01, p.getCurrentPrice());
    }

    @Test
    @DisplayName("Should handle very large price")
    void testVeryLargePrice() {
        Product p = new Product("iPhone", "Black", 999999999.99);
        assertEquals(999999999.99, p.getCurrentPrice());
    }

    @Test
    @DisplayName("Should handle multiple price updates")
    void testMultiplePriceUpdates() {
        Product p = new Product("iPhone", "Black", 1000.0);
        
        for (int i = 1; i <= 100; i++) {
            p.updatePrice(1000.0 - i);
        }
        
        assertEquals(101, p.getPriceHistory().size());
        assertEquals(900.0, p.getCurrentPrice());
    }
}
