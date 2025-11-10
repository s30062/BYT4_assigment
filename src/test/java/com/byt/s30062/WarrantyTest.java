package com.byt.s30062;


import com.byt.s30062.Warranty;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WarrantyTest {

    @BeforeEach
    void setup() { Warranty.clearExtent(); }

    @Test
    void testWarrantyValidConstructionAndValidity() {

        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = start.plusYears(1).plusDays(1); // >1 year
        Warranty w = new Warranty(1, 1, 100, start, end);
        assertTrue(w.isValid());
    }

    @Test
    void testWarrantyMinimumOneYearEnforced() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(6);
        assertThrows(IllegalArgumentException.class, () -> new Warranty(2, 1, 101, start, end));
    }

    @Test
    void testExtendYears() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = start.plusYears(1).plusDays(1);
        Warranty w = new Warranty(3, 2, 102, start, end);
        LocalDate oldEnd = w.getEndDate();
        w.extendYears(1);
        assertTrue(w.getEndDate().isAfter(oldEnd));
    }
}
