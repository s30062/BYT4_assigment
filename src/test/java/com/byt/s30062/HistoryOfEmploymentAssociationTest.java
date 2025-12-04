package com.byt.s30062;

import com.byt.s30062.model.*;
import com.byt.s30062.model.complex.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HistoryOfEmploymentAssociationTest {

    private Manager staff;
    private Store store;

    @BeforeEach
    void setup() {
        HistoryOfEmployment.clearExtent();
        Store.clearExtent();
        Manager.clearExtent();

        staff = new Manager("John", "Doe", LocalDate.of(1985, 3, 15), 5000.0, false);
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        store = new Store(address, LocalDate.of(2020, 5, 15));
    }

    @Test
    @DisplayName("Creating HistoryOfEmployment links it to both Staff and Store")
    void testBidirectionalLinkingOnCreation() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, staff, store);

        // Check staff has this history
        assertTrue(staff.getEmploymentHistory().contains(hoe),
                "Staff should have the employment history in their collection");

        // Check store has this history
        assertTrue(store.getEmploymentHistory().contains(hoe),
                "Store should have the employment history in their collection");

        // Check the history references the correct entities
        assertEquals(staff, hoe.getStaff());
        assertEquals(store, hoe.getStore());
    }

    @Test
    @DisplayName("Staff can have multiple HistoryOfEmployment records at the same store")
    void testStaffMultipleHistories() {
        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate finish1 = LocalDate.of(2021, 5, 31);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(date1, finish1, staff, store);

        LocalDate date2 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(date2, staff, store);

        assertEquals(2, staff.getEmploymentHistory().size(),
                "Staff should have 2 employment history records at the same store");
        assertTrue(staff.getEmploymentHistory().contains(hoe1));
        assertTrue(staff.getEmploymentHistory().contains(hoe2));
    }

    @Test
    @DisplayName("Store can have multiple HistoryOfEmployment records for different staff")
    void testStoreMultipleHistories() {
        Manager staff2 = new Manager("Jane", "Smith", LocalDate.of(1990, 7, 20), 5500.0, false);

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(date1, staff, store);

        LocalDate date2 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(date2, staff2, store);

        assertEquals(2, store.getEmploymentHistory().size(),
                "Store should have 2 employment history records");
        assertTrue(store.getEmploymentHistory().contains(hoe1));
        assertTrue(store.getEmploymentHistory().contains(hoe2));
    }

    @Test
    @DisplayName("Employment history with finish date maintains links")
    void testHistoryWithFinishDateLinks() {
        LocalDate startDate = LocalDate.of(2020, 1, 1);
        LocalDate finishDate = LocalDate.of(2023, 12, 31);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, finishDate, staff, store);

        assertTrue(staff.getEmploymentHistory().contains(hoe),
                "Staff should have the completed employment history");
        assertTrue(store.getEmploymentHistory().contains(hoe),
                "Store should have the completed employment history");
    }

    @Test
    @DisplayName("getEmploymentHistory returns defensive copy for Staff")
    void testStaffGetEmploymentHistoryDefensiveCopy() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, staff, store);

        var history1 = staff.getEmploymentHistory();
        var history2 = staff.getEmploymentHistory();

        assertNotSame(history1, history2,
                "getEmploymentHistory should return different list instances");
        assertEquals(history1, history2,
                "But the defensive copies should be equal");

        // Modifying returned list should not affect staff's internal collection
        history1.clear();
        assertEquals(1, staff.getEmploymentHistory().size(),
                "Staff's actual employment history should not be affected");
    }

    @Test
    @DisplayName("getEmploymentHistory returns defensive copy for Store")
    void testStoreGetEmploymentHistoryDefensiveCopy() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, staff, store);

        var history1 = store.getEmploymentHistory();
        var history2 = store.getEmploymentHistory();

        assertNotSame(history1, history2,
                "getEmploymentHistory should return different list instances");
        assertEquals(history1, history2,
                "But the defensive copies should be equal");

        // Modifying returned list should not affect store's internal collection
        history1.clear();
        assertEquals(1, store.getEmploymentHistory().size(),
                "Store's actual employment history should not be affected");
    }

    @Test
    @DisplayName("Multiple staff members can work at the same store across time")
    void testMultipleStaffSameStore() {
        Manager staff1 = new Manager("John", "Doe", LocalDate.of(1985, 3, 15), 5000.0, false);
        Manager staff2 = new Manager("Jane", "Smith", LocalDate.of(1990, 7, 20), 5500.0, false);

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(date1, staff1, store);

        LocalDate date2 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(date2, staff2, store);

        assertEquals(2, store.getEmploymentHistory().size());
        assertTrue(store.getEmploymentHistory().contains(hoe1));
        assertTrue(store.getEmploymentHistory().contains(hoe2));

        assertEquals(1, staff1.getEmploymentHistory().size());
        assertEquals(1, staff2.getEmploymentHistory().size());
    }

    @Test
    @DisplayName("Bag association: Staff records multiple employment periods at same store")
    void testBagAssociationFromStaffPerspective() {
        LocalDate startDate1 = LocalDate.of(2020, 1, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(startDate1, staff, store);

        LocalDate finishDate1 = LocalDate.of(2021, 5, 31);
        hoe1.setDateOfFinish(finishDate1);

        LocalDate startDate2 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(startDate2, staff, store);

        // Staff has two employment histories with the same store
        assertEquals(2, staff.getEmploymentHistory().size());

        // Both histories are linked correctly to the same store
        assertTrue(staff.getEmploymentHistory().contains(hoe1));
        assertTrue(staff.getEmploymentHistory().contains(hoe2));

        // Store has both histories with this staff
        assertEquals(2, store.getEmploymentHistory().size());
        assertTrue(store.getEmploymentHistory().contains(hoe1));
        assertTrue(store.getEmploymentHistory().contains(hoe2));
    }

    @Test
    @DisplayName("Bag association: Store records history in both directions")
    void testBagAssociationFromStorePerspective() {
        Manager staff1 = new Manager("Employee1", "One", LocalDate.of(1980, 1, 1), 4000.0, false);
        Manager staff2 = new Manager("Employee2", "Two", LocalDate.of(1985, 6, 15), 4500.0, false);

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(date1, staff1, store);

        LocalDate date2 = LocalDate.of(2021, 3, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(date2, staff2, store);

        // Store has two employment histories
        assertEquals(2, store.getEmploymentHistory().size());

        // Both histories are linked to this store
        assertTrue(store.getEmploymentHistory().contains(hoe1));
        assertTrue(store.getEmploymentHistory().contains(hoe2));

        // Each staff member has exactly one history with this store
        assertEquals(1, staff1.getEmploymentHistory().size());
        assertTrue(staff1.getEmploymentHistory().contains(hoe1));

        assertEquals(1, staff2.getEmploymentHistory().size());
        assertTrue(staff2.getEmploymentHistory().contains(hoe2));
    }

    @Test
    @DisplayName("Employment history extent contains all created associations")
    void testHistoryExtentContainsAllAssociations() {
        Manager staff2 = new Manager("Jane", "Smith", LocalDate.of(1990, 7, 20), 5500.0, false);
        Manager staff3 = new Manager("Bob", "Johnson", LocalDate.of(1975, 3, 20), 5800.0, false);
        Store store2 = new Store(new Address("999 Elm St", "Seattle", "98101", "USA"), LocalDate.of(2021, 1, 1));

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(date1, staff, store);

        LocalDate date2 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(date2, staff2, store);

        LocalDate date3 = LocalDate.of(2021, 9, 1);
        HistoryOfEmployment hoe3 = new HistoryOfEmployment(date3, staff3, store2);

        assertEquals(3, HistoryOfEmployment.getExtent().size());
        assertTrue(HistoryOfEmployment.getExtent().contains(hoe1));
        assertTrue(HistoryOfEmployment.getExtent().contains(hoe2));
        assertTrue(HistoryOfEmployment.getExtent().contains(hoe3));
    }

    @Test
    @DisplayName("Complex scenario: Multiple staff at multiple stores")
    void testComplexMultipleStaffMultipleStores() {
        Manager staff1 = new Manager("Manager1", "One", LocalDate.of(1980, 1, 1), 6000.0, false);
        Manager staff2 = new Manager("Manager2", "Two", LocalDate.of(1985, 6, 15), 6500.0, false);
        Store store1 = new Store(new Address("111 1st St", "NYC", "10001", "USA"), LocalDate.of(2018, 1, 1));
        Store store2 = new Store(new Address("222 2nd St", "LA", "90001", "USA"), LocalDate.of(2019, 6, 1));

        // staff1 works at store1 from 2018-2020
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(
                LocalDate.of(2018, 1, 1),
                LocalDate.of(2020, 12, 31),
                staff1, store1);

        // staff1 works at store1 again from 2021-present (same store)
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(
                LocalDate.of(2021, 1, 1),
                staff1, store1);

        // staff2 works at store2 from 2020-present
        HistoryOfEmployment hoe3 = new HistoryOfEmployment(
                LocalDate.of(2020, 1, 1),
                staff2, store2);

        // Verify staff1's history (both at store1)
        assertEquals(2, staff1.getEmploymentHistory().size());
        assertTrue(staff1.getEmploymentHistory().contains(hoe1));
        assertTrue(staff1.getEmploymentHistory().contains(hoe2));

        // Verify staff2's history (at store2)
        assertEquals(1, staff2.getEmploymentHistory().size());
        assertTrue(staff2.getEmploymentHistory().contains(hoe3));

        // Verify store1's history
        assertEquals(2, store1.getEmploymentHistory().size());
        assertTrue(store1.getEmploymentHistory().contains(hoe1));
        assertTrue(store1.getEmploymentHistory().contains(hoe2));

        // Verify store2's history
        assertEquals(1, store2.getEmploymentHistory().size());
        assertTrue(store2.getEmploymentHistory().contains(hoe3));

        // Verify extent
        assertEquals(3, HistoryOfEmployment.getExtent().size());
    }

    @Test
    @DisplayName("Staff cannot work at multiple different stores")
    void testStaffCannotWorkAtMultipleStores() {
        Store store2 = new Store(new Address("555 Somewhere", "Denver", "80202", "USA"), LocalDate.of(2019, 1, 1));

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        new HistoryOfEmployment(date1, staff, store);

        LocalDate date2 = LocalDate.of(2021, 6, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new HistoryOfEmployment(date2, staff, store2),
                "Staff should not be able to work at a different store");
    }

    @Test
    @DisplayName("Staff cannot work at a different store even after finishing employment")
    void testStaffCannotSwitchStoresAfterFinishingEmployment() {
        Store store2 = new Store(new Address("777 Different", "Portland", "97201", "USA"), LocalDate.of(2018, 1, 1));

        LocalDate date1 = LocalDate.of(2020, 1, 1);
        LocalDate finish1 = LocalDate.of(2021, 5, 31);
        new HistoryOfEmployment(date1, finish1, staff, store);

        // Even though employment is finished, staff cannot work at a different store
        LocalDate date2 = LocalDate.of(2022, 1, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new HistoryOfEmployment(date2, staff, store2),
                "Staff cannot work at a different store even after finishing previous employment");
    }
}
