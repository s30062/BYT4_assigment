package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Customer> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "customer_extent.ser";

    private String firstName;
    private String lastName;
    private final LocalDate birthDate; // complex attr
    private final LocalDate registrationDate; // basic

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Customer(String firstName, String lastName, LocalDate birthDate, LocalDate registrationDate) {
        if (firstName == null) throw new IllegalArgumentException("firstName cannot be null");
        if (firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty or blank");
        if (firstName.length() > 50) throw new IllegalArgumentException("firstName cannot exceed 50 characters");
        if (lastName == null) throw new IllegalArgumentException("lastName cannot be null");
        if (lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty or blank");
        if (lastName.length() > 50) throw new IllegalArgumentException("lastName cannot exceed 50 characters");
        if (birthDate == null) throw new IllegalArgumentException("birth date cannot be null");
        if (birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("birth date cannot be in the future");
        if (birthDate.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("birth date cannot be before 1900");
        if (registrationDate == null) throw new IllegalArgumentException("registration date cannot be null");
        if (registrationDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("registration date cannot be in the future");
        if (registrationDate.isBefore(birthDate)) throw new IllegalArgumentException("registration date cannot be before birth date");
        
        int age = LocalDate.now().getYear() - birthDate.getYear();
        if (age < 13) throw new IllegalArgumentException("customer must be at least 13 years old");
        
        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        extent.add(this);
    }


    public int getAge(){
        return LocalDate.now().getYear()-birthDate.getYear();
    }

    public String getFirstName() { return firstName; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDate getRegistrationDate() { return registrationDate; }

    public String getLastName() {
        return lastName;
    }



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
        return firstName.equals(c.firstName) && lastName.equals(c.lastName) && birthDate.isEqual(c.getBirthDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate);
    }
}
