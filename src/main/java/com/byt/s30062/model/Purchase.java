package com.byt.s30062.model;

import com.byt.s30062.model.enums.PurchaseStatus;
import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
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
    private String deliveryAddress; // optional for in-store vs online
    private List<Report> reports = new ArrayList<>(); // 0..many reports associated with this purchase
    private List<Warranty> warranties = new ArrayList<>(); // 1..many warranties link units to purchase

    private PurchaseStatus status;

    public Purchase(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("customer cannot be null");
        
        this.customer = customer;
        this.purchaseDate = LocalDateTime.now();
        this.status = PurchaseStatus.Pending;
        
        extent.add(this);
        
        // Link purchase to customer (bidirectional)
        customer.linkPurchase(this);
    }


    public Customer getCustomer() { return customer; }

    public List<Report> getReports() {
        return new ArrayList<>(reports);
    }

    // Called by Report to link itself to this purchase
    void linkReport(Report report) {
        if (report != null && !reports.contains(report)) {
            reports.add(report);
        }
    }

    // Called when Report is unlinked from this purchase
    void unlinkReport(Report report) {
        if (report != null) {
            reports.remove(report);
        }
    }

    // Get all warranties for this purchase
    public List<Warranty> getWarranties() { return new ArrayList<>(warranties); }

    // Called by Warranty to link itself to this purchase
    void linkWarranty(Warranty warranty) {
        if (warranty != null && !warranties.contains(warranty)) {
            warranties.add(warranty);
        }
    }

    // Called by Warranty when removed
    void unlinkWarranty(Warranty warranty) {
        if (warranty != null) {
            warranties.remove(warranty);
        }
    }

    // Derived: get all units from warranties
    public List<Unit> getItems() {
        List<Unit> items = new ArrayList<>();
        for (Warranty w : warranties) {
            Unit u = w.getUnit();
            if (!items.contains(u)) {
                items.add(u);
            }
        }
        return items;
    }

    // derived attribute total price
    public double getTotalPrice() {
        double sum = 0.;
        for (Unit u : getItems()){
            sum += u.getProduct().getCurrentPrice();
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

    // Finalize purchase: set endDate on all dummy warranties and update status to Preparing
    public void finalizePurchase() {
        if (warranties.isEmpty()) {
            throw new IllegalStateException("Cannot finalize purchase with no items");
        }
        
        // Set endDate on all dummy warranties (endDate was null)
        LocalDate warrantyEndDate = purchaseDate.toLocalDate().plusYears(Warranty.getMinimumPeriod());
        for (Warranty w : new ArrayList<>(warranties)) {
            if (w.getEndDate() == null) {
                w.setEndDate(warrantyEndDate);
            }
        }
        
        // Update purchase status to Preparing
        this.status = PurchaseStatus.Preparing;
    }

    // Add report to this purchase (bidirectional link)
    public void addReport(Report report) {
        if (report != null) {
            linkReport(report);
            report.linkPurchase(this);
        }
    }

    // Remove report from this purchase (bidirectional unlink)
    public void removeReport(Report report) {
        if (report != null) {
            unlinkReport(report);
            report.unlinkPurchase(this);
        }
    }

    // Delete this Purchase and unlink from customer
    public void delete() {
        customer.unlinkPurchase(this);
        extent.remove(this);
    }

    // Remove this Purchase from extent only (called by Customer when unlinking)
    void removeFromExtent() {
        extent.remove(this);
    }

    public static List<Purchase> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Purchase)) return false;
        Purchase p = (Purchase) o;

        return customer.equals(p.customer) && purchaseDate.equals(p.purchaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchaseDate, customer);
    }
}
