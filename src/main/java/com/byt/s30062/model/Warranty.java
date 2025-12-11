package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Warranty implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Warranty> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "warranty_extent.ser";

    final Purchase purchase;
    final Unit unit;
    private LocalDate endDate;
    private static int minimumPeriod = 1;

    // Constructor for dummy warranties (added to cart with null endDate)
    public Warranty(Purchase purchase, Unit unit) {
        if (purchase == null) throw new IllegalArgumentException("purchase cannot be null");
        if (unit == null) throw new IllegalArgumentException("unit cannot be null");
        
        this.purchase = purchase;
        this.unit = unit;
        this.endDate = null; // dummy warranty, endDate set later via setDateTo()
        extent.add(this);
        
        // Establish bidirectional links with Purchase and Unit
        purchase.linkWarranty(this);
        unit.linkWarranty(this);
    }

    // Constructor for warranties with specified endDate (after purchase finalization)
    public Warranty(Purchase purchase, Unit unit, LocalDate endDate) {
        if (purchase == null) throw new IllegalArgumentException("purchase cannot be null");
        if (unit == null) throw new IllegalArgumentException("unit cannot be null");
        if (endDate == null) throw new IllegalArgumentException("end date cannot be null");
        
        LocalDate startDate = purchase.getPurchaseDate().toLocalDate();
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("end date cannot be before purchase date");
        if (endDate.isEqual(startDate)) throw new IllegalArgumentException("end date cannot be the same as purchase date");
        
        int yearsBetween = Period.between(startDate, endDate).getYears();
        if (yearsBetween < minimumPeriod) {
            throw new IllegalArgumentException("warranty period must be at least " + minimumPeriod + " year(s)");
        }
        if (yearsBetween > 10) throw new IllegalArgumentException("warranty period cannot exceed 10 years");
        
        this.purchase = purchase;
        this.unit = unit;
        this.endDate = endDate;
        extent.add(this);
        
        // Establish bidirectional links with Purchase and Unit
        purchase.linkWarranty(this);
        unit.linkWarranty(this);
    }

    // derived
    public boolean isValid() {
        return LocalDate.now().isEqual(getStartDate()) || (LocalDate.now().isAfter(getStartDate()) && LocalDate.now().isBefore(endDate));
    }

    // Set endDate on a dummy warranty (endDate was null)
    // Only works if warranty was created as dummy (endDate=null)
    // and new endDate meets minimum period requirement
    public void setEndDate(LocalDate endDate) {
        if (this.endDate != null) {
            throw new IllegalStateException("Cannot modify endDate on a finalized warranty");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate cannot be null");
        }
        
        LocalDate startDate = purchase.getPurchaseDate().toLocalDate();
        if (endDate.isBefore(startDate)) throw new IllegalArgumentException("end date cannot be before purchase date");
        if (endDate.isEqual(startDate)) throw new IllegalArgumentException("end date cannot be the same as purchase date");
        
        int yearsBetween = Period.between(startDate, endDate).getYears();
        if (yearsBetween < minimumPeriod) {
            throw new IllegalArgumentException("warranty period must be at least " + minimumPeriod + " year(s)");
        }
        if (yearsBetween > 10) throw new IllegalArgumentException("warranty period cannot exceed 10 years");
        
        this.endDate = endDate;
    }

    public static int getMinimumPeriod() {
        return minimumPeriod;
    }

    public static void setMinimumPeriod(int minimumPeriod) {
        Warranty.minimumPeriod = minimumPeriod;
    }

    // prolong warranty
    public void prolong(Period period) {
        if (!isValid()) throw new IllegalArgumentException("only valid warranty can be prolonged");
        if (period == null) throw new IllegalArgumentException("period cannot be null");
        if (period.isNegative()) throw new IllegalArgumentException("period cannot be negative");
        if (period.isZero()) throw new IllegalArgumentException("period cannot be zero");
        if (period.getYears() < minimumPeriod) {
            throw new IllegalArgumentException("prolongation period must be at least " + minimumPeriod + " year(s)");
        }
        
        LocalDate newEndDate = this.endDate.plus(period);
        int totalYears = Period.between(getStartDate(), newEndDate).getYears();
        if (totalYears > 10) throw new IllegalArgumentException("total warranty period cannot exceed 10 years");
        
        new Warranty(this.purchase, this.unit, newEndDate);
    }

    public Purchase getPurchase() { return purchase; }
    public Unit getUnit() { return unit; }
    public LocalDate getStartDate() { return purchase.getPurchaseDate().toLocalDate(); }
    public LocalDate getEndDate() { return endDate; }

    // Delete this warranty and unlink from purchase and unit
    public void delete() {
        purchase.unlinkWarranty(this);
        unit.unlinkWarranty(this);
        extent.remove(this);
    }


    public static List<Warranty> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Warranty)) return false;
        Warranty w = (Warranty) o;
        return purchase == w.purchase && unit == w.unit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(purchase)+Objects.hash(unit);
    }
}
