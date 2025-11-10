package com.byt.s30062;

import com.byt.s30062.Address;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Customer> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "customer_extent.ser";

    private final int id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthDate; // complex attr
    private final LocalDate registrationDate; // basic
    private Address address; // optional complex

    public Customer(int id, String firstName, String lastName, LocalDate birthDate, LocalDate registrationDate) {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("firstName required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName required");
        if (birthDate == null || birthDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("invalid birth date");
        if (registrationDate == null || registrationDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("invalid registration date");
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        extent.add(this);
    }

    public void setAddress(Address address) {
        this.address = address;
    }


    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getBirthDate() { return birthDate; }
    public LocalDate getRegistrationDate() { return registrationDate; }
    public Address getAddress() { return address; }


    public static List<Customer> getExtent() { return extent; }

    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
        }
    }


    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Customer>) in.readObject();
        }
    }

    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer c = (Customer) o;
        return id == c.id && firstName.equals(c.firstName) && lastName.equals(c.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }
}
