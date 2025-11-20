package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HistoryOfEmployment implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<HistoryOfEmployment> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "history_of_employment_extent.ser";

    private final LocalDate dateOfStart;
    private LocalDate dateOfFinish; // optional
    private final Person person;
    private final Store store;

    public HistoryOfEmployment(LocalDate dateOfStart, Person person, Store store) {
        this(dateOfStart, null, person, store);
    }

    public HistoryOfEmployment(LocalDate dateOfStart, LocalDate dateOfFinish, Person person, Store store) {
        if (dateOfStart == null) throw new IllegalArgumentException("dateOfStart cannot be null");
        if (dateOfStart.isAfter(LocalDate.now())) throw new IllegalArgumentException("dateOfStart cannot be in the future");
        if (dateOfStart.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("dateOfStart cannot be before 1900");
        
        if (dateOfFinish != null) {
            if (dateOfFinish.isBefore(dateOfStart)) throw new IllegalArgumentException("dateOfFinish cannot be before dateOfStart");
            if (dateOfFinish.isAfter(LocalDate.now())) throw new IllegalArgumentException("dateOfFinish cannot be in the future");
        }
        
        if (person == null) throw new IllegalArgumentException("person cannot be null");
        if (store == null) throw new IllegalArgumentException("store cannot be null");
        
        this.dateOfStart = dateOfStart;
        this.dateOfFinish = dateOfFinish;
        this.person = person;
        this.store = store;
        extent.add(this);
    }

    public LocalDate getDateOfStart() { return dateOfStart; }

    public LocalDate getDateOfFinish() { return dateOfFinish; }

    public Person getPerson() { return person; }

    public Store getStore() { return store; }

    public void setDateOfFinish(LocalDate dateOfFinish) {
        if (dateOfFinish != null) {
            if (dateOfFinish.isBefore(dateOfStart)) throw new IllegalArgumentException("dateOfFinish cannot be before dateOfStart");
            if (dateOfFinish.isAfter(LocalDate.now())) throw new IllegalArgumentException("dateOfFinish cannot be in the future");
        }
        this.dateOfFinish = dateOfFinish;
    }

    public boolean isActive() {
        return dateOfFinish == null;
    }

    public static List<HistoryOfEmployment> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof HistoryOfEmployment)) return false;
        HistoryOfEmployment hoe = (HistoryOfEmployment) o;
        return dateOfStart.isEqual(hoe.dateOfStart) && 
               Objects.equals(dateOfFinish, hoe.dateOfFinish) &&
               person.equals(hoe.person) && 
               store.equals(hoe.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateOfStart, dateOfFinish, person, store);
    }

    @Override
    public String toString() {
        return String.format("HistoryOfEmployment(Person:%s, Store:%s, Start:%s, Finish:%s, Active:%s)", 
            person.getFirstName() + " " + person.getLastName(), 
            store.getAddress().getCity(), 
            dateOfStart, 
            dateOfFinish, 
            isActive());
    }
}
