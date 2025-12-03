package com.byt.s30062;

import com.byt.s30062.model.complex.Address;
import com.byt.s30062.model.Store;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @BeforeEach
    void setup() {
        Store.clearExtent();
    }

    @Test
    @DisplayName("Should create store with valid attributes")
    void testValidStore() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        LocalDate dateOfOpening = LocalDate.of(2020, 5, 15);
        Store store = new Store(address, dateOfOpening);
        
        assertEquals(address, store.getAddress());
        assertEquals(dateOfOpening, store.getDateOfOpening());
    }

    @Test
    @DisplayName("Should reject null address")
    void testNullAddress() {
        LocalDate dateOfOpening = LocalDate.of(2020, 5, 15);
        assertThrows(IllegalArgumentException.class,
            () -> new Store(null, dateOfOpening));
    }

    @Test
    @DisplayName("Should reject null dateOfOpening")
    void testNullDateOfOpening() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        assertThrows(IllegalArgumentException.class,
            () -> new Store(address, null));
    }

    @Test
    @DisplayName("Should reject future dateOfOpening")
    void testFutureDateOfOpening() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        assertThrows(IllegalArgumentException.class,
            () -> new Store(address, LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Should reject dateOfOpening before 1900")
    void testDateOfOpeningBeforeLimit() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        assertThrows(IllegalArgumentException.class,
            () -> new Store(address, LocalDate.of(1899, 12, 31)));
    }

    @Test
    @DisplayName("Should store stores in extent")
    void testExtent() {
        assertEquals(0, Store.getExtent().size());
        
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Store store1 = new Store(address1, LocalDate.of(2020, 5, 15));
        assertEquals(1, Store.getExtent().size());
        
        Address address2 = new Address("456 Market St", "San Francisco", "94105", "USA");
        Store store2 = new Store(address2, LocalDate.of(2019, 3, 20));
        assertEquals(2, Store.getExtent().size());
        
        assertTrue(Store.getExtent().contains(store1));
        assertTrue(Store.getExtent().contains(store2));
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        LocalDate dateOfOpening = LocalDate.of(2020, 5, 15);
        
        Store store1 = new Store(address, dateOfOpening);
        Store store2 = new Store(address, dateOfOpening);
        Store store3 = new Store(new Address("456 Market St", "San Francisco", "94105", "USA"), dateOfOpening);
        
        assertEquals(store1, store2);
        assertNotEquals(store1, store3);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        LocalDate dateOfOpening = LocalDate.of(2020, 5, 15);
        
        Store store1 = new Store(address, dateOfOpening);
        Store store2 = new Store(address, dateOfOpening);
        
        assertEquals(store1.hashCode(), store2.hashCode());
    }

    @Test
    @DisplayName("Should generate proper toString")
    void testToString() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        Store store = new Store(address, LocalDate.of(2020, 5, 15));
        
        String result = store.toString();
        assertTrue(result.contains("Store"));
        assertTrue(result.contains("123 Main St"));
        assertTrue(result.contains("2020-05-15"));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        Address address = new Address("123 Main St", "New York", "10001", "USA");
        Store store = new Store(address, LocalDate.of(2020, 5, 15));
        
        var extent1 = Store.getExtent();
        var extent2 = Store.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Store.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Address address1 = new Address("123 Main St", "New York", "10001", "USA");
        Address address2 = new Address("456 Market St", "San Francisco", "94105", "USA");
        
        Store store1 = new Store(address1, LocalDate.of(2020, 5, 15));
        Store store2 = new Store(address2, LocalDate.of(2019, 3, 20));
        
        Store.saveExtent();
        assertTrue(new File("store_extent.ser").exists());
        
        Store.clearExtent();
        assertEquals(0, Store.getExtent().size());
        
        Store.loadExtent();
        assertEquals(2, Store.getExtent().size());
        
        new File("store_extent.ser").delete();
    }
}
