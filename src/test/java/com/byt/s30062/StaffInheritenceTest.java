package com.byt.s30062;

import com.byt.s30062.model.Manager;
import com.byt.s30062.model.Administrator;
import com.byt.s30062.model.SalesStaff;
import com.byt.s30062.model.Staff;
import com.byt.s30062.model.enums.DayOfWeek;
import com.byt.s30062.model.enums.LevelOfPermission;
import com.byt.s30062.model.enums.StaffType;
import com.byt.s30062.model.complex.WorkingHours;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Staff multi-aspect inheritance:
 * - Role aspect: Manager, Administrator, SalesStaff (WHO they are)
 * - Employment type aspect: FULL_TIME, PART_TIME (HOW they work)
 * 
 * Every Staff must have both a role AND an employment type.
 */
class StaffInheritenceTest {

    @BeforeEach
    void setup() {
        Staff.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Staff.clearExtent();
        new File("staff_extent.ser").delete();
    }

    // ============================================================================
    // ASPECT 1: Role Aspect (Manager, Administrator, SalesStaff)
    // ============================================================================

    @Test
    @DisplayName("Manager FULL_TIME has proper role attributes")
    void testManagerFullTimeRole() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Manager manager = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertTrue(manager instanceof Manager);
        assertTrue(manager instanceof Staff);
        assertTrue(manager.getReports().isEmpty());
    }

    @Test
    @DisplayName("Administrator PART_TIME has proper role attributes")
    void testAdministratorPartTimeRole() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        Administrator admin = new Administrator("Bob", "Smith", birthDate, 60000.0, true,
                                               LevelOfPermission.Senior, StaffType.PART_TIME,
                                               Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                               new WorkingHours(9.0, 17.0));
        
        assertTrue(admin instanceof Administrator);
        assertTrue(admin instanceof Staff);
        assertEquals(LevelOfPermission.Senior, admin.getLevelOfPermission());
    }

    @Test
    @DisplayName("SalesStaff FULL_TIME has proper role attributes")
    void testSalesStaffFullTimeRole() {
        LocalDate birthDate = LocalDate.of(1992, 7, 10);
        SalesStaff sales = new SalesStaff("Charlie", "Brown", birthDate, 50000.0, false, 5000.0, StaffType.FULL_TIME,
                                         Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertTrue(sales instanceof SalesStaff);
        assertTrue(sales instanceof Staff);
        assertEquals(5000.0, sales.getSalesBonus());
    }

    // ============================================================================
    // ASPECT 2: Employment Type Aspect (FULL_TIME vs PART_TIME)
    // ============================================================================

    @Test
    @DisplayName("FULL_TIME staff has exactly 2 weekend days")
    void testFullTimeHasWeekends() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Manager manager = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals(StaffType.FULL_TIME, manager.getStaffType());
        assertEquals(2, manager.getWeekends().size());
        assertTrue(manager.getWeekends().contains(DayOfWeek.Saturday));
        assertTrue(manager.getWeekends().contains(DayOfWeek.Sunday));
    }

    @Test
    @DisplayName("PART_TIME staff has 2-5 working days and hours")
    void testPartTimeHasWorkingDaysAndHours() {
        LocalDate birthDate = LocalDate.of(1990, 5, 15);
        SalesStaff sales = new SalesStaff("Bob", "Smith", birthDate, 40000.0, true,
                                         3000.0, StaffType.PART_TIME,
                                         Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday),
                                         new WorkingHours(14.0, 18.0));
        
        assertEquals(StaffType.PART_TIME, sales.getStaffType());
        assertEquals(3, sales.getWorkingDays().size());
        assertEquals(14.0, sales.getWorkingHours().getStartHour());
        assertEquals(18.0, sales.getWorkingHours().getFinishHour());
    }

    // ============================================================================
    // CROSS-ASPECT: Combining Role and Employment Type
    // ============================================================================

    @Test
    @DisplayName("Manager can be FULL_TIME")
    void testManagerFullTime() {
        LocalDate birthDate = LocalDate.of(1985, 3, 20);
        Manager manager = new Manager("Alice", "Johnson", birthDate, 80000.0, false,
                                     StaffType.FULL_TIME, Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals("Alice", manager.getFirstName());
        assertEquals(StaffType.FULL_TIME, manager.getStaffType());
        assertEquals(2, manager.getWeekends().size());
        assertTrue(manager.getReports().isEmpty());
    }

    @Test
    @DisplayName("Manager can be PART_TIME")
    void testManagerPartTime() {
        LocalDate birthDate = LocalDate.of(1988, 1, 15);
        Manager manager = new Manager("Carol", "Davis", birthDate, 70000.0, false,
                                     StaffType.PART_TIME,
                                     Arrays.asList(DayOfWeek.Tuesday, DayOfWeek.Thursday),
                                     new WorkingHours(10.0, 16.0));
        
        assertEquals("Carol", manager.getFirstName());
        assertEquals(StaffType.PART_TIME, manager.getStaffType());
        assertEquals(2, manager.getWorkingDays().size());
        assertEquals(10.0, manager.getWorkingHours().getStartHour());
    }

    @Test
    @DisplayName("Administrator can be FULL_TIME")
    void testAdministratorFullTime() {
        LocalDate birthDate = LocalDate.of(1992, 6, 22);
        Administrator admin = new Administrator("David", "Evans", birthDate, 65000.0, true, LevelOfPermission.Middle, StaffType.FULL_TIME,
                                               Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals(LevelOfPermission.Middle, admin.getLevelOfPermission());
        assertEquals(StaffType.FULL_TIME, admin.getStaffType());
        assertEquals(2, admin.getWeekends().size());
    }

    @Test
    @DisplayName("Administrator can be PART_TIME")
    void testAdministratorPartTime() {
        LocalDate birthDate = LocalDate.of(1995, 9, 30);
        Administrator admin = new Administrator("Eve", "Frank", birthDate, 55000.0, false,
                                               LevelOfPermission.Junior, StaffType.PART_TIME,
                                               Arrays.asList(DayOfWeek.Wednesday, DayOfWeek.Friday),
                                               new WorkingHours(13.0, 19.0));
        
        assertEquals(LevelOfPermission.Junior, admin.getLevelOfPermission());
        assertEquals(StaffType.PART_TIME, admin.getStaffType());
        assertEquals(2, admin.getWorkingDays().size());
    }

    @Test
    @DisplayName("SalesStaff can be FULL_TIME")
    void testSalesStaffFullTime() {
        LocalDate birthDate = LocalDate.of(1987, 4, 12);
        SalesStaff sales = new SalesStaff("Frank", "Garcia", birthDate, 45000.0, false, 8000.0, StaffType.FULL_TIME,
                                         Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals(8000.0, sales.getSalesBonus());
        assertEquals(StaffType.FULL_TIME, sales.getStaffType());
        assertEquals(2, sales.getWeekends().size());
    }

    @Test
    @DisplayName("SalesStaff can be PART_TIME")
    void testSalesStaffPartTime() {
        LocalDate birthDate = LocalDate.of(1993, 11, 5);
        SalesStaff sales = new SalesStaff("Grace", "Harris", birthDate, 35000.0, true,
                                         2500.0, StaffType.PART_TIME,
                                         Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday),
                                         new WorkingHours(9.0, 13.0));
        
        assertEquals(2500.0, sales.getSalesBonus());
        assertEquals(StaffType.PART_TIME, sales.getStaffType());
        assertEquals(3, sales.getWorkingDays().size());
    }
}
