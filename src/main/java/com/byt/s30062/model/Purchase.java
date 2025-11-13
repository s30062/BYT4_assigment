package com.byt.s30062.model;


import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Purchase implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Purchase> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "purchase_extent.ser";

    private final int purchaseId;
    private final int customerId;
    private final LocalDateTime purchaseDate;
    private final List<Product> items = new ArrayList<>(); // multi-value attribute
    private String deliveryAddress; // optional for in-store vs online

    private String status; // e.g. , Pending, Completed, Canceled

    public Purchase(int purchaseId, int customerId) {
        if (purchaseId <= 0) throw new IllegalArgumentException("purchaseId positive");
        if (customerId <= 0) throw new IllegalArgumentException("customerId positive");
        this.purchaseId = purchaseId;
        this.customerId = customerId;
        this.purchaseDate = LocalDateTime.now();
        this.status = "Pending";
        extent.add(this);
    }

    public void addProduct(Product p) {
        if (p == null) throw new IllegalArgumentException("product null");
        items.add(p);
    }

    public void removeProduct(Product p) {
        items.remove(p);
    }

    public List<Product> getItems() { return Collections.unmodifiableList(items); }


      // derived attribute total price
    public double getTotalPrice() {
        return items.stream().mapToDouble(Product::getCurrentPrice).sum();
    }

    public int getPurchaseId() { return purchaseId; }
    public int getCustomerId() { return customerId; }
    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = (deliveryAddress == null || deliveryAddress.isBlank()) ? null : deliveryAddress;
    }

    public String getDeliveryAddress() { return deliveryAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("status required");
        this.status = status;
    }


    public static List<Purchase> getExtent() { return extent; }
    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
        }
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Purchase>) in.readObject();
        }
    }
    public static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase p = (Purchase) o;

        return purchaseId == p.purchaseId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseId);
    }
}
