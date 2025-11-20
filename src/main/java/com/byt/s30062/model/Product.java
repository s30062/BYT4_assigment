package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Product> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "product_extent.ser";

    private final String name;
    private String color;

    // temporary decision
    private List<Double> priceHistory = new ArrayList<>();

    public Product(String name, String color, double initialPrice) {
        if (name == null) throw new IllegalArgumentException("name cannot be null");
        if (name.isBlank()) throw new IllegalArgumentException("name cannot be empty or blank");
        if (name.length() > 100) throw new IllegalArgumentException("name cannot exceed 100 characters");
//        if (color == null) throw new IllegalArgumentException("color cannot be null");
//        if (color.isBlank()) throw new IllegalArgumentException("color cannot be empty or blank");
        if (initialPrice <= 0) throw new IllegalArgumentException("initial price must be positive");
        if (Double.isNaN(initialPrice)) throw new IllegalArgumentException("initial price cannot be NaN");
        if (Double.isInfinite(initialPrice)) throw new IllegalArgumentException("initial price cannot be infinite");
        
        this.name = name.trim();
        if (color != null && !color.isBlank()) this.color = color.trim();
        this.priceHistory.add(initialPrice);
        extent.add(this);
    }

    public String getName() { return name; }

    // temporary decision - derived attribute
    public double getCurrentPrice() { 
        return priceHistory.get(priceHistory.size() - 1); 
    }


    public List<Double> getPriceHistory() { return new ArrayList<>(priceHistory); }

    public void updatePrice(double newPrice) {
        if (newPrice <= 0) throw new IllegalArgumentException("price must be positive");
        if (Double.isNaN(newPrice)) throw new IllegalArgumentException("price cannot be NaN");
        if (Double.isInfinite(newPrice)) throw new IllegalArgumentException("price cannot be infinite");
        this.priceHistory.add(newPrice);
    }


    // extent methods
    public static List<Product> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return name.equals(p.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name) + Objects.hash(color);
    }
}
