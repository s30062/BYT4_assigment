package com.byt.s30062;

import com.byt.s30062.model.PartTime;
import com.byt.s30062.model.Staff;
import com.byt.s30062.model.complex.WorkingHours;
import com.byt.s30062.model.enums.DayOfWeek;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PartTimeTest {

    @BeforeEach
    void setup() {
        Staff.clearExtent();
    }

    @AfterEach
    void cleanup() {
        Staff.clearExtent();
        new File("staff_extent.ser").delete();
    }

    @Test
    @DisplayName("Should create part-time employee with valid attributes")
    void testValidPartTime() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime employee = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday),
                                        hours);
        
        assertEquals("David", employee.getFirstName());
        assertEquals("Brown", employee.getLastName());
        assertEquals(25000.0, employee.getBaseSalary());
        assertEquals(3, employee.getWorkingDays().size());
        assertEquals(hours, employee.getWorkingHours());
    }

    @Test
    @DisplayName("Should reject null workingDays")
    void testNullWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false, null, hours));
    }

    @Test
    @DisplayName("Should reject workingDays with less than 2 days")
    void testTooFewWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday), hours));
    }

    @Test
    @DisplayName("Should reject workingDays with more than 5 days")
    void testTooManyWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday,
                                          DayOfWeek.Thursday, DayOfWeek.Friday, DayOfWeek.Saturday), hours));
    }

    @Test
    @DisplayName("Should reject workingDays with null values")
    void testNullDayInWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, null), hours));
    }

    @Test
    @DisplayName("Should reject duplicate days in workingDays")
    void testDuplicateDaysInWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Monday), hours));
    }

    @Test
    @DisplayName("Should reject null workingHours")
    void testNullWorkingHours() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday), null));
    }

    @Test
    @DisplayName("Should maintain encapsulation of workingDays list")
    void testWorkingDaysEncapsulation() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime employee = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                        hours);
        
        var days1 = employee.getWorkingDays();
        var days2 = employee.getWorkingDays();
        
        // Should return different instances
        assertNotSame(days1, days2);
        
        // Try to modify returned list
        assertThrows(UnsupportedOperationException.class, () -> days1.clear());
    }

    @Test
    @DisplayName("Should set workingDays with validation")
    void testSetWorkingDays() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime employee = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                        hours);
        
        employee.setWorkingDays(Arrays.asList(DayOfWeek.Tuesday, DayOfWeek.Thursday, DayOfWeek.Friday));
        assertEquals(3, employee.getWorkingDays().size());
        
        assertThrows(IllegalArgumentException.class,
            () -> employee.setWorkingDays(Arrays.asList(DayOfWeek.Monday)));
    }

    @Test
    @DisplayName("Should set workingHours with validation")
    void testSetWorkingHours() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime employee = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                        hours);
        
        WorkingHours newHours = new WorkingHours(10.0, 14.0);
        employee.setWorkingHours(newHours);
        assertEquals(newHours, employee.getWorkingHours());
        
        assertThrows(IllegalArgumentException.class,
            () -> employee.setWorkingHours(null));
    }

    @Test
    @DisplayName("Should store PartTime in Staff extent (inheritance)")
    void testInheritance() {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate partTimeBirth = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime partTime = new PartTime("David", "Brown", partTimeBirth, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                        hours);
        
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(regularStaff));
        assertTrue(Staff.getExtent().contains(partTime));
        
        assertTrue(Staff.getExtent().stream().anyMatch(s -> s instanceof PartTime));
    }

    @Test
    @DisplayName("Should inherit Staff and Person validations")
    void testInheritedValidations() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime(null, "Brown", birthDate, 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday), hours));
        
        // Negative salary
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", birthDate, -1000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday), hours));
        
        // Future birthDate
        assertThrows(IllegalArgumentException.class,
            () -> new PartTime("David", "Brown", LocalDate.now().plusDays(1), 25000.0, false,
                             Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday), hours));
    }

    @Test
    @DisplayName("Should implement equals correctly with different working configurations")
    void testEqualsWithWorkingConfig() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours1 = new WorkingHours(14.0, 18.0);
        WorkingHours hours2 = new WorkingHours(10.0, 14.0);
        
        PartTime partTime1 = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                         Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                         hours1);
        PartTime partTime2 = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                         Arrays.asList(DayOfWeek.Tuesday, DayOfWeek.Thursday),
                                         hours1);
        PartTime partTime3 = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                         Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                         hours2);
        
        // Different working days - not equal
        assertNotEquals(partTime1, partTime2);
        
        // Different working hours - not equal
        assertNotEquals(partTime1, partTime3);
        
        PartTime partTime4 = new PartTime("David", "Brown", birthDate, 25000.0, false,
                                         Arrays.asList(DayOfWeek.Wednesday, DayOfWeek.Monday),
                                         hours1);
        // Same days (different order) and same hours - equal
        assertEquals(partTime1, partTime4);
    }

    @Test
    @DisplayName("Should handle all valid day combinations (2-5 days)")
    void testVariousDayCombinations() {
        LocalDate birthDate = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        
        // 2 days
        PartTime pt2 = new PartTime("P1", "P1", birthDate, 25000.0, false,
                                   Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday),
                                   hours);
        
        // 3 days
        PartTime pt3 = new PartTime("P2", "P2", birthDate, 25000.0, false,
                                   Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday),
                                   hours);
        
        // 4 days
        PartTime pt4 = new PartTime("P3", "P3", birthDate, 25000.0, false,
                                   Arrays.asList(DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday, DayOfWeek.Thursday),
                                   hours);
        
        // 5 days
        PartTime pt5 = new PartTime("P4", "P4", birthDate, 25000.0, false,
                                   Arrays.asList(DayOfWeek.Monday, DayOfWeek.Tuesday, DayOfWeek.Wednesday,
                                               DayOfWeek.Thursday, DayOfWeek.Friday),
                                   hours);
        
        assertEquals(4, Staff.getExtent().size());
    }

    @Test
    @DisplayName("Should persist PartTime with Staff extent")
    void testPersistence() throws Exception {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate partTimeBirth = LocalDate.of(1995, 6, 18);
        WorkingHours hours = new WorkingHours(14.0, 18.0);
        PartTime partTime = new PartTime("David", "Brown", partTimeBirth, 25000.0, false,
                                        Arrays.asList(DayOfWeek.Monday, DayOfWeek.Wednesday, DayOfWeek.Friday),
                                        hours);
        
        Staff.saveExtent();
        Staff.clearExtent();
        Staff.loadExtent();
        
        assertEquals(2, Staff.getExtent().size());
        
        PartTime loadedPartTime = (PartTime) Staff.getExtent().stream()
            .filter(s -> s instanceof PartTime)
            .findFirst()
            .orElseThrow();
        
        assertEquals("David", loadedPartTime.getFirstName());
        assertEquals(25000.0, loadedPartTime.getBaseSalary());
        assertEquals(3, loadedPartTime.getWorkingDays().size());
        assertTrue(loadedPartTime.getWorkingDays().contains(DayOfWeek.Monday));
        assertTrue(loadedPartTime.getWorkingDays().contains(DayOfWeek.Wednesday));
        assertTrue(loadedPartTime.getWorkingDays().contains(DayOfWeek.Friday));
        assertEquals(14.0, loadedPartTime.getWorkingHours().getStartHour());
        assertEquals(18.0, loadedPartTime.getWorkingHours().getFinishHour());
    }
}
