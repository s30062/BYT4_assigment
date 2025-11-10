package com.byt.s30062;



import com.byt.s30062.Product;
import com.byt.s30062.Purchase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {

    @BeforeEach
    void setup() { Product.clearExtent(); Purchase.clearExtent(); }

    @Test
    void testAddProductsAndTotal() {
        Product p1 = new Product(100, "iPhone", 900.0);
        Product p2 = new Product(101, "AirPods", 200.0);
        Purchase pu = new Purchase(1, 1);
        pu.addProduct(p1);
        pu.addProduct(p2);
        assertEquals(2, pu.getItems().size());
        assertEquals(1100.0, pu.getTotalPrice());
    }

    @Test
    void testStatusChangeValidation() {
        Purchase pu = new Purchase(2, 1);
        assertEquals("Pending", pu.getStatus());
        pu.setStatus("Completed");
        assertEquals("Completed", pu.getStatus());
        assertThrows(IllegalArgumentException.class, () -> pu.setStatus(""));
    }
}
