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
    private static int totalEmployees = 0; // static attribute

    private final int staffId;
    private final String firstName;
    private final String lastName;
    private final LocalDate hireDate;
    private final double baseSalary;
    private boolean isIntern;

    public Staff(int staffId, String firstName, String lastName, LocalDate hireDate, double baseSalary, boolean isIntern) {
        if (staffId <= 0) throw new IllegalArgumentException("staffId positive");
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("firstName required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("lastName required");
        if (hireDate == null || hireDate.isAfter(LocalDate.now())) throw new IllegalArgumentException("invalid hire date");
        if (baseSalary < 0) throw new IllegalArgumentException("baseSalary cannot be negative");
        this.staffId = staffId;

        this.firstName = firstName;
        this.lastName = lastName;

        this.hireDate = hireDate;
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        extent.add(this);
        totalEmployees++;
    }

    public int getStaffId() { return staffId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getHireDate() { return hireDate; }
    public boolean isIntern() { return isIntern; }
 //derived attribure
    public int getYearsWorked() {
        return Period.between(hireDate, LocalDate.now()).getYears();
    }

    // derived computed salary
    public double getCurrentSalary() {
        double multiplier = 1 + 0.02 * getYearsWorked();
        return Math.round(baseSalary * multiplier * 100.0) / 100.0;
    }

    public static int getTotalEmployees() { return totalEmployees; }


    public static List<Staff> getExtent() { return extent; }
    public static void saveExtent() throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(EXTENT_FILE))) {
            out.writeObject(extent);
            out.writeInt(totalEmployees);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadExtent() throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(EXTENT_FILE))) {
            extent = (List<Staff>) in.readObject();
            try { totalEmployees = in.readInt(); } catch (EOFException e) { /*ignore*/ }
        }
    }

    public static void clearExtent() { extent.clear(); totalEmployees = 0; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff s = (Staff) o;
        return staffId == s.staffId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffId);
    }
}
