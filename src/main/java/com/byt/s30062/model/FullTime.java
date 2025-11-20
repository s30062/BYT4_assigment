package com.byt.s30062.model;

import com.byt.s30062.model.enums.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FullTime extends Staff {

    private List<DayOfWeek> weekends; // multi-value attribute: exactly 2 days

    public FullTime(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                   boolean isIntern, List<DayOfWeek> weekends) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern);
        
        if (weekends == null) throw new IllegalArgumentException("weekends cannot be null");
        if (weekends.size() != 2) throw new IllegalArgumentException("weekends must contain exactly 2 days");
        if (weekends.contains(null)) throw new IllegalArgumentException("weekends cannot contain null values");
        
        // Ensure no duplicates
        Set<DayOfWeek> uniqueDays = new HashSet<>(weekends);
        if (uniqueDays.size() != 2) throw new IllegalArgumentException("weekends cannot contain duplicate days");
        
        this.weekends = new ArrayList<>(weekends); // defensive copy
    }

    public List<DayOfWeek> getWeekends() {
        return Collections.unmodifiableList(weekends);
    }

    public void setWeekends(List<DayOfWeek> weekends) {
        if (weekends == null) throw new IllegalArgumentException("weekends cannot be null");
        if (weekends.size() != 2) throw new IllegalArgumentException("weekends must contain exactly 2 days");
        if (weekends.contains(null)) throw new IllegalArgumentException("weekends cannot contain null values");
        
        Set<DayOfWeek> uniqueDays = new HashSet<>(weekends);
        if (uniqueDays.size() != 2) throw new IllegalArgumentException("weekends cannot contain duplicate days");
        
        this.weekends = new ArrayList<>(weekends);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FullTime)) return false;
        FullTime f = (FullTime) o;
        return super.equals(o) && new HashSet<>(weekends).equals(new HashSet<>(f.weekends));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), new HashSet<>(weekends));
    }
}
