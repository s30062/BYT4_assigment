package com.byt.s30062.model;

import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.model.complex.WorkingHours;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class SalesStaff extends Staff {

    private double salesBonus;

    public SalesStaff(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                     boolean isIntern, double salesBonus, StaffType staffType, List<DayOfWeek> weekends) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, weekends);
        validateSalesBonus(salesBonus);
        this.salesBonus = salesBonus;
    }

    public SalesStaff(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                     boolean isIntern, double salesBonus, StaffType staffType, 
                     List<DayOfWeek> workingDays, WorkingHours workingHours) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, workingDays, workingHours);
        validateSalesBonus(salesBonus);
        this.salesBonus = salesBonus;
    }

    private static void validateSalesBonus(double salesBonus) {
        if (salesBonus < 0) throw new IllegalArgumentException("salesBonus cannot be negative");
        if (Double.isNaN(salesBonus)) throw new IllegalArgumentException("salesBonus cannot be NaN");
        if (Double.isInfinite(salesBonus)) throw new IllegalArgumentException("salesBonus cannot be infinite");
        if (salesBonus > 10_000_000) throw new IllegalArgumentException("salesBonus cannot exceed 10,000,000");
    }

    public double getSalesBonus() { return salesBonus; }

    public void setSalesBonus(double salesBonus) {
        if (salesBonus < 0) throw new IllegalArgumentException("salesBonus cannot be negative");
        if (Double.isNaN(salesBonus)) throw new IllegalArgumentException("salesBonus cannot be NaN");
        if (Double.isInfinite(salesBonus)) throw new IllegalArgumentException("salesBonus cannot be infinite");
        if (salesBonus > 10_000_000) throw new IllegalArgumentException("salesBonus cannot exceed 10,000,000");
        this.salesBonus = salesBonus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SalesStaff)) return false;
        SalesStaff s = (SalesStaff) o;
        return super.equals(o) && salesBonus == s.salesBonus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), salesBonus);
    }
}
