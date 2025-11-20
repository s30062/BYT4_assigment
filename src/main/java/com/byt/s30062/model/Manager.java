package com.byt.s30062.model;

import java.time.LocalDate;

public class Manager extends Staff {

    public Manager(String firstName, String lastName, LocalDate dateOfBirth, double baseSalary, boolean isIntern) {
        super(firstName, lastName, dateOfBirth, baseSalary, isIntern);
    }

}
