package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Store implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Store> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "store_extent.ser";

    private final Address address;
    private final LocalDate dateOfOpening;

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
