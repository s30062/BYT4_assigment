package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Product> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "product_extent.ser";

    private final String name;
    private String color;

    // Composition: Product owns PriceHistory objects. If Product is deleted, all its PriceHistory is deleted.
    private List<PriceHistory> priceHistory = new ArrayList<>();

    // Association: Product may have 0 to many Units (each Unit references exactly one Product)
    private List<Unit> units = new ArrayList<>();

    public Product(String name, String color, double initialPrice) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        if (name.isBlank()) throw new IllegalArgumentException("name cannot be empty or blank");
        if (name.length() > 100) throw new IllegalArgumentException("name cannot exceed 100 characters");
        if (Double.isNaN(initialPrice)) throw new IllegalArgumentException("initial price cannot be NaN");
        if (Double.isInfinite(initialPrice)) throw new IllegalArgumentException("initial price cannot be infinite");
        if (initialPrice <= 0) throw new IllegalArgumentException("initial price must be positive");
        
        this.name = name.trim();
        if (color != null && !color.isBlank()) this.color = color.trim();
        // Create initial price history entry (composition: PriceHistory belongs to this Product)
        this.priceHistory.add(new PriceHistory(initialPrice, LocalDate.now(), this));
        extent.add(this);
    }

    public String getName() { return name; }

    public String getColor() { return color; }

    // Derived attribute: get the current (active) price today
    public double getCurrentPrice() {
        LocalDate today = LocalDate.now();
        // Iterate in reverse to find the most recent active price
        for (int i = priceHistory.size() - 1; i >= 0; i--) {
            PriceHistory ph = priceHistory.get(i);
            // Check if this price is active today: started today or before, and not yet ended (or ends today or after)
            if ((ph.getDateFrom().isBefore(today) || ph.getDateFrom().isEqual(today))
                    &&
                    (ph.getDateTo() == null || ph.getDateTo().isAfter(today) || ph.getDateTo().isEqual(today))){
                return ph.getPrice();
            }
        }
        return 0.;
    }

    // Get all price values as doubles (for backward compatibility with tests)
    public List<Double> getPriceHistory() {
        List<Double> prices = new ArrayList<>();
        for (PriceHistory ph : priceHistory) {
            prices.add(ph.getPrice());
        }
        return prices;
    }

    // Get all price history entries (full objects)
    public List<PriceHistory> getPriceHistoryObjects() { 
        return new ArrayList<>(priceHistory); 
    }

    // Get all units of this product
    public List<Unit> getUnits() {
        return new ArrayList<>(units);
    }

    // Called by Unit constructor to register itself with this product
    public void linkUnit(Unit unit) {
        if (unit != null && !units.contains(unit)) {
            units.add(unit);
        }
    }

    // Called when Unit is deleted or disassociated from this Product
    // Deletes the Unit from system since Unit must be associated with exactly one Product
    public void unlinkUnit(Unit unit) {
        if (unit != null) {
            units.remove(unit);
            // Unit is now orphaned (has no product), so delete it from system
            unit.delete();
        }
    }

    // Add new price entry to history
    public void updatePrice(double newPrice) {
        if (newPrice <= 0) throw new IllegalArgumentException("price must be positive");
        if (Double.isNaN(newPrice)) throw new IllegalArgumentException("price cannot be NaN");
        if (Double.isInfinite(newPrice)) throw new IllegalArgumentException("price cannot be infinite");
        
        LocalDate today = LocalDate.now();
        
        // End the current (last) price history entry (if it doesn't have an end date yet)
        if (!priceHistory.isEmpty()) {
            PriceHistory lastPrice = priceHistory.get(priceHistory.size() - 1);
            if (lastPrice.getDateTo() == null && !lastPrice.getDateFrom().isEqual(today)) {
                lastPrice.setDateTo(today);
            }
        }
        
        // Add new price entry
        this.priceHistory.add(new PriceHistory(newPrice, today, this));
    }


    // extent methods
    public static List<Product> getExtent() { return new ArrayList<>(extent); }

    public static void saveExtent() throws IOException {
        ExtentManager.saveExtent(extent, EXTENT_FILE);
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        extent = ExtentManager.loadExtent(EXTENT_FILE);
    }

    // Delete a product: cascade delete all composed PriceHistory objects
    public void delete() {
        priceHistory.clear(); // Clear all composed PriceHistory objects
        extent.remove(this); // Remove from extent
    }

    // For testing purposes only - clears extent
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return name.equals(p.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name) + Objects.hash(color);
    }
}
