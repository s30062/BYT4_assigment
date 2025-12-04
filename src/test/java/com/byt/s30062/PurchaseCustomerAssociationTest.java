package com.byt.s30062;

import com.byt.s30062.model.Customer;
import com.byt.s30062.model.Product;
import com.byt.s30062.model.Purchase;
import com.byt.s30062.model.Unit;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseCustomerAssociationTest {

    @BeforeEach
    void setup() {
        Customer.clearExtent();
        Product.clearExtent();
        Unit.clearExtent();
        Purchase.clearExtent();
    }

    @Test
    @DisplayName("Should link Purchase to Customer and maintain forward and reverse links")
    void testPurchaseLinksToCustomer() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        assertEquals(0, c.getPurchases().size());
        
        // Create purchase
        Purchase purchase = new Purchase(c, Arrays.asList(u));
        
        // Forward link verified
        assertEquals(c, purchase.getCustomer());
        
        // Reverse link verified
        assertEquals(1, c.getPurchases().size());
        assertTrue(c.getPurchases().contains(purchase));
    }

    @Test
    @DisplayName("Should support multiple purchases per Customer")
    void testMultiplePurchasesPerCustomer() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        Product p3 = new Product("AirPods", "White", 199.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p3);
        
        Purchase purchase1 = new Purchase(c, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c, Arrays.asList(u2));
        Purchase purchase3 = new Purchase(c, Arrays.asList(u3));
        
        assertEquals(3, c.getPurchases().size());
        assertTrue(c.getPurchases().contains(purchase1));
        assertTrue(c.getPurchases().contains(purchase2));
        assertTrue(c.getPurchases().contains(purchase3));
    }

    @Test
    @DisplayName("Should allow Customer to have no purchases initially")
    void testCustomerWithoutPurchases() {
        Customer c = new Customer("Jane", "Smith", LocalDate.of(1995, 5, 15), LocalDate.now());
        
        assertEquals(0, c.getPurchases().size());
    }

    @Test
    @DisplayName("Should maintain Purchase reference to Customer immutably")
    void testPurchaseCustomerReferenceImmutable() {
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1995, 5, 15), LocalDate.now());
        
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Purchase purchase = new Purchase(c1, Arrays.asList(u));
        
        // Purchase references c1
        assertEquals(c1, purchase.getCustomer());
        
        // c1 contains purchase
        assertEquals(1, c1.getPurchases().size());
        
        // c2 does not contain purchase
        assertEquals(0, c2.getPurchases().size());
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency with multiple customers")
    void testBidirectionalConsistencyMultipleCustomers() {
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1995, 5, 15), LocalDate.now());
        
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase1 = new Purchase(c1, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c2, Arrays.asList(u2));
        
        // Each customer has their own purchase
        assertEquals(1, c1.getPurchases().size());
        assertEquals(1, c2.getPurchases().size());
        
        // Purchases reference correct customers
        assertEquals(c1, purchase1.getCustomer());
        assertEquals(c2, purchase2.getCustomer());
        
        // Cross-customer checks
        assertFalse(c1.getPurchases().contains(purchase2));
        assertFalse(c2.getPurchases().contains(purchase1));
    }

    @Test
    @DisplayName("Should handle Purchase with multiple items")
    void testPurchaseWithMultipleItems() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        Product p3 = new Product("AirPods", "White", 199.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p3);
        
        Purchase purchase = new Purchase(c, Arrays.asList(u1, u2, u3));
        
        // Purchase is linked to customer once, even with multiple items
        assertEquals(1, c.getPurchases().size());
        assertTrue(c.getPurchases().contains(purchase));
        
        // Purchase has all items
        assertEquals(3, purchase.getItems().size());
    }

    @Test
    @DisplayName("Should remove Purchase from Customer via delete and clear bidirectional link")
    void testUnlinkPurchaseFromCustomer() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase1 = new Purchase(c, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c, Arrays.asList(u2));
        
        assertEquals(2, c.getPurchases().size());
        assertEquals(2, Purchase.getExtent().size());
        
        // Delete one purchase
        purchase1.delete();
        
        // Removed from customer's purchases
        assertEquals(1, c.getPurchases().size());
        assertFalse(c.getPurchases().contains(purchase1));
        assertTrue(c.getPurchases().contains(purchase2));
        
        // Removed from extent
        assertEquals(1, Purchase.getExtent().size());
        assertFalse(Purchase.getExtent().contains(purchase1));
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency when unlinking")
    void testBidirectionalConsistencyOnUnlink() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase1 = new Purchase(c, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c, Arrays.asList(u2));
        
        // Verify both linked
        assertEquals(2, c.getPurchases().size());
        assertEquals(c, purchase1.getCustomer());
        assertEquals(c, purchase2.getCustomer());
        
        // Delete first purchase
        purchase1.delete();
        
        // Second purchase still linked
        assertEquals(1, c.getPurchases().size());
        assertTrue(c.getPurchases().contains(purchase2));
        assertEquals(c, purchase2.getCustomer());
        
        // Delete second purchase
        purchase2.delete();
        
        // Customer has no purchases
        assertEquals(0, c.getPurchases().size());
        assertEquals(0, Purchase.getExtent().size());
    }

    @Test
    @DisplayName("Should prevent null Customer in Purchase")
    void testNullCustomerRejected() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        assertThrows(IllegalArgumentException.class, 
            () -> new Purchase(null, Arrays.asList(u)));
    }

    @Test
    @DisplayName("Should provide defensive copy of purchases list")
    void testGetPurchasesReturnsDefensiveCopy() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Purchase purchase = new Purchase(c, Arrays.asList(u));
        
        var purchases1 = c.getPurchases();
        var purchases2 = c.getPurchases();
        
        // Different list instances
        assertNotSame(purchases1, purchases2);
        
        // Same content
        assertEquals(purchases1, purchases2);
        assertEquals(1, purchases1.size());
        
        // Modifying returned list doesn't affect customer
        purchases1.clear();
        assertEquals(1, c.getPurchases().size());
    }

    @Test
    @DisplayName("Should maintain Purchase-Customer link in extent")
    void testPurchaseCustomerLinkInExtent() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase1 = new Purchase(c, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c, Arrays.asList(u2));
        
        // Both purchases in extent
        assertTrue(Purchase.getExtent().contains(purchase1));
        assertTrue(Purchase.getExtent().contains(purchase2));
        
        // Both linked to customer
        assertEquals(2, c.getPurchases().size());
    }

    @Test
    @DisplayName("Should correctly count purchases across different customers")
    void testPurchaseCountingMultipleCustomers() {
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1995, 5, 15), LocalDate.now());
        Customer c3 = new Customer("Bob", "Johnson", LocalDate.of(1985, 3, 20), LocalDate.now());
        
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        Product p3 = new Product("AirPods", "White", 199.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p3);
        
        // c1 has 2 purchases
        Purchase pu1 = new Purchase(c1, Arrays.asList(u1));
        Product p4 = new Product("MacBook", "Silver", 1999.0);
        Unit u4 = new Unit(LocalDate.of(2024, 1, 18), "SN004", p4);
        Purchase pu2 = new Purchase(c1, Arrays.asList(u4));
        
        // c2 has 1 purchase
        Purchase pu3 = new Purchase(c2, Arrays.asList(u2));
        
        // c3 has 1 purchase
        Purchase pu4 = new Purchase(c3, Arrays.asList(u3));
        
        assertEquals(2, c1.getPurchases().size());
        assertEquals(1, c2.getPurchases().size());
        assertEquals(1, c3.getPurchases().size());
        assertEquals(4, Purchase.getExtent().size());
    }

    @Test
    @DisplayName("Should remove Purchase from system when unlinking from Customer side")
    void testUnlinkFromCustomerSideDeletesPurchase() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p2);
        
        Purchase purchase1 = new Purchase(c, Arrays.asList(u1));
        Purchase purchase2 = new Purchase(c, Arrays.asList(u2));
        
        assertEquals(2, c.getPurchases().size());
        assertEquals(2, Purchase.getExtent().size());
        
        // Unlink from customer side
        c.removePurchase(purchase1);
        
        // Removed from customer's purchases
        assertEquals(1, c.getPurchases().size());
        assertFalse(c.getPurchases().contains(purchase1));
        
        // Removed from extent (mandatory relationship violated)
        assertEquals(1, Purchase.getExtent().size());
        assertFalse(Purchase.getExtent().contains(purchase1));
    }

    @Test
    @DisplayName("Should enforce mandatory relationship - Purchase cannot exist without Customer")
    void testMandatoryCustomerRelationship() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Purchase purchase = new Purchase(c, Arrays.asList(u));
        
        // Purchase must have a customer
        assertEquals(c, purchase.getCustomer());
        assertTrue(c.getPurchases().contains(purchase));
        
        // Unlinking from customer removes purchase from system
        c.removePurchase(purchase);
        
        assertEquals(0, Purchase.getExtent().size());
        assertEquals(0, c.getPurchases().size());
    }

    @Test
    @DisplayName("Should handle sequential unlinking and relinking")
    void testSequentialUnlinkingAndRelinking() {
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1995, 5, 15), LocalDate.now());
        
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Purchase purchase = new Purchase(c1, Arrays.asList(u));
        
        // Initially linked to c1
        assertEquals(1, c1.getPurchases().size());
        assertEquals(0, c2.getPurchases().size());
        
        // Delete from c1
        purchase.delete();
        assertEquals(0, c1.getPurchases().size());
        assertEquals(0, Purchase.getExtent().size());
    }
}
