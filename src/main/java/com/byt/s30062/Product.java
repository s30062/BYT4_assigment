package com.byt.s30062;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Product> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "product_extent.ser";

    private final int id;
    private final String name;
    private double currentPrice;
    private List<Double> priceHistory = new ArrayList<>();
    private String description;

    public Product(int id, String name, double currentPrice) {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
        if (currentPrice <= 0) throw new IllegalArgumentException("price must be > 0");
        this.id = id;
        this.name = name;
        this.currentPrice = currentPrice;
        this.priceHistory.add(currentPrice);
        extent.add(this);
    }


    public void setDescription(String description) {
        this.description = (description == null || description.isBlank()) ? null : description;
    }


    public int getId() { return id; }
    public String getName() { return name; }
    public double getCurrentPrice() { return currentPrice; }


    public List<Double> getPriceHistory() { return new ArrayList<>(priceHistory); }

    public void updatePrice(double newPrice) {
        if (newPrice <= 0) throw new IllegalArgumentException("price must be positive");
        this.currentPrice = newPrice;
        this.priceHistory.add(newPrice);
    }

    public String getDescription() { return description; }

    // extent methods
    public static List<Product> getExtent() { return extent; }

    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Product>) in.readObject();
        }
    }

    //  tests cleanup
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product p = (Product) o;
        return id == p.id && name.equals(p.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
