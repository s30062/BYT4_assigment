package com.byt.s30062;


import com.byt.s30062.model.Address;

import com.byt.s30062.model.Customer;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @BeforeEach
    void setup() { Customer.clearExtent(); }

    @Test
    void testCreateCustomerValid() {
        Customer c = new Customer(1, "Anna", "Smith", LocalDate.of(1990, 5, 1), LocalDate.now());
        assertEquals("Anna", c.getFirstName());
    }

    @Test
    void testBirthDateFutureRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Customer(2, "John", "Doe",
                LocalDate.now().plusDays(1), LocalDate.now()));
    }

    @Test
    void testAddressComplexAttr() {
        Customer c = new Customer(3, "Tom", "Lee", LocalDate.of(1985,1,1), LocalDate.now());
        Address addr = new Address("Main St", "City", "12345", "Country");
        c.setAddress(addr);

        assertNotNull(c.getAddress());
        assertEquals("Main St", c.getAddress().getStreet());
    }
}
