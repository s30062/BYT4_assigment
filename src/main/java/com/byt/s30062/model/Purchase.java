package com.byt.s30062.model;

import com.byt.s30062.model.enums.PurchaseStatus;
import com.byt.s30062.util.ExtentManager;

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

    private final Customer customer;
    private final LocalDateTime purchaseDate;
    private List<Product> items = new ArrayList<>(); // multi-value attribute
    private String deliveryAddress; // optional for in-store vs online

    private PurchaseStatus status;

    public Purchase(Customer customer, List<Product> items) {
        if (customer == null) throw new IllegalArgumentException("customer cannot be null");
        if (items == null) throw new IllegalArgumentException("items list cannot be null");
        if (items.isEmpty()) throw new IllegalArgumentException("purchase must contain at least one item");
        if (items.contains(null)) throw new IllegalArgumentException("items list cannot contain null values");
        
        this.customer = customer;
        this.purchaseDate = LocalDateTime.now();
        this.status = PurchaseStatus.Pending;
        this.items = new ArrayList<>(items); // defensive copy
        extent.add(this);
    }


    public List<Product> getItems() { return Collections.unmodifiableList(items); }


      // derived attribute total price
    public double getTotalPrice() {
        double sum = 0.;
        for (Product p : items){
            sum += p.getCurrentPrice();
        }
        return sum;
    }

    public LocalDateTime getPurchaseDate() { return purchaseDate; }

    public void setDeliveryAddress(String deliveryAddress) {
        if (deliveryAddress != null && deliveryAddress.length() > 200) {
            throw new IllegalArgumentException("delivery address cannot exceed 200 characters");
        }
        this.deliveryAddress = (deliveryAddress == null || deliveryAddress.isBlank()) ? null : deliveryAddress.trim();
    }

    public String getDeliveryAddress() { return deliveryAddress; }

    public PurchaseStatus getStatus() { return status; }

    public void setStatus(PurchaseStatus status) {
        if (status == null) throw new IllegalArgumentException("status cannot be null");
        this.status = status;
    }


    public static List<Purchase> getExtent() { return new ArrayList<>(extent); }

    public static void saveExtent() throws IOException {
        ExtentManager.saveExtent(extent, EXTENT_FILE);
    }

    public static void loadExtent() throws IOException, ClassNotFoundException {
        extent = ExtentManager.loadExtent(EXTENT_FILE);
    }
    static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Purchase)) return false;
        Purchase p = (Purchase) o;

        return customer.equals(p.customer) && items.equals(p.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items) + Objects.hash(customer);
    }
}
