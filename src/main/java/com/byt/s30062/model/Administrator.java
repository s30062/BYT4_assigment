package com.byt.s30062.model;

import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.LevelOfPermission;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.model.complex.WorkingHours;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class Administrator extends Staff {

    private LevelOfPermission levelOfPermission;

    public Administrator(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                        boolean isIntern, LevelOfPermission levelOfPermission, StaffType staffType, List<DayOfWeek> weekends) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, weekends);
        if (levelOfPermission == null) throw new IllegalArgumentException("levelOfPermission cannot be null");
        this.levelOfPermission = levelOfPermission;
    }

    public Administrator(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, 
                        boolean isIntern, LevelOfPermission levelOfPermission, StaffType staffType, 
                        List<DayOfWeek> workingDays, WorkingHours workingHours) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, workingDays, workingHours);
        if (levelOfPermission == null) throw new IllegalArgumentException("levelOfPermission cannot be null");
        this.levelOfPermission = levelOfPermission;
    }

    public Administrator(Person person, double baseSalary, boolean isIntern, LevelOfPermission levelOfPermission, 
                        StaffType staffType, List<DayOfWeek> weekends) {
        super(person, baseSalary, isIntern, staffType, weekends);
        if (levelOfPermission == null) throw new IllegalArgumentException("levelOfPermission cannot be null");
        this.levelOfPermission = levelOfPermission;
    }

    public Administrator(Person person, double baseSalary, boolean isIntern, LevelOfPermission levelOfPermission, 
                        StaffType staffType, List<DayOfWeek> workingDays, WorkingHours workingHours) {
        super(person, baseSalary, isIntern, staffType, workingDays, workingHours);
        if (levelOfPermission == null) throw new IllegalArgumentException("levelOfPermission cannot be null");
        this.levelOfPermission = levelOfPermission;
    }

    public LevelOfPermission getLevelOfPermission() { return levelOfPermission; }

    public void setLevelOfPermission(LevelOfPermission levelOfPermission) {
        if (levelOfPermission == null) throw new IllegalArgumentException("levelOfPermission cannot be null");
        this.levelOfPermission = levelOfPermission;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Administrator)) return false;
        Administrator a = (Administrator) o;
        return super.equals(o) && levelOfPermission == a.levelOfPermission;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), levelOfPermission);
    }
}
