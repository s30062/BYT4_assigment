package com.byt.s30062.model;

import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Staff extends Person {
    private static final long serialVersionUID = 1L;
    private static List<Staff> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "staff_extent.ser";

    private double baseSalary;
    private boolean isIntern;
    
    // Bag association: employment history records (managed by HistoryOfEmployment)
    List<HistoryOfEmployment> employmentHistory = new ArrayList<>();

    // Reflexive association: supervision (non-intern supervises interns)
    private Staff supervisedBy; // nullable; only set for interns
    private List<Staff> supervises = new ArrayList<>(); // non-intern can supervise many interns

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
        // Enforce supervision invariants when changing role
        if (intern) {
            // Becoming an intern: cannot supervise others
            if (!supervises.isEmpty()) {
                throw new IllegalStateException("An intern cannot supervise other staff");
            }
        } else {
            // Becoming non-intern: cannot be supervised
            if (supervisedBy != null) {
                throw new IllegalStateException("A non-intern cannot be supervised");
            }
        }
        isIntern = intern;
    }

    public List<HistoryOfEmployment> getEmploymentHistory() {
        return new ArrayList<>(employmentHistory);
    }

    // Supervision API
    public Staff getSupervisor() { return supervisedBy; }

    public List<Staff> getSupervises() { return new ArrayList<>(supervises); }

    // Supervisor-side linking: this supervises the given intern
    public void supervise(Staff intern) {
        if (intern == null) return;
        if (this == intern) throw new IllegalArgumentException("Staff cannot supervise themselves");
        if (this.isIntern) throw new IllegalArgumentException("Only non-interns can supervise interns");
        if (!intern.isIntern) throw new IllegalArgumentException("Only interns can be supervised");
        if (intern.supervisedBy != null && intern.supervisedBy != this) {
            throw new IllegalArgumentException("Intern is already supervised by another staff member");
        }
        if (!supervises.contains(intern)) {
            supervises.add(intern);
        }
        intern.supervisedBy = this;
    }

    // Supervisor-side unlinking
    public void stopSupervising(Staff intern) {
        if (intern == null) return;
        if (supervises.remove(intern)) {
            if (intern.supervisedBy == this) {
                intern.supervisedBy = null;
            }
        }
    }

    // Intern-side linking: set this intern's supervisor
    public void setSupervisor(Staff supervisor) {
        if (supervisor == null) {
            clearSupervisor();
            return;
        }
        if (!this.isIntern) throw new IllegalArgumentException("Only interns can be supervised");
        if (supervisor.isIntern) throw new IllegalArgumentException("An intern cannot be a supervisor");
        if (this == supervisor) throw new IllegalArgumentException("Staff cannot supervise themselves");
        if (this.supervisedBy == supervisor) return; // no-op

        // detach from previous supervisor if any
        if (this.supervisedBy != null) {
            this.supervisedBy.supervises.remove(this);
        }
        this.supervisedBy = supervisor;
        if (!supervisor.supervises.contains(this)) {
            supervisor.supervises.add(this);
        }
    }

    // Intern-side unlinking
    public void clearSupervisor() {
        if (this.supervisedBy != null) {
            Staff old = this.supervisedBy;
            this.supervisedBy = null;
            old.supervises.remove(this);
        }
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
