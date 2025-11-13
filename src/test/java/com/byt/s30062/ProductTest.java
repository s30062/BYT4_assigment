package com.byt.s30062;


import com.byt.s30062.model.Product;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @BeforeEach
    void beforeEach() { Product.clearExtent(); }

    @Test
    void testCreateProductValid() {
        Product p = new Product(1, "iPhone 14", 999.0);
        assertEquals(1, p.getId());
        assertEquals("iPhone 14", p.getName());
        assertEquals(999.0, p.getCurrentPrice());
    }

    @Test
    void testPriceValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Product(2, "Bad", -10.0));
    }

    @Test
    void testPriceUpdateAndHistory() {
        Product p = new Product(3, "iPad", 500.0);
        p.updatePrice(450.0);
        assertEquals(450.0, p.getCurrentPrice());
        assertTrue(p.getPriceHistory().size() >= 2);
    }
}
