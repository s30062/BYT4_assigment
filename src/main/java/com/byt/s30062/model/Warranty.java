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

    private final Purchase purchase;
    private final Unit unit;
    private LocalDate endDate;
    private static int minimumPeriod = 1;

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
        
        // Verify unit is in the purchase
        if (!purchase.getItems().contains(unit)) {
            throw new IllegalArgumentException("unit must be part of the purchase");
        }
        
        this.purchase = purchase;
        this.unit = unit;
        this.endDate = endDate;
        extent.add(this);
    }

    // derived
    public boolean isValid() {
        return LocalDate.now().isEqual(getStartDate()) || (LocalDate.now().isAfter(getStartDate()) && LocalDate.now().isBefore(endDate));
    }

    public void setEndDate(LocalDate endDate) {
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
