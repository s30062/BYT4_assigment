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
    private final Product product;
    private Store store; // optional, 0..1 (Unit may be in 0 or 1 store)
    
    // Association through Warranty: unit may have 0..many warranties
    List<Warranty> warranties = new ArrayList<>();

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
        
        // Establish bidirectional link with Product
        product.linkUnit(this);
        extent.add(this);
    }

    public LocalDate getManufacturingDate() { return manufacturingDate; }

    public String getSerialNumber() { return serialNumber; }

    // Get all warranties for this unit
    public List<Warranty> getWarranties() { return new ArrayList<>(warranties); }
    
    // Derived: get purchase if this unit has a warranty (0..1)
    public Purchase getPurchase() {
        if (warranties.isEmpty()) return null;
        return warranties.get(0).getPurchase();
    }

    public Product getProduct() { return product; }

    public Store getStore() { return store; }

    // Set store for this unit (0..1). Maintains ordered association.
    public void setStore(Store newStore) {
        // If changing stores, unlink from old store
        if (this.store != null && this.store != newStore) {
            this.store.unlinkUnit(this); // will clear this.store
        }
        // Set new store and link
        if (newStore != null) {
            this.store = newStore;
            newStore.linkUnit(this);
        } else {
            this.store = null;
        }
    }

    // Called by Store.unlinkUnit() to clear store reference
    void clearStore() {
        this.store = null;
    }

    // Called by Warranty to link itself to this unit
    void linkWarranty(Warranty warranty) {
        if (warranty != null && !warranties.contains(warranty)) {
            // Constraint: unit can be associated with at most one purchase
            if (!warranties.isEmpty()) {
                Purchase existingPurchase = warranties.get(0).getPurchase();
                if (!existingPurchase.equals(warranty.getPurchase())) {
                    throw new IllegalArgumentException("Unit can only be associated with one purchase");
                }
            }
            warranties.add(warranty);
        }
    }
    
    // Called by Warranty when removed
    void unlinkWarranty(Warranty warranty) {
        if (warranty != null) {
            warranties.remove(warranty);
        }
    }

    public boolean isPurchased() { return !warranties.isEmpty(); }

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
