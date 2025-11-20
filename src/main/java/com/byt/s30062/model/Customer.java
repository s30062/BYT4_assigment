package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends Person {
    private static final long serialVersionUID = 1L;
    private static List<Customer> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "customer_extent.ser";

    private final LocalDate registrationDate; // basic attribute

    public Customer(String firstName, String lastName, LocalDate dateOfBirth, LocalDate registrationDate) {
        super(firstName, lastName, dateOfBirth);
        
        if (registrationDate == null) throw new IllegalArgumentException("registration date cannot be null");
        if (registrationDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("registration date cannot be in the future");
        if (registrationDate.isBefore(dateOfBirth)) throw new IllegalArgumentException("registration date cannot be before birth date");
        
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();
        if (age < 13) throw new IllegalArgumentException("customer must be at least 13 years old");
        
        this.registrationDate = registrationDate;
        extent.add(this);
    }

    public LocalDate getRegistrationDate() { return registrationDate; }


    public static List<Customer> getExtent() { return new ArrayList<>(extent); }

    public static void saveExtent() throws IOException {
        ExtentManager.saveExtent(extent, EXTENT_FILE);
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        extent = ExtentManager.loadExtent(EXTENT_FILE);
    }

    // For testing purposes only - clears extent
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer c = (Customer) o;
        return getFirstName().equals(c.getFirstName()) && getLastName().equals(c.getLastName()) && getDateOfBirth().isEqual(c.getDateOfBirth());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getDateOfBirth());
    }
}
