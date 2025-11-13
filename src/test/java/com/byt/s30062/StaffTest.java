package com.byt.s30062;



import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Staff;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StaffTest {

    @AfterEach
    void cleanup() { Staff.clearExtent(); }

    @Test
    void testStaffAndTotalEmployees() {
        new Staff(1, "A", "B", LocalDate.of(2020,1,1), 1000.0, false);
        new Staff(2, "C", "D", LocalDate.of(2021,1,1), 1200.0, false);
        assertEquals(2, Staff.getExtent().size());
        assertEquals(2, Staff.getTotalEmployees());
    }

    @Test
    void testDerivedSalary() {
        Staff s = new Staff(3, "E", "F", LocalDate.of(2015,1,1), 1000.0, false);
        assertTrue(s.getCurrentSalary() >= 1000.0);
    }

    @Test
    void testManagerInheritance() {
        Manager m = new Manager(10, "Mgr", "One", LocalDate.of(2022,1,1), 2000.0, false, "Sales");
        assertEquals("Sales", m.getDepartment());
    }
}
