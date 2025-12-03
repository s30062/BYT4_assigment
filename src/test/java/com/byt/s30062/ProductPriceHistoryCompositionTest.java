package com.byt.s30062;

import com.byt.s30062.model.Product;
import com.byt.s30062.model.PriceHistory;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductPriceHistoryCompositionTest {

    @BeforeEach
    void setup() {
        Product.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Product.clearExtent();
        new File("product_extent.ser").delete();
    }

    @Test
    @DisplayName("Should serialize Product with all PriceHistory entries")
    void testSerializationWithPriceHistory() throws IOException, ClassNotFoundException {
        // Create product with initial price
        Product p = new Product("iPhone", "Black", 999.0);
        assertEquals(1, p.getPriceHistory().size());
        
        // Update price multiple times
        p.updatePrice(899.0);
        p.updatePrice(799.0);
        
        assertEquals(3, p.getPriceHistory().size());
        assertEquals(999.0, p.getPriceHistory().get(0));
        assertEquals(899.0, p.getPriceHistory().get(1));
        assertEquals(799.0, p.getPriceHistory().get(2));
        assertEquals(799.0, p.getCurrentPrice());
        
        // Serialize
        Product.saveExtent();
        assertTrue(new File("product_extent.ser").exists());
        
        // Clear and verify empty
        Product.clearExtent();
        assertEquals(0, Product.getExtent().size());
        
        // Load and verify all price history is restored
        Product.loadExtent();
        assertEquals(1, Product.getExtent().size());
        
        Product loadedProduct = Product.getExtent().get(0);
        assertEquals("iPhone", loadedProduct.getName());
        assertEquals(3, loadedProduct.getPriceHistory().size());
        assertEquals(999.0, loadedProduct.getPriceHistory().get(0));
        assertEquals(899.0, loadedProduct.getPriceHistory().get(1));
        assertEquals(799.0, loadedProduct.getPriceHistory().get(2));
        assertEquals(799.0, loadedProduct.getCurrentPrice());
    }

    @Test
    @DisplayName("Should cascade delete all PriceHistory when Product is deleted")
    void testCascadeDeleteOnProductDeletion() {
        // Create product
        Product p = new Product("iPad", "White", 599.0);
        assertEquals(1, p.getPriceHistory().size());
        
        // Update price multiple times
        p.updatePrice(549.0);
        p.updatePrice(499.0);
        assertEquals(3, p.getPriceHistory().size());
        
        // Get reference to price history objects
        int initialHistorySize = p.getPriceHistory().size();
        
        // Verify product is in extent
        assertEquals(1, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(p));
        
        // Delete product
        p.delete();
        
        // Verify product is removed from extent
        assertEquals(0, Product.getExtent().size());
        assertFalse(Product.getExtent().contains(p));
        
        // Verify price history is cleared
        assertEquals(0, p.getPriceHistory().size());
    }

    @Test
    @DisplayName("Should maintain PriceHistory composition through lifecycle")
    void testCompositionLifecycle() {
        // Create product
        Product p1 = new Product("MacBook", "Silver", 1999.0);
        Product p2 = new Product("iPad", "Black", 599.0);
        
        // Both products have initial price history
        assertEquals(1, p1.getPriceHistory().size());
        assertEquals(1, p2.getPriceHistory().size());
        
        // Update prices independently
        p1.updatePrice(1899.0);
        p2.updatePrice(549.0);
        
        assertEquals(2, p1.getPriceHistory().size());
        assertEquals(2, p2.getPriceHistory().size());
        
        // Verify each PriceHistory references correct Product
        for (PriceHistory ph : p1.getPriceHistoryObjects()) {
            assertEquals(p1, ph.getProduct());
        }
        
        for (PriceHistory ph : p2.getPriceHistoryObjects()) {
            assertEquals(p2, ph.getProduct());
        }
        
        // Delete first product
        p1.delete();
        
        // Verify p1 is gone but p2 remains
        assertEquals(1, Product.getExtent().size());
        assertEquals(p2, Product.getExtent().get(0));
        
        // p2 price history should still be intact
        assertEquals(2, p2.getPriceHistory().size());
    }

    @Test
    @DisplayName("Should verify each PriceHistory is owned by exactly one Product")
    void testCompositionOwnership() {
        Product p = new Product("AirPods", "White", 199.0);
        p.updatePrice(179.0);
        p.updatePrice(159.0);
        
        // Each price history should reference this product
        for (PriceHistory ph : p.getPriceHistoryObjects()) {
            assertNotNull(ph.getProduct());
            assertEquals(p, ph.getProduct());
            assertSame(p, ph.getProduct()); // Same instance
        }
    }

    @Test
    @DisplayName("Deleting Product should allow creating new Product with same name")
    void testDeletionAllowsRecreation() {
        // Create product
        Product p1 = new Product("iPhone", "Black", 999.0);
        p1.updatePrice(899.0);
        assertEquals(2, p1.getPriceHistory().size());
        assertEquals(1, Product.getExtent().size());
        
        // Delete
        p1.delete();
        assertEquals(0, Product.getExtent().size());
        assertEquals(0, p1.getPriceHistory().size());
        
        // Create new product with same name (should work)
        Product p2 = new Product("iPhone", "Black", 899.0);
        assertEquals(1, p2.getPriceHistory().size());
        assertEquals(899.0, p2.getCurrentPrice());
        assertEquals(1, Product.getExtent().size());
        assertTrue(Product.getExtent().contains(p2));
    }
}
