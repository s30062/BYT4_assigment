package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Unit implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Unit> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "unit_extent.ser";

    private final LocalDate manufacturingDate;
    private final String serialNumber;
    private Purchase purchase; // optional, 0..1 multiplicity
    private final Product product;

    public Unit(LocalDate manufacturingDate, String serialNumber, Product product) {
        if (manufacturingDate == null) throw new IllegalArgumentException("manufacturingDate cannot be null");
        if (manufacturingDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("manufacturingDate cannot be in the future");
        if (manufacturingDate.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("manufacturingDate cannot be before 1900");
        
        if (serialNumber == null) throw new IllegalArgumentException("serialNumber cannot be null");
        if (serialNumber.isBlank()) throw new IllegalArgumentException("serialNumber cannot be empty or blank");
        if (serialNumber.length() > 100) throw new IllegalArgumentException("serialNumber cannot exceed 100 characters");
        
        if (product == null) throw new IllegalArgumentException("product cannot be null");
        
        this.manufacturingDate = manufacturingDate;
        this.serialNumber = serialNumber.trim();
        this.product = product;
        this.purchase = null; // optional
        
        // Establish bidirectional link with Product
        product.linkUnit(this);
        extent.add(this);
    }

    public LocalDate getManufacturingDate() { return manufacturingDate; }

    public String getSerialNumber() { return serialNumber; }

    public Purchase getPurchase() { return purchase; }

    public Product getProduct() { return product; }

    public void setPurchase(Purchase purchase) {
        if (purchase != null) {
            // Verify that this unit is in the purchase
            if (!purchase.getItems().contains(this)) {
                throw new IllegalArgumentException("unit must be part of the purchase");
            }
        }
        this.purchase = purchase;
    }

    public boolean isPurchased() { return purchase != null; }

    // Delete this Unit from the system
    public void delete() {
        extent.remove(this);
    }

    public static List<Unit> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Unit)) return false;
        Unit u = (Unit) o;
        return manufacturingDate.isEqual(u.manufacturingDate) && 
               serialNumber.equals(u.serialNumber) && 
               product.equals(u.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(manufacturingDate, serialNumber, product);
    }

    @Override
    public String toString() {
        return String.format("Unit(SN:%s, MfgDate:%s, Product:%s, Purchased:%s)", 
            serialNumber, manufacturingDate, product.getName(), isPurchased());
    }
}
