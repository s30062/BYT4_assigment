package com.byt.s30062.model;

import com.byt.s30062.model.complex.Address;
import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.Collections;
import java.util.Comparator;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Store> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "store_extent.ser";

    private final Address address;
    private final LocalDate dateOfOpening;

    // Ordered association: units sorted by serial number
    private final List<Unit> units = new ArrayList<>();
    
    // Bag association: employment history records (managed by HistoryOfEmployment)
    List<HistoryOfEmployment> employmentHistory = new ArrayList<>();

    public Store(Address address, LocalDate dateOfOpening) {
        if (address == null) throw new IllegalArgumentException("address cannot be null");
        if (dateOfOpening == null) throw new IllegalArgumentException("dateOfOpening cannot be null");
        if (dateOfOpening.isAfter(LocalDate.now())) throw new IllegalArgumentException("dateOfOpening cannot be in the future");
        if (dateOfOpening.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("dateOfOpening cannot be before 1900");
        
        this.address = address;
        this.dateOfOpening = dateOfOpening;
        extent.add(this);
    }

    public Address getAddress() { return address; }

    public LocalDate getDateOfOpening() { return dateOfOpening; }

    public List<Unit> getUnits() {
        return new ArrayList<>(units);
    }

    public void linkUnit(Unit unit) {
        if (unit == null) return;
        if (!units.contains(unit)) {
            units.add(unit);
            units.sort(Comparator.comparing(Unit::getSerialNumber));
        }
    }

    public void unlinkUnit(Unit unit) {
        if (unit == null) return;
        if (units.remove(unit)) {
            unit.clearStore();
        }
    }

    public List<HistoryOfEmployment> getEmploymentHistory() {
        return new ArrayList<>(employmentHistory);
    }

    public static List<Store> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Store)) return false;
        Store store = (Store) o;
        return address.equals(store.address) && dateOfOpening.isEqual(store.dateOfOpening);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, dateOfOpening);
    }

    @Override
    public String toString() {
        return String.format("Store(Address:%s, DateOfOpening:%s)", address, dateOfOpening);
    }
}
