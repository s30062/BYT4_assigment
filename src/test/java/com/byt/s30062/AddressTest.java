package com.byt.s30062;

import com.byt.s30062.model.Address;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    @DisplayName("Should create address with valid attributes")
    void testValidAddress() {
        Address addr = new Address("123 Main St", "Warsaw", "00-001", "Poland");
        
        assertEquals("123 Main St", addr.getStreet());
        assertEquals("Warsaw", addr.getCity());
        assertEquals("00-001", addr.getPostalCode());
        assertEquals("Poland", addr.getCountry());
    }

    @Test
    @DisplayName("Should reject null or blank fields")
    void testValidations() {
        // Null street
        assertThrows(IllegalArgumentException.class,
            () -> new Address(null, "Warsaw", "00-001", "Poland"));
        
        // Blank street
        assertThrows(IllegalArgumentException.class,
            () -> new Address("", "Warsaw", "00-001", "Poland"));
        
        // Null city
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", null, "00-001", "Poland"));
        
        // Blank city
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", "  ", "00-001", "Poland"));
        
        // Null postalCode
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", "Warsaw", null, "Poland"));
        
        // Null country
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", "Warsaw", "00-001", null));
    }

    @Test
    @DisplayName("Should enforce length limits")
    void testLengthLimits() {
        // Street too long (>100)
        String longStreet = "A".repeat(101);
        assertThrows(IllegalArgumentException.class,
            () -> new Address(longStreet, "Warsaw", "00-001", "Poland"));
        
        // City too long (>50)
        String longCity = "A".repeat(51);
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", longCity, "00-001", "Poland"));
        
        // PostalCode too long (>20)
        String longPostal = "A".repeat(21);
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", "Warsaw", longPostal, "Poland"));
        
        // Country too long (>50)
        String longCountry = "A".repeat(51);
        assertThrows(IllegalArgumentException.class,
            () -> new Address("123 Main St", "Warsaw", "00-001", longCountry));
    }

    @Test
    @DisplayName("Should trim whitespace from fields")
    void testTrimming() {
        Address addr = new Address("  123 Main St  ", "  Warsaw  ", "  00-001  ", "  Poland  ");
        
        assertEquals("123 Main St", addr.getStreet());
        assertEquals("Warsaw", addr.getCity());
        assertEquals("00-001", addr.getPostalCode());
        assertEquals("Poland", addr.getCountry());
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        Address addr1 = new Address("123 Main St", "Warsaw", "00-001", "Poland");
        Address addr2 = new Address("123 Main St", "Warsaw", "00-001", "Poland");
        Address addr3 = new Address("456 Oak Ave", "Warsaw", "00-001", "Poland");
        
        assertEquals(addr1, addr2);
        assertEquals(addr1.hashCode(), addr2.hashCode());
        assertNotEquals(addr1, addr3);
    }

    @Test
    @DisplayName("Should generate proper toString representation")
    void testToString() {
        Address addr = new Address("123 Main St", "Warsaw", "00-001", "Poland");
        String result = addr.toString();
        
        assertTrue(result.contains("123 Main St"));
        assertTrue(result.contains("Warsaw"));
        assertTrue(result.contains("00-001"));
        assertTrue(result.contains("Poland"));
    }
}
