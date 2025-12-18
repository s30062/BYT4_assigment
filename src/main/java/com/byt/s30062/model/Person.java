package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Person> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "person_extent.ser";

    private String firstName;
    private String lastName;
    private final LocalDate dateOfBirth;
    
    // Bidirectional links to roles (0..1 each)
    private Customer customer;
    private Staff staff;

    public Person(String firstName, String lastName, LocalDate dateOfBirth) {
        if (firstName == null) throw new IllegalArgumentException("firstName cannot be null");
        if (firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty or blank");
        if (firstName.length() > 50) throw new IllegalArgumentException("firstName cannot exceed 50 characters");
        if (lastName == null) throw new IllegalArgumentException("lastName cannot be null");
        if (lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty or blank");
        if (lastName.length() > 50) throw new IllegalArgumentException("lastName cannot exceed 50 characters");
        if (dateOfBirth == null) throw new IllegalArgumentException("date of birth cannot be null");
        if (dateOfBirth.isAfter(LocalDate.now())) throw new IllegalArgumentException("date of birth cannot be in the future");
        if (dateOfBirth.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("date of birth cannot be before 1900");

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.dateOfBirth = dateOfBirth;
        this.customer = null;
        this.staff = null;
        extent.add(this);
    }

    public int getAge() {
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }

    public void setFirstName(String firstName) {
        if (firstName == null) throw new IllegalArgumentException("firstName cannot be null");
        if (firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty or blank");
        if (firstName.length() > 50) throw new IllegalArgumentException("firstName cannot exceed 50 characters");
        this.firstName = firstName.trim();
    }

    public void setLastName(String lastName) {
        if (lastName == null) throw new IllegalArgumentException("lastName cannot be null");
        if (lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty or blank");
        if (lastName.length() > 50) throw new IllegalArgumentException("lastName cannot exceed 50 characters");
        this.lastName = lastName.trim();
    }

    // Bidirectional linking methods
    public Customer getCustomer() { return customer; }

    public Staff getStaff() { return staff; }

    // Link this person to a customer (bidirectional)
    void linkCustomer(Customer customer) {
        if (customer != null && this.customer != customer) {
            this.customer = customer;
        }
    }

    // Unlink this person from customer
    void unlinkCustomer() {
        this.customer = null;
    }

    // Link this person to staff (bidirectional)
    void linkStaff(Staff staff) {
        if (staff != null && this.staff != staff) {
            this.staff = staff;
        }
    }

    // Unlink this person from staff
    void unlinkStaff() {
        this.staff = null;
    }

    // Extent management
    public static List<Person> getExtent() { return new ArrayList<>(extent); }

    public static void saveExtent() throws IOException {
        ExtentManager.saveExtent(extent, EXTENT_FILE);
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        extent = ExtentManager.loadExtent(EXTENT_FILE);
    }

    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person p = (Person) o;
        return firstName.equals(p.firstName) && lastName.equals(p.lastName) && dateOfBirth.isEqual(p.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, dateOfBirth);
    }
}
