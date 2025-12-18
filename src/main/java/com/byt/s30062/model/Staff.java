package com.byt.s30062.model;

import com.byt.s30062.model.complex.WorkingHours;
import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.util.ExtentManager;

import java.io.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Staff implements Serializable {
    private static final long serialVersionUID = 1L;
    private static List<Staff> extent = new ArrayList<>();
    private static final String EXTENT_FILE = "staff_extent.ser";

    protected Person person; // Composed Person (required)
    private double baseSalary;
    private boolean isIntern;
    private StaffType staffType; // FULL_TIME or PART_TIME (required)

    // FullTime-specific attributes
    private List<DayOfWeek> weekends; // exactly 2 days for FULL_TIME

    // PartTime-specific attributes
    private List<DayOfWeek> workingDays; // 2-5 days for PART_TIME
    private WorkingHours workingHours; // complex attribute for PART_TIME

    // Bag association: employment history records (managed by HistoryOfEmployment)
    List<HistoryOfEmployment> employmentHistory = new ArrayList<>();

    // Reflexive association: supervision (non-intern supervises interns)
    private Staff supervisedBy; // nullable; only set for interns
    private List<Staff> supervises = new ArrayList<>(); // non-intern can supervise many interns

    // Protected constructor for FULL_TIME staff with new Person
    protected Staff(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary,
                    boolean isIntern, StaffType staffType, List<DayOfWeek> weekends) {
        Person newPerson = new Person(firstName, lastName, dateOfBirth);
        this.person = newPerson;
        validateBaseSalary(baseSalary);
        if (staffType != StaffType.FULL_TIME) {
            throw new IllegalArgumentException("This constructor is for FULL_TIME staff only");
        }
        validateWeekends(weekends);
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        this.staffType = staffType;
        this.weekends = new ArrayList<>(weekends);
        // Bidirectional link
        newPerson.linkStaff(this);
        extent.add(this);
    }

    // Protected constructor for PART_TIME staff with new Person
    protected Staff(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary,
                    boolean isIntern, StaffType staffType, List<DayOfWeek> workingDays, WorkingHours workingHours) {
        Person newPerson = new Person(firstName, lastName, dateOfBirth);
        this.person = newPerson;
        validateBaseSalary(baseSalary);
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalArgumentException("This constructor is for PART_TIME staff only");
        }
        validateWorkingDays(workingDays);
        if (workingHours == null) throw new IllegalArgumentException("workingHours cannot be null");
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        this.staffType = staffType;
        this.workingDays = new ArrayList<>(workingDays);
        this.workingHours = workingHours;
        // Bidirectional link
        newPerson.linkStaff(this);
        extent.add(this);
    }

    // Protected constructor for FULL_TIME staff with existing Person
    protected Staff(Person person, double baseSalary, boolean isIntern, StaffType staffType, List<DayOfWeek> weekends) {
        if (person == null) throw new IllegalArgumentException("person cannot be null");
        this.person = person;
        validateBaseSalary(baseSalary);
        if (staffType != StaffType.FULL_TIME) {
            throw new IllegalArgumentException("This constructor is for FULL_TIME staff only");
        }
        validateWeekends(weekends);
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        this.staffType = staffType;
        this.weekends = new ArrayList<>(weekends);
        // Bidirectional link
        person.linkStaff(this);
        extent.add(this);
    }

    // Protected constructor for PART_TIME staff with existing Person
    protected Staff(Person person, double baseSalary, boolean isIntern, StaffType staffType, List<DayOfWeek> workingDays, WorkingHours workingHours) {
        if (person == null) throw new IllegalArgumentException("person cannot be null");
        this.person = person;
        validateBaseSalary(baseSalary);
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalArgumentException("This constructor is for PART_TIME staff only");
        }
        validateWorkingDays(workingDays);
        if (workingHours == null) throw new IllegalArgumentException("workingHours cannot be null");
        this.baseSalary = baseSalary;
        this.isIntern = isIntern;
        this.staffType = staffType;
        this.workingDays = new ArrayList<>(workingDays);
        this.workingHours = workingHours;
        // Bidirectional link
        person.linkStaff(this);
        extent.add(this);
    }

    // Validation helpers
    private static void validateBaseSalary(double baseSalary) {
        if (baseSalary < 0) throw new IllegalArgumentException("baseSalary cannot be negative");
        if (Double.isNaN(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be NaN");
        if (Double.isInfinite(baseSalary)) throw new IllegalArgumentException("baseSalary cannot be infinite");
        if (baseSalary > 10_000_000) throw new IllegalArgumentException("baseSalary cannot exceed 10,000,000");
    }

    private static void validateWeekends(List<DayOfWeek> weekends) {
        if (weekends == null) throw new IllegalArgumentException("weekends cannot be null");
        if (weekends.size() != 2) throw new IllegalArgumentException("weekends must contain exactly 2 days");
        if (weekends.contains(null)) throw new IllegalArgumentException("weekends cannot contain null values");
        Set<DayOfWeek> uniqueDays = new HashSet<>(weekends);
        if (uniqueDays.size() != 2) throw new IllegalArgumentException("weekends cannot contain duplicate days");
    }

    private static void validateWorkingDays(List<DayOfWeek> workingDays) {
        if (workingDays == null) throw new IllegalArgumentException("workingDays cannot be null");
        if (workingDays.size() < 2 || workingDays.size() > 5)
            throw new IllegalArgumentException("workingDays must contain between 2 and 5 days");
        if (workingDays.contains(null)) throw new IllegalArgumentException("workingDays cannot contain null values");
        Set<DayOfWeek> uniqueDays = new HashSet<>(workingDays);
        if (uniqueDays.size() != workingDays.size()) throw new IllegalArgumentException("workingDays cannot contain duplicate days");
    }

    // Person delegation methods
    public Person getPerson() { return person; }

    public String getFirstName() { return person.getFirstName(); }

    public String getLastName() { return person.getLastName(); }

    public LocalDate getDateOfBirth() { return person.getDateOfBirth(); }

    public int getAge() { return person.getAge(); }

    public void setFirstName(String firstName) { person.setFirstName(firstName); }

    public void setLastName(String lastName) { person.setLastName(lastName); }

    public StaffType getStaffType() { return staffType; }

    public boolean isIntern() { return isIntern; }

    public void setBaseSalary(double baseSalary) {
        validateBaseSalary(baseSalary);
        this.baseSalary = baseSalary;
    }

    public double getBaseSalary() {
        return baseSalary;
    }

    // FULL_TIME specific
    public List<DayOfWeek> getWeekends() {
        if (staffType != StaffType.FULL_TIME) {
            throw new IllegalStateException("getWeekends() can only be called on FULL_TIME staff");
        }
        return Collections.unmodifiableList(weekends);
    }

    public void setWeekends(List<DayOfWeek> weekends) {
        if (staffType != StaffType.FULL_TIME) {
            throw new IllegalStateException("setWeekends() can only be called on FULL_TIME staff");
        }
        validateWeekends(weekends);
        this.weekends = new ArrayList<>(weekends);
    }

    // PART_TIME specific
    public List<DayOfWeek> getWorkingDays() {
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalStateException("getWorkingDays() can only be called on PART_TIME staff");
        }
        return Collections.unmodifiableList(workingDays);
    }

    public WorkingHours getWorkingHours() {
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalStateException("getWorkingHours() can only be called on PART_TIME staff");
        }
        return workingHours;
    }

    public void setWorkingDays(List<DayOfWeek> workingDays) {
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalStateException("setWorkingDays() can only be called on PART_TIME staff");
        }
        validateWorkingDays(workingDays);
        this.workingDays = new ArrayList<>(workingDays);
    }

    public void setWorkingHours(WorkingHours workingHours) {
        if (staffType != StaffType.PART_TIME) {
            throw new IllegalStateException("setWorkingHours() can only be called on PART_TIME staff");
        }
        if (workingHours == null) throw new IllegalArgumentException("workingHours cannot be null");
        this.workingHours = workingHours;
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
        if (!person.equals(s.person) || baseSalary != s.getBaseSalary()) return false;
        if (!Objects.equals(staffType, s.staffType)) return false;
        if (staffType == StaffType.FULL_TIME) {
            return weekends != null && s.weekends != null && new HashSet<>(weekends).equals(new HashSet<>(s.weekends));
        }
        if (staffType == StaffType.PART_TIME) {
            return workingDays != null && s.workingDays != null &&
                    new HashSet<>(workingDays).equals(new HashSet<>(s.workingDays)) &&
                    Objects.equals(workingHours, s.workingHours);
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = Objects.hash(person, baseSalary, staffType);
        if (staffType == StaffType.FULL_TIME && weekends != null) {
            hash = Objects.hash(hash, new HashSet<>(weekends));
        } else if (staffType == StaffType.PART_TIME && workingDays != null) {
            hash = Objects.hash(hash, new HashSet<>(workingDays), workingHours);
        }
        return hash;
    }
}
