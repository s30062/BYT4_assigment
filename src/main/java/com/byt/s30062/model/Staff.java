package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Staff extends Person {
    private static final long serialVersionUID = 1L;
    private static List<Staff> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "staff_extent.ser";

    private double baseSalary;
    private boolean isIntern;

    public Staff(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, boolean isIntern) {
        super(firstName, lastName, dateOfBirth);
        if (baseSalary < 0) throw new IllegalArgumentException("baseSalary cannot be negative");
        if (Double.isNaN(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be NaN");
        if (Double.isInfinite(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be infinite");
        if (baseSalary > 10_000_000) throw new IllegalArgumentException("baseSalary cannot exceed 10,000,000");

        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        extent.add(this);
    }

    public boolean isIntern() { return isIntern; }

    public void setBaseSalary(double baseSalary) {
        this.baseSalary = baseSalary;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    public void setIntern(boolean intern) {
        isIntern = intern;
    }

    public static List<Staff> getExtent() { return new ArrayList<>(extent); }

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
        if (!(o instanceof Staff)) return false;
        Staff s = (Staff) o;
        return super.equals(o) && baseSalary == s.getBaseSalary();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), baseSalary);
    }
}
