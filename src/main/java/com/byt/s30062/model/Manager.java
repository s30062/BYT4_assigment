package com.byt.s30062.model;

import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.model.complex.WorkingHours;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Manager extends Staff {
    private List<Report> reports = new ArrayList<>(); // 0..many reports for this manager

    public Manager(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary,
                   boolean isIntern, StaffType staffType, List<DayOfWeek> weekends) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, weekends);
    }

    public Manager(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary,
                   boolean isIntern, StaffType staffType, List<DayOfWeek> workingDays, WorkingHours workingHours) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern, staffType, workingDays, workingHours);
    }

    public List<Report> getReports() {
        return new ArrayList<>(reports);
    }

    // Called by Report constructor to link itself to this manager
    void linkReport(Report report) {
        if (report != null && !reports.contains(report)) {
            reports.add(report);
        }
    }

    // Called if Report is deleted or unlinked
    // Removes report from manager's list AND deletes it from system (mandatory relationship)
    void unlinkReport(Report report) {
        if (report != null) {
            reports.remove(report);
            report.removeFromExtent(); // Report cannot exist without a manager
        }
    }

    // Public method to unlink a report (removes it from system due to mandatory relationship)
    public void removeReport(Report report) {
        if (report != null) {
            unlinkReport(report);
        }
    }

}
