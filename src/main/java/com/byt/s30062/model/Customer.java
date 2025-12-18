package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Customer> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "customer_extent.ser";

    private final Person person; // Composed Person (required)
    private final LocalDate registrationDate; // basic attribute
    private List<Purchase> purchases = new ArrayList<>(); // 0..many purchases for this customer

    // Constructor 1: Create Customer with new Person
    public Customer(String firstName, String lastName, LocalDate dateOfBirth, LocalDate registrationDate) {
        // Create and link a new Person
        Person newPerson = new Person(firstName, lastName, dateOfBirth);
        this.person = newPerson;
        
        if (registrationDate == null) throw new IllegalArgumentException("registration date cannot be null");
        if (registrationDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("registration date cannot be in the future");
        if (registrationDate.isBefore(dateOfBirth)) throw new IllegalArgumentException("registration date cannot be before birth date");
        
        int age = LocalDate.now().getYear() - dateOfBirth.getYear();
        if (age < 13) throw new IllegalArgumentException("customer must be at least 13 years old");
        
        this.registrationDate = registrationDate;
        
        // Bidirectional link
        newPerson.linkCustomer(this);
        extent.add(this);
    }

    // Constructor 2: Create Customer with existing Person
    public Customer(Person person, LocalDate registrationDate) {
        if (person == null) throw new IllegalArgumentException("person cannot be null");
        if (registrationDate == null) throw new IllegalArgumentException("registration date cannot be null");
        if (registrationDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("registration date cannot be in the future");
        if (registrationDate.isBefore(person.getDateOfBirth())) throw new IllegalArgumentException("registration date cannot be before birth date");
        
        int age = LocalDate.now().getYear() - person.getDateOfBirth().getYear();
        if (age < 13) throw new IllegalArgumentException("customer must be at least 13 years old");
        
        this.person = person;
        this.registrationDate = registrationDate;
        
        // Bidirectional link
        person.linkCustomer(this);
        extent.add(this);
    }

    // Person delegation methods
    public Person getPerson() { return person; }

    public String getFirstName() { return person.getFirstName(); }

    public String getLastName() { return person.getLastName(); }

    public LocalDate getDateOfBirth() { return person.getDateOfBirth(); }

    public int getAge() { return person.getAge(); }

    public void setFirstName(String firstName) { person.setFirstName(firstName); }

    public void setLastName(String lastName) { person.setLastName(lastName); }

    public LocalDate getRegistrationDate() { return registrationDate; }

    public List<Purchase> getPurchases() {
        return new ArrayList<>(purchases);
    }

    // Called by Purchase constructor to link itself to this customer
    void linkPurchase(Purchase purchase) {
        if (purchase != null && !purchases.contains(purchase)) {
            purchases.add(purchase);
        }
    }

    // Called if Purchase is deleted or unlinked
    // Removes purchase from customer's list AND deletes it from system (mandatory relationship)
    void unlinkPurchase(Purchase purchase) {
        if (purchase != null) {
            purchases.remove(purchase);
            purchase.removeFromExtent(); // Purchase cannot exist without a customer
        }
    }

    // Public method to unlink a purchase (removes it from system due to mandatory relationship)
    public void removePurchase(Purchase purchase) {
        if (purchase != null) {
            unlinkPurchase(purchase);
        }
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
        return person.equals(c.person) && registrationDate.isEqual(c.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, registrationDate);
    }
}
