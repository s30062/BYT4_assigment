package com.byt.s30062.model;


import java.time.LocalDate;

public class Manager extends Staff {
    private String department;

    public Manager(int staffId, String firstName, String lastName, LocalDate hireDate, double baseSalary, boolean isIntern, String department) {
        super(staffId, firstName, lastName, hireDate, baseSalary, isIntern);
        if (department == null || department.isBlank()) throw new IllegalArgumentException("department required");
        this.department = department;
    }

    public String getDepartment() { return department; }
}
