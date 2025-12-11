package com.byt.s30062;

import com.byt.s30062.model.Customer;
import com.byt.s30062.model.Product;
import com.byt.s30062.model.Purchase;
import com.byt.s30062.model.Unit;
import com.byt.s30062.model.enums.PurchaseStatus;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {

    @BeforeEach
    void setup() {
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        Unit.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        Unit.clearExtent();
        new File("purchase_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create purchase with valid attributes")
    void testValidPurchase() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("AirPods", "White", 199.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase = new Purchase(c);
        p1.addToCart(purchase, u1);
        p2.addToCart(purchase, u2);
        
        assertEquals(2, purchase.getItems().size());
        assertEquals(PurchaseStatus.Pending, purchase.getStatus());
        assertNotNull(purchase.getPurchaseDate());
    }

    @Test
    @DisplayName("Should calculate total price correctly (derived attribute)")
    void testDerivedTotalPrice() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("AirPods", "White", 199.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase = new Purchase(c);
        p1.addToCart(purchase, u1);
        p2.addToCart(purchase, u2);
        
        assertEquals(1198.0, purchase.getTotalPrice());
        
        // Update price and verify total changes
        p1.updatePrice(899.0);
        assertEquals(1098.0, purchase.getTotalPrice());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        // Null customer
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(null));
        
        // Null status
        Purchase purchase = new Purchase(c);
        assertThrows(IllegalArgumentException.class, 
            () -> purchase.setStatus(null));
        
        // Cannot finalize empty purchase
        assertThrows(IllegalStateException.class,
            () -> purchase.finalizePurchase());
    }

    @Test
    @DisplayName("Should store purchases in extent")
    void testExtent() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        assertEquals(0, Purchase.getExtent().size());
        
        Purchase pu1 = new Purchase(c);
        p.addToCart(pu1, u1);
        assertEquals(1, Purchase.getExtent().size());
        
        Purchase pu2 = new Purchase(c);
        p.addToCart(pu2, u2);
        assertEquals(2, Purchase.getExtent().size());
        
        assertTrue(Purchase.getExtent().contains(pu1));
        assertTrue(Purchase.getExtent().contains(pu2));
    }

    @Test
    @DisplayName("Should maintain encapsulation of items list")
    void testEncapsulation() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Purchase purchase = new Purchase(c);
        p.addToCart(purchase, u);
        
        var items1 = purchase.getItems();
        var items2 = purchase.getItems();
        
        assertNotSame(items1, items2);
        
        // Modifying returned list doesn't affect purchase's items
        items1.clear();
        assertEquals(1, purchase.getItems().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "Silver", 799.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase pu1 = new Purchase(c);
        p1.addToCart(pu1, u1);
        Purchase pu2 = new Purchase(c);
        p2.addToCart(pu2, u2);
        
        Purchase.saveExtent();
        assertTrue(new File("purchase_extent.ser").exists());
        
        Purchase.clearExtent();
        assertEquals(0, Purchase.getExtent().size());
        
        Purchase.loadExtent();
        assertEquals(2, Purchase.getExtent().size());
    }
}
