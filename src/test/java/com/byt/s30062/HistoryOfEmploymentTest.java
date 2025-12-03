package com.byt.s30062;

import com.byt.s30062.model.*;
import com.byt.s30062.model.complex.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class HistoryOfEmploymentTest {

    private Manager person;
    private Store store;

    @BeforeEach
    void setup() {
        HistoryOfEmployment.clearExtent();
        Store.clearExtent();
        Product.clearExtent();
        Manager.clearExtent();
        
        person = new Manager("John", "Doe", LocalDate.of(1985, 3, 15), 5000.0, false);
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        store = new Store(address, LocalDate.of(2020, 5, 15));
    }

    @Test
    @DisplayName("Should create employment history with start date and no finish date")
    void testValidEmploymentHistoryWithoutFinish() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, person, store);
        
        assertEquals(startDate, hoe.getDateOfStart());
        assertNull(hoe.getDateOfFinish());
        assertEquals(person, hoe.getPerson());
        assertEquals(store, hoe.getStore());
        assertTrue(hoe.isActive());
    }

    @Test
    @DisplayName("Should create employment history with both start and finish dates")
    void testValidEmploymentHistoryWithFinish() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate finishDate = LocalDate.of(2023, 3, 31);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, finishDate, person, store);
        
        assertEquals(startDate, hoe.getDateOfStart());
        assertEquals(finishDate, hoe.getDateOfFinish());
        assertEquals(person, hoe.getPerson());
        assertEquals(store, hoe.getStore());
        assertFalse(hoe.isActive());
    }

    @Test
    @DisplayName("Should reject null dateOfStart")
    void testNullDateOfStart() {
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(null, person, store));
    }

    @Test
    @DisplayName("Should reject future dateOfStart")
    void testFutureDateOfStart() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(futureDate, person, store));
    }

    @Test
    @DisplayName("Should reject dateOfStart before 1900")
    void testDateOfStartBeforeLimit() {
        LocalDate oldDate = LocalDate.of(1899, 12, 31);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(oldDate, person, store));
    }

    @Test
    @DisplayName("Should reject dateOfFinish before dateOfStart")
    void testFinishBeforeStart() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate finishDate = LocalDate.of(2021, 5, 31);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(startDate, finishDate, person, store));
    }

    @Test
    @DisplayName("Should reject future dateOfFinish")
    void testFutureDateOfFinish() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate futureFinish = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(startDate, futureFinish, person, store));
    }

    @Test
    @DisplayName("Should reject null person")
    void testNullPerson() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(startDate, null, store));
    }

    @Test
    @DisplayName("Should reject null store")
    void testNullStore() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new HistoryOfEmployment(startDate, person, null));
    }

    @Test
    @DisplayName("Should allow setting dateOfFinish on active employment")
    void testSetDateOfFinish() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, person, store);
        
        assertTrue(hoe.isActive());
        
        LocalDate finishDate = LocalDate.of(2023, 3, 31);
        hoe.setDateOfFinish(finishDate);
        
        assertEquals(finishDate, hoe.getDateOfFinish());
        assertFalse(hoe.isActive());
    }

    @Test
    @DisplayName("Should reject invalid dateOfFinish in setter")
    void testSetInvalidDateOfFinish() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, person, store);
        
        LocalDate beforeStart = LocalDate.of(2021, 5, 31);
        assertThrows(IllegalArgumentException.class,
            () -> hoe.setDateOfFinish(beforeStart));
    }

    @Test
    @DisplayName("Should store employment histories in extent")
    void testExtent() {
        assertEquals(0, HistoryOfEmployment.getExtent().size());
        
        LocalDate startDate1 = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(startDate1, person, store);
        assertEquals(1, HistoryOfEmployment.getExtent().size());
        
        Manager person2 = new Manager("Jane", "Smith", LocalDate.of(1990, 7, 20), 5500.0, false);
        LocalDate startDate2 = LocalDate.of(2022, 1, 15);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(startDate2, person2, store);
        assertEquals(2, HistoryOfEmployment.getExtent().size());
        
        assertTrue(HistoryOfEmployment.getExtent().contains(hoe1));
        assertTrue(HistoryOfEmployment.getExtent().contains(hoe2));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate finishDate = LocalDate.of(2023, 3, 31);
        
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(startDate, finishDate, person, store);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(startDate, finishDate, person, store);
        HistoryOfEmployment hoe3 = new HistoryOfEmployment(startDate, person, store); // No finish
        
        assertEquals(hoe1, hoe2);
        assertNotEquals(hoe1, hoe3);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        LocalDate finishDate = LocalDate.of(2023, 3, 31);
        
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(startDate, finishDate, person, store);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(startDate, finishDate, person, store);
        
        assertEquals(hoe1.hashCode(), hoe2.hashCode());
    }

    @Test
    @DisplayName("Should generate proper toString")
    void testToString() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        HistoryOfEmployment hoe = new HistoryOfEmployment(startDate, person, store);
        
        String result = hoe.toString();
        assertTrue(result.contains("HistoryOfEmployment"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("New York"));
        assertTrue(result.contains("true")); // isActive
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        LocalDate startDate = LocalDate.of(2021, 6, 1);
        new HistoryOfEmployment(startDate, person, store);
        
        var extent1 = HistoryOfEmployment.getExtent();
        var extent2 = HistoryOfEmployment.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, HistoryOfEmployment.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        LocalDate startDate1 = LocalDate.of(2021, 6, 1);
        LocalDate finishDate1 = LocalDate.of(2023, 3, 31);
        HistoryOfEmployment hoe1 = new HistoryOfEmployment(startDate1, finishDate1, person, store);
        
        Manager person2 = new Manager("Jane", "Smith", LocalDate.of(1990, 7, 20), 5500.0, false);
        LocalDate startDate2 = LocalDate.of(2022, 1, 15);
        HistoryOfEmployment hoe2 = new HistoryOfEmployment(startDate2, person2, store);
        
        HistoryOfEmployment.saveExtent();
        assertTrue(new File("history_of_employment_extent.ser").exists());
        
        HistoryOfEmployment.clearExtent();
        assertEquals(0, HistoryOfEmployment.getExtent().size());
        
        HistoryOfEmployment.loadExtent();
        assertEquals(2, HistoryOfEmployment.getExtent().size());
        
        new File("history_of_employment_extent.ser").delete();
    }
}
