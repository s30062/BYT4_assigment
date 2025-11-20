package com.byt.s30062.model;

import com.byt.s30062.model.enums.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PartTime extends Staff {

    private List<DayOfWeek> workingDays; // multi-value attribute: 2-5 days
    private WorkingHours workingHours; // complex attribute

    public PartTime(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                   boolean isIntern, List<DayOfWeek> workingDays, WorkingHours workingHours) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern);
        
        if (workingDays == null) throw new IllegalArgumentException("workingDays cannot be null");
        if (workingDays.size() < 2 || workingDays.size() > 5) 
            throw new IllegalArgumentException("workingDays must contain between 2 and 5 days");
        if (workingDays.contains(null)) throw new IllegalArgumentException("workingDays cannot contain null values");
        
        // Ensure no duplicates
        Set<DayOfWeek> uniqueDays = new HashSet<>(workingDays);
        if (uniqueDays.size() != workingDays.size()) throw new IllegalArgumentException("workingDays cannot contain duplicate days");
        
        if (workingHours == null) throw new IllegalArgumentException("workingHours cannot be null");
        
        this.workingDays = new ArrayList<>(workingDays); // defensive copy
        this.workingHours = workingHours;
    }

    public List<DayOfWeek> getWorkingDays() {
        return Collections.unmodifiableList(workingDays);
    }

    public WorkingHours getWorkingHours() { return workingHours; }

    public void setWorkingDays(List<DayOfWeek> workingDays) {
        if (workingDays == null) throw new IllegalArgumentException("workingDays cannot be null");
        if (workingDays.size() < 2 || workingDays.size() > 5) 
            throw new IllegalArgumentException("workingDays must contain between 2 and 5 days");
        if (workingDays.contains(null)) throw new IllegalArgumentException("workingDays cannot contain null values");
        
        Set<DayOfWeek> uniqueDays = new HashSet<>(workingDays);
        if (uniqueDays.size() != workingDays.size()) throw new IllegalArgumentException("workingDays cannot contain duplicate days");
        
        this.workingDays = new ArrayList<>(workingDays);
    }

    public void setWorkingHours(WorkingHours workingHours) {
        if (workingHours == null) throw new IllegalArgumentException("workingHours cannot be null");
        this.workingHours = workingHours;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartTime)) return false;
        PartTime p = (PartTime) o;
        return super.equals(o) && 
               new HashSet<>(workingDays).equals(new HashSet<>(p.workingDays)) &&
               workingHours.equals(p.workingHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), new HashSet<>(workingDays), workingHours);
    }
}
