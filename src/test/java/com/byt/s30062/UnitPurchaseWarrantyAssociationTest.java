package com.byt.s30062;

import com.byt.s30062.model.*;
import com.byt.s30062.model.enums.PurchaseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UnitPurchaseWarrantyAssociationTest {

    private Customer customer;
    private Product product1;
    private Product product2;
    private Unit unit1;
    private Unit unit2;
    private Purchase purchase;

    @BeforeEach
    void setup() {
        Warranty.clearExtent();
        Purchase.clearExtent();
        Product.clearExtent();
        Customer.clearExtent();
        Unit.clearExtent();
        
        customer = new Customer("Alice", "Buyer", LocalDate.of(1990, 1, 1), LocalDate.now());
        product1 = new Product("iPhone", "Black", 999.0);
        product2 = new Product("AirPods", "White", 199.0);
        unit1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", product1);
        unit2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", product2);
        purchase = new Purchase(customer);
    }

    @Test
    @DisplayName("Should add unit to purchase via Product.addToCart creating dummy warranty")
    void testAddUnitToCart() {
        product1.addToCart(purchase, unit1);
        
        // Warranty created as dummy
        assertEquals(1, purchase.getWarranties().size());
        Warranty w = purchase.getWarranties().get(0);
        assertNull(w.getEndDate());
        assertEquals(purchase, w.getPurchase());
        assertEquals(unit1, w.getUnit());
        
        // Unit linked through warranty
        assertEquals(1, unit1.getWarranties().size());
        assertTrue(unit1.getWarranties().contains(w));
    }

    @Test
    @DisplayName("Should add multiple units to same purchase")
    void testAddMultipleUnitsToCart() {
        product1.addToCart(purchase, unit1);
        product2.addToCart(purchase, unit2);
        
        assertEquals(2, purchase.getWarranties().size());
        assertEquals(2, purchase.getItems().size());
        assertTrue(purchase.getItems().contains(unit1));
        assertTrue(purchase.getItems().contains(unit2));
    }

    @Test
    @DisplayName("Should prevent unit from being in multiple purchases")
    void testUnitCannotBeInMultiplePurchases() {
        product1.addToCart(purchase, unit1);
        
        Purchase purchase2 = new Purchase(customer);
        assertThrows(IllegalArgumentException.class,
                () -> product1.addToCart(purchase2, unit1),
                "Unit can only be in one purchase");
    }

    @Test
    @DisplayName("Should finalize purchase and set warranty endDates")
    void testFinalizePurchase() {
        product1.addToCart(purchase, unit1);
        product2.addToCart(purchase, unit2);
        
        // Before finalization
        assertEquals(PurchaseStatus.Pending, purchase.getStatus());
        assertNull(purchase.getWarranties().get(0).getEndDate());
        
        // Finalize
        purchase.finalizePurchase();
        
        // After finalization
        assertEquals(PurchaseStatus.Preparing, purchase.getStatus());
        for (Warranty w : purchase.getWarranties()) {
            assertNotNull(w.getEndDate());
            LocalDate expected = purchase.getPurchaseDate().toLocalDate().plusYears(1);
            assertEquals(expected, w.getEndDate());
        }
    }

    @Test
    @DisplayName("Should calculate total price from warranties")
    void testTotalPriceCalculation() {
        product1.addToCart(purchase, unit1);
        product2.addToCart(purchase, unit2);
        
        assertEquals(1198.0, purchase.getTotalPrice());
        
        // Update price and verify
        product1.updatePrice(899.0);
        assertEquals(1098.0, purchase.getTotalPrice());
    }

    @Test
    @DisplayName("Should get items through warranties (derived)")
    void testGetItemsFromWarranties() {
        product1.addToCart(purchase, unit1);
        product2.addToCart(purchase, unit2);
        
        var items = purchase.getItems();
        assertEquals(2, items.size());
        assertTrue(items.contains(unit1));
        assertTrue(items.contains(unit2));
        
        // Items are derived from warranties
        assertFalse(items.isEmpty());
    }

    @Test
    @DisplayName("Should remove unit from purchase via Product.removeFromCart")
    void testRemoveFromCart() {
        product1.addToCart(purchase, unit1);
        product2.addToCart(purchase, unit2);
        
        assertEquals(2, purchase.getWarranties().size());
        
        product1.removeFromCart(purchase, unit1);
        
        assertEquals(1, purchase.getWarranties().size());
        assertFalse(purchase.getItems().contains(unit1));
        assertTrue(purchase.getItems().contains(unit2));
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency through warranties")
    void testBidirectionalConsistency() {
        product1.addToCart(purchase, unit1);
        
        Warranty w = purchase.getWarranties().get(0);
        
        // From purchase side
        assertTrue(purchase.getWarranties().contains(w));
        
        // From unit side
        assertTrue(unit1.getWarranties().contains(w));
        
        // Purchase can be derived from unit
        assertEquals(purchase, unit1.getPurchase());
    }

    @Test
    @DisplayName("Should enforce that purchase belongs to customer")
    void testPurchaseBelongsToCustomer() {
        product1.addToCart(purchase, unit1);
        purchase.finalizePurchase();
        
        assertEquals(customer, purchase.getCustomer());
        assertTrue(customer.getPurchases().contains(purchase));
    }

    @Test
    @DisplayName("Should prevent finalizing empty purchase")
    void testCannotFinalizeEmptyPurchase() {
        assertThrows(IllegalStateException.class,
                () -> purchase.finalizePurchase(),
                "Cannot finalize purchase with no items");
    }

    @Test
    @DisplayName("Should maintain warranty state through finalization")
    void testWarrantyStateTransition() {
        product1.addToCart(purchase, unit1);
        Warranty w = purchase.getWarranties().get(0);
        
        // Dummy state
        assertNull(w.getEndDate());
        assertTrue(w.getUnit().getWarranties().contains(w));
        
        // Finalize
        purchase.finalizePurchase();
        
        // Finalized state
        assertNotNull(w.getEndDate());
        assertTrue(w.getUnit().getWarranties().contains(w));
        assertEquals(purchase, w.getPurchase());
    }

    @Test
    @DisplayName("Should correctly link unit through warranty to purchase")
    void testUnitToPurchaseLinkingPath() {
        product1.addToCart(purchase, unit1);
        
        // Path: unit -> warranty -> purchase
        assertEquals(1, unit1.getWarranties().size());
        Warranty w = unit1.getWarranties().get(0);
        assertEquals(purchase, w.getPurchase());
        assertEquals(unit1, w.getUnit());
    }

    @Test
    @DisplayName("Should support multiple units from same product in one purchase")
    void testMultipleUnitsFromSameProduct() {
        Unit unit3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", product1);
        
        product1.addToCart(purchase, unit1);
        product1.addToCart(purchase, unit3);
        
        assertEquals(2, purchase.getWarranties().size());
        assertEquals(2, purchase.getItems().size());
        
        // Both units are in the same purchase
        assertTrue(purchase.getItems().contains(unit1));
        assertTrue(purchase.getItems().contains(unit3));
    }
}
