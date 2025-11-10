package com.byt.s30062;


import com.byt.s30062.Purchase;
import com.byt.s30062.Product;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceTest {

    @AfterEach
    void cleanup() {
        Product.clearExtent();
        Purchase.clearExtent();
        new File("product_extent.ser").delete();
        new File("purchase_extent.ser").delete();
    }

    @Test
    void testProductExtentSaveLoad() throws Exception {
        Product p = new Product(400, "TestProduct", 10.0);
        Product.saveExtent();
        Product.clearExtent();
        Product.loadExtent();
        assertFalse(Product.getExtent().isEmpty());
    }

    @Test
    void testPurchaseExtentSaveLoad() throws Exception {
        Purchase p = new Purchase(500, 1);
        Purchase.saveExtent();
        Purchase.clearExtent();
        Purchase.loadExtent();
        assertFalse(Purchase.getExtent().isEmpty());
    }
}
