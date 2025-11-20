package com.byt.s30062;

import com.byt.s30062.model.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {

    @Test
    @DisplayName("Should create person with valid attributes through concrete subclass")
    void testValidPerson() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Staff staff = new Staff("John", "Doe", birthDate, 50000.0, false);
        
        assertEquals("John", staff.getFirstName());
        assertEquals("Doe", staff.getLastName());
        assertEquals(birthDate, staff.getDateOfBirth());
    }

    @Test
    @DisplayName("Should calculate age correctly (derived attribute)")
    void testDerivedAge() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        Staff staff = new Staff("Jane", "Smith", birthDate, 50000.0, false);
        
        int expectedAge = LocalDate.now().getYear() - 2000;
        assertEquals(expectedAge, staff.getAge());
    }

    @Test
    @DisplayName("Should reject null firstName")
    void testNullFirstName() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff(null, "Doe", LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject blank firstName")
    void testBlankFirstName() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("", "Doe", LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject firstName exceeding 50 characters")
    void testFirstNameTooLong() {
        String longName = "A".repeat(51);
        assertThrows(IllegalArgumentException.class,
            () -> new Staff(longName, "Doe", LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject null lastName")
    void testNullLastName() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", null, LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject blank lastName")
    void testBlankLastName() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", "", LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject lastName exceeding 50 characters")
    void testLastNameTooLong() {
        String longName = "A".repeat(51);
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", longName, LocalDate.of(1990, 1, 1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject null dateOfBirth")
    void testNullDateOfBirth() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", "Doe", null, 50000.0, false));
    }

    @Test
    @DisplayName("Should reject future dateOfBirth")
    void testFutureDateOfBirth() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", "Doe", LocalDate.now().plusDays(1), 50000.0, false));
    }

    @Test
    @DisplayName("Should reject dateOfBirth before 1900")
    void testDateOfBirthBeforeLimit() {
        assertThrows(IllegalArgumentException.class,
            () -> new Staff("John", "Doe", LocalDate.of(1899, 12, 31), 50000.0, false));
    }

    @Test
    @DisplayName("Should trim whitespace from names")
    void testTrimming() {
        Staff staff = new Staff("  John  ", "  Doe  ", LocalDate.of(1990, 1, 1), 50000.0, false);
        
        assertEquals("John", staff.getFirstName());
        assertEquals("Doe", staff.getLastName());
    }

    @Test
    @DisplayName("Should update firstName with validation")
    void testSetFirstName() {
        Staff staff = new Staff("John", "Doe", LocalDate.of(1990, 1, 1), 50000.0, false);
        
        staff.setFirstName("Jane");
        assertEquals("Jane", staff.getFirstName());
        
        assertThrows(IllegalArgumentException.class, () -> staff.setFirstName(null));
        assertThrows(IllegalArgumentException.class, () -> staff.setFirstName(""));
    }

    @Test
    @DisplayName("Should update lastName with validation")
    void testSetLastName() {
        Staff staff = new Staff("John", "Doe", LocalDate.of(1990, 1, 1), 50000.0, false);
        
        staff.setLastName("Smith");
        assertEquals("Smith", staff.getLastName());
        
        assertThrows(IllegalArgumentException.class, () -> staff.setLastName(null));
        assertThrows(IllegalArgumentException.class, () -> staff.setLastName(""));
    }

    @Test
    @DisplayName("Should implement equals and hashCode correctly")
    void testEqualsAndHashCode() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Staff staff1 = new Staff("John", "Doe", birthDate, 50000.0, false);
        Staff staff2 = new Staff("John", "Doe", birthDate, 50000.0, false);
        Staff staff3 = new Staff("John", "Doe", birthDate, 60000.0, false);
        Staff staff4 = new Staff("Jane", "Doe", birthDate, 50000.0, false);
        
        // Same name, birth date, and salary - should be equal
        assertEquals(staff1, staff2);
        assertEquals(staff1.hashCode(), staff2.hashCode());
        
        // Different salary - not equal
        assertNotEquals(staff1, staff3);
        
        // Different name - not equal
        assertNotEquals(staff1, staff4);
    }
}
