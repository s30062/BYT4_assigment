package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Staff> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "staff_extent.ser";

    private final String firstName;
    private final String lastName;
    private final double baseSalary;
    private boolean isIntern;

    public Staff(String firstName, String lastName, double baseSalary, boolean isIntern) {
        if (firstName == null) throw new IllegalArgumentException("firstName cannot be null");
        if (firstName.isBlank()) throw new IllegalArgumentException("firstName cannot be empty or blank");
        if (firstName.length() > 50) throw new IllegalArgumentException("firstName cannot exceed 50 characters");
        if (lastName == null) throw new IllegalArgumentException("lastName cannot be null");
        if (lastName.isBlank()) throw new IllegalArgumentException("lastName cannot be empty or blank");
        if (lastName.length() > 50) throw new IllegalArgumentException("lastName cannot exceed 50 characters");
        if (baseSalary < 0) throw new IllegalArgumentException("baseSalary cannot be negative");
        if (Double.isNaN(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be NaN");
        if (Double.isInfinite(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be infinite");
        if (baseSalary > 10_000_000) throw new IllegalArgumentException("baseSalary cannot exceed 10,000,000");

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        extent.add(this);
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public boolean isIntern() { return isIntern; }

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
    static void clearExtent() { extent.clear(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff s = (Staff) o;
        return firstName.equals(s.firstName) && lastName.equals(s.lastName) && baseSalary == s.getBaseSalary();
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, baseSalary);
    }
}
