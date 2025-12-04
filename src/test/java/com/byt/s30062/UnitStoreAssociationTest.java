package com.byt.s30062;

import com.byt.s30062.model.Product;
import com.byt.s30062.model.Store;
import com.byt.s30062.model.Unit;
import com.byt.s30062.model.complex.Address;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UnitStoreAssociationTest {

    @BeforeEach
    void setup() {
        Unit.clearExtent();
        Product.clearExtent();
        Store.clearExtent();
    }

    @Test
    @DisplayName("Should assign Unit to Store and maintain forward and reverse links")
    void testUnitAssignToStore() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        
        assertNull(u.getStore());
        assertEquals(0, s.getUnits().size());
        
        // Assign unit to store
        u.setStore(s);
        
        // Forward link verified
        assertEquals(s, u.getStore());
        
        // Reverse link verified
        assertEquals(1, s.getUnits().size());
        assertTrue(s.getUnits().contains(u));
    }

    @Test
    @DisplayName("Should maintain units ordered by serial number in Store")
    void testUnitsOrderedBySerialNumber() {
        Product p = new Product("iPad", "White", 599.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN003", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN001", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN002", p);
        
        Store s = new Store(new Address("456 Market", "SF", "94105", "USA"), LocalDate.of(2020, 1, 1));
        
        // Add units in non-order
        u1.setStore(s);
        u2.setStore(s);
        u3.setStore(s);
        
        // Verify they are sorted by serial number
        var units = s.getUnits();
        assertEquals(3, units.size());
        assertEquals("SN001", units.get(0).getSerialNumber());
        assertEquals("SN002", units.get(1).getSerialNumber());
        assertEquals("SN003", units.get(2).getSerialNumber());
    }

    @Test
    @DisplayName("Should support multiple units in a Store")
    void testMultipleUnitsPerStore() {
        Product p = new Product("MacBook", "Silver", 1999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN003", p);
        
        Store s = new Store(new Address("789 Park", "LA", "90001", "USA"), LocalDate.of(2020, 1, 1));
        
        u1.setStore(s);
        u2.setStore(s);
        u3.setStore(s);
        
        assertEquals(3, s.getUnits().size());
    }

    @Test
    @DisplayName("Should allow Unit to have no store (optional 0..1)")
    void testUnitWithoutStore() {
        Product p = new Product("AirPods", "White", 199.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        assertNull(u.getStore());
    }

    @Test
    @DisplayName("Should clear store when setStore(null) is called")
    void testClearStoreReference() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        
        u.setStore(s);
        assertEquals(s, u.getStore());
        assertEquals(1, s.getUnits().size());
        
        // Clear store
        u.setStore(null);
        
        assertNull(u.getStore());
        assertEquals(0, s.getUnits().size());
    }

    @Test
    @DisplayName("Should remove Unit from Store via unlinkUnit and clear store reference")
    void testUnlinkUnitFromStore() {
        Product p = new Product("iPad", "White", 599.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        Store s = new Store(new Address("456 Market", "SF", "94105", "USA"), LocalDate.of(2020, 1, 1));
        
        u1.setStore(s);
        u2.setStore(s);
        
        assertEquals(2, s.getUnits().size());
        assertEquals(s, u1.getStore());
        
        // Unlink unit via Store
        s.unlinkUnit(u1);
        
        // Removed from store's list
        assertEquals(1, s.getUnits().size());
        assertFalse(s.getUnits().contains(u1));
        assertTrue(s.getUnits().contains(u2));
        
        // Unit's store reference cleared bidirectionally
        assertNull(u1.getStore());
        assertEquals(s, u2.getStore());
    }

    @Test
    @DisplayName("Should handle moving Unit between Stores")
    void testMoveUnitBetweenStores() {
        Product p = new Product("MacBook", "Silver", 1999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Store s1 = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        Store s2 = new Store(new Address("456 Market", "SF", "94105", "USA"), LocalDate.of(2020, 1, 1));
        
        // Assign to first store
        u.setStore(s1);
        assertEquals(s1, u.getStore());
        assertEquals(1, s1.getUnits().size());
        assertEquals(0, s2.getUnits().size());
        
        // Move to second store
        u.setStore(s2);
        assertEquals(s2, u.getStore());
        assertEquals(0, s1.getUnits().size());
        assertEquals(1, s2.getUnits().size());
    }

    @Test
    @DisplayName("Should maintain bidirectional consistency when unlinking")
    void testBidirectionalConsistencyOnUnlink() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN002", p);
        
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        
        u1.setStore(s);
        u2.setStore(s);
        
        // Unlink via Unit
        u1.setStore(null);
        
        // Store's list updated
        assertFalse(s.getUnits().contains(u1));
        assertTrue(s.getUnits().contains(u2));
        
        // Unlink via Store
        s.unlinkUnit(u2);
        
        // Unit's reference cleared
        assertNull(u2.getStore());
        assertEquals(0, s.getUnits().size());
    }

    @Test
    @DisplayName("Should maintain ordering when units are added and removed")
    void testOrderingAfterAddRemove() {
        Product p = new Product("iPad", "White", 599.0);
        Unit u1 = new Unit(LocalDate.of(2024, 1, 15), "SN003", p);
        Unit u2 = new Unit(LocalDate.of(2024, 1, 16), "SN001", p);
        Unit u3 = new Unit(LocalDate.of(2024, 1, 17), "SN002", p);
        
        Store s = new Store(new Address("789 Park", "LA", "90001", "USA"), LocalDate.of(2020, 1, 1));
        
        u1.setStore(s);
        u2.setStore(s);
        u3.setStore(s);
        
        // Remove middle unit
        u2.setStore(null);
        
        // Verify order is maintained
        var units = s.getUnits();
        assertEquals(2, units.size());
        assertEquals("SN002", units.get(0).getSerialNumber());
        assertEquals("SN003", units.get(1).getSerialNumber());
    }

    @Test
    @DisplayName("Should prevent duplicate Units in Store")
    void testNoDuplicateUnitsInStore() {
        Product p = new Product("AirPods", "White", 199.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        
        u.setStore(s);
        assertEquals(1, s.getUnits().size());
        
        // Try to link same unit again (should not create duplicate)
        s.linkUnit(u);
        assertEquals(1, s.getUnits().size());
    }

    @Test
    @DisplayName("Should handle null Unit in linkUnit/unlinkUnit gracefully")
    void testNullUnitHandling() {
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        
        // Should not throw
        s.linkUnit(null);
        s.unlinkUnit(null);
        
        assertEquals(0, s.getUnits().size());
    }

    @Test
    @DisplayName("Should provide defensive copy of units list")
    void testGetUnitsReturnsDefensiveCopy() {
        Product p = new Product("iPhone", "Black", 999.0);
        Unit u = new Unit(LocalDate.of(2024, 1, 15), "SN001", p);
        
        Store s = new Store(new Address("123 Main", "NYC", "10001", "USA"), LocalDate.of(2020, 1, 1));
        u.setStore(s);
        
        var units1 = s.getUnits();
        var units2 = s.getUnits();
        
        // Different list instances
        assertNotSame(units1, units2);
        
        // Same content
        assertEquals(units1, units2);
        
        // Modifying returned list doesn't affect store
        units1.clear();
        assertEquals(1, s.getUnits().size());
    }
}
