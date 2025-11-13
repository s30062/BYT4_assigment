package com.byt.s30062;

import com.byt.s30062.model.Customer;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @BeforeEach
    void setup() {
        Customer.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Customer.clearExtent();
        new File("customer_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create customer with valid attributes")
    void testValidCustomer() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        LocalDate regDate = LocalDate.of(2020, 1, 1);
        Customer c = new Customer("John", "Doe", birthDate, regDate);
        
        assertEquals("John", c.getFirstName());
        assertEquals("Doe", c.getLastName());
        assertEquals(birthDate, c.getBirthDate());
        assertEquals(regDate, c.getRegistrationDate());
    }

    @Test
    @DisplayName("Should calculate age correctly (derived attribute)")
    void testDerivedAge() {
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        Customer c = new Customer("Jane", "Smith", birthDate, LocalDate.now());
        
        int expectedAge = LocalDate.now().getYear() - 2000;
        assertEquals(expectedAge, c.getAge());
    }

    @Test
    @DisplayName("Should reject invalid inputs")
    void testValidations() {
        LocalDate validBirth = LocalDate.of(1990, 1, 1);
        LocalDate validReg = LocalDate.now();
        
        // Null/blank names
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer(null, "Doe", validBirth, validReg));
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer("", "Doe", validBirth, validReg));
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer("John", null, validBirth, validReg));
        
        // Invalid dates
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer("John", "Doe", LocalDate.now().plusDays(1), validReg));
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer("John", "Doe", validBirth, validBirth.minusDays(1)));
        
        // Too young
        assertThrows(IllegalArgumentException.class, 
            () -> new Customer("Kid", "Young", LocalDate.now().minusYears(5), validReg));
    }

    @Test
    @DisplayName("Should store customers in extent")
    void testExtent() {
        assertEquals(0, Customer.getExtent().size());
        
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        assertEquals(1, Customer.getExtent().size());
        
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1985, 5, 10), LocalDate.now());
        assertEquals(2, Customer.getExtent().size());
        
        assertTrue(Customer.getExtent().contains(c1));
        assertTrue(Customer.getExtent().contains(c2));
    }

    @Test
    @DisplayName("Should maintain encapsulation")
    void testEncapsulation() {
        Customer c = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.now());
        
        var extent1 = Customer.getExtent();
        var extent2 = Customer.getExtent();
        
        assertNotSame(extent1, extent2);
        extent1.clear();
        assertEquals(1, Customer.getExtent().size());
    }

    @Test
    @DisplayName("Should persist and load extent")
    void testPersistence() throws IOException, ClassNotFoundException {
        Customer c1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 1, 1));
        Customer c2 = new Customer("Jane", "Smith", LocalDate.of(1985, 5, 10), LocalDate.of(2019, 3, 15));
        
        Customer.saveExtent();
        assertTrue(new File("customer_extent.ser").exists());
        
        Customer.clearExtent();
        assertEquals(0, Customer.getExtent().size());
        
        Customer.loadExtent();
        assertEquals(2, Customer.getExtent().size());
        
        assertTrue(Customer.getExtent().stream().anyMatch(c -> c.getFirstName().equals("John")));
        assertTrue(Customer.getExtent().stream().anyMatch(c -> c.getFirstName().equals("Jane")));
    }
}
