package com.byt.s30062;

import com.byt.s30062.model.Product;
import com.byt.s30062.model.Unit;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProductUnitAssociationTest {

    @BeforeEach
    void setup() {
        Product.clearExtent();
        Unit.clearExtent();
    }

    @Test
    @DisplayName("Should automatically link Unit to Product on creation")
    void testUnitCreationLinksToProduct() {
        Product p = new Product("iPhone", "Black", 999.0);
        assertEquals(0, p.getUnits().size());
        
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        // Forward link verified
        assertEquals(p, u.getProduct());
        
        // Reverse link verified (Product now tracks Unit)
        assertEquals(1, p.getUnits().size());
        assertTrue(p.getUnits().contains(u));
    }

    @Test
    @DisplayName("Should support multiple Units for same Product")
    void testMultipleUnitsPerProduct() {
        Product p = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p);
        
        assertEquals(3, p.getUnits().size());
        assertTrue(p.getUnits().contains(u1));
        assertTrue(p.getUnits().contains(u2));
        assertTrue(p.getUnits().contains(u3));
    }

    @Test
    @DisplayName("Should reject null Product when creating Unit")
    void testUnitWithNullProductThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> new Unit(LocalDate.of(2024, 1, 15), "SN001", null));
    }

    @Test
    @DisplayName("Should maintain reference integrity: each Unit knows its Product")
    void testUnitProductReferenceImmutable() {
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        
        // Unit's Product reference cannot change (it's final)
        assertEquals(p1, u.getProduct());
        assertNotEquals(p2, u.getProduct());
    }

    @Test
    @DisplayName("Should not add duplicate Units to Product")
    void testNoDuplicateUnitsInProduct() {
        Product p = new Product("MacBook", "Silver", 1999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        // Initially 1 unit
        assertEquals(1, p.getUnits().size());
        
        // Trying to add same unit again should not create duplicate
        p.linkUnit(u);
        assertEquals(1, p.getUnits().size());
    }

    @Test
    @DisplayName("Should delete Unit from system when unlinked from Product")
    void testRemoveUnitFromProduct() {
        Product p = new Product("AirPods", "White", 199.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        assertEquals(2, p.getUnits().size());
        assertEquals(2, Unit.getExtent().size());
        
        // Unlink unit (should also delete from extent since Unit must have Product)
        p.unlinkUnit(u1);
        
        // Removed from Product's unit list
        assertEquals(1, p.getUnits().size());
        assertFalse(p.getUnits().contains(u1));
        assertTrue(p.getUnits().contains(u2));
        
        // Deleted from system extent
        assertEquals(1, Unit.getExtent().size());
        assertFalse(Unit.getExtent().contains(u1));
        assertTrue(Unit.getExtent().contains(u2));
    }

    @Test
    @DisplayName("Should handle removing non-existent Unit gracefully")
    void testRemoveNonExistentUnitDoesNotThrow() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", new Product("iPad", "White", 599.0));
        
        assertEquals(1, p.getUnits().size());
        
        // Should not throw or modify
        p.unlinkUnit(u2);
        assertEquals(1, p.getUnits().size());
    }

    @Test
    @DisplayName("Should handle null Unit in linkUnit/unlinkUnit")
    void testNullUnitHandling() {
        Product p = new Product("iPhone", "Black", 999.0);
        
        // Should not throw
        p.linkUnit(null);
        p.unlinkUnit(null);
        
        assertEquals(0, p.getUnits().size());
    }

    @Test
    @DisplayName("Should maintain separate Unit lists for different Products")
    void testIndependentUnitListsPerProduct() {
        Product p1 = new Product("iPhone", "Black", 999.0);
        Product p2 = new Product("iPad", "White", 599.0);
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p1);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p1);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p2);
        
        assertEquals(2, p1.getUnits().size());
        assertEquals(1, p2.getUnits().size());
        
        assertTrue(p1.getUnits().contains(u1));
        assertTrue(p1.getUnits().contains(u2));
        assertFalse(p1.getUnits().contains(u3));
        
        assertTrue(p2.getUnits().contains(u3));
        assertFalse(p2.getUnits().contains(u1));
    }

    @Test
    @DisplayName("Should provide defensive copy of Units list")
    void testGetUnitsReturnsDefensiveCopy() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        var units1 = p.getUnits();
        var units2 = p.getUnits();
        
        // Different list instances
        assertNotSame(units1, units2);
        
        // But same content
        assertEquals(units1, units2);
        
        // Modifying returned list doesn't affect Product's units
        units1.clear();
        assertEquals(1, p.getUnits().size());
    }

    @Test
    @DisplayName("Should verify bidirectional consistency: Unit.product == Product from p.getUnits()")
    void testBidirectionalConsistency() {
        Product p = new Product("MacBook", "Silver", 1999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        // All units in Product's list should reference the same Product
        for (Unit u : p.getUnits()) {
            assertEquals(p, u.getProduct());
        }
    }

    @Test
    @DisplayName("Should track Unit count and deletion accuracy after additions and removals")
    void testUnitCountAccuracy() {
        Product p = new Product("iPad", "Black", 599.0);
        assertEquals(0, p.getUnits().size());
        assertEquals(0, Unit.getExtent().size());
        
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        assertEquals(1, p.getUnits().size());
        assertEquals(1, Unit.getExtent().size());
        
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        assertEquals(2, p.getUnits().size());
        assertEquals(2, Unit.getExtent().size());
        
        // Unlinking also deletes from extent
        p.unlinkUnit(u1);
        assertEquals(1, p.getUnits().size());
        assertEquals(1, Unit.getExtent().size());
        
        p.unlinkUnit(u2);
        assertEquals(0, p.getUnits().size());
        assertEquals(0, Unit.getExtent().size());
    }
}
