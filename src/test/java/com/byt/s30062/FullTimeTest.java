package com.byt.s30062;

import com.byt.s30062.model.FullTime;
import com.byt.s30062.model.Staff;
import com.byt.s30062.model.enums.DayOfWeek;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class FullTimeTest {

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
    @DisplayName("Should create full-time employee with valid attributes")
    void testValidFullTime() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        FullTime employee = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                        Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals("Emma", employee.getFirstName());
        assertEquals("Wilson", employee.getLastName());
        assertEquals(60000.0, employee.getBaseSalary());
        assertEquals(2, employee.getWeekends().size());
        assertTrue(employee.getWeekends().contains(DayOfWeek.Saturday));
        assertTrue(employee.getWeekends().contains(DayOfWeek.Sunday));
    }

    @Test
    @DisplayName("Should reject null weekends")
    void testNullWeekends() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, 60000.0, false, null));
    }

    @Test
    @DisplayName("Should reject weekends with wrong size")
    void testWrongWeekendSize() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        
        // Only 1 day
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday)));
        
        // 3 days
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday, DayOfWeek.Monday)));
    }

    @Test
    @DisplayName("Should reject weekends with null values")
    void testNullDayInWeekends() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, null)));
    }

    @Test
    @DisplayName("Should reject duplicate days in weekends")
    void testDuplicateDaysInWeekends() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Saturday)));
    }

    @Test
    @DisplayName("Should maintain encapsulation of weekends list")
    void testWeekendsEncapsulation() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        FullTime employee = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                        Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        var weekends1 = employee.getWeekends();
        var weekends2 = employee.getWeekends();
        
        // Should return different instances
        assertNotSame(weekends1, weekends2);
        
        // Try to modify returned list
        assertThrows(UnsupportedOperationException.class, () -> weekends1.clear());
    }

    @Test
    @DisplayName("Should set weekends with validation")
    void testSetWeekends() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        FullTime employee = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                        Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        employee.setWeekends(Arrays.asList(DayOfWeek.Friday, DayOfWeek.Saturday));
        assertEquals(2, employee.getWeekends().size());
        assertTrue(employee.getWeekends().contains(DayOfWeek.Friday));
        
        assertThrows(IllegalArgumentException.class, 
            () -> employee.setWeekends(Arrays.asList(DayOfWeek.Monday)));
    }

    @Test
    @DisplayName("Should store FullTime in Staff extent (inheritance)")
    void testInheritance() {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate fullTimeBirth = LocalDate.of(1988, 4, 12);
        FullTime fullTime = new FullTime("Emma", "Wilson", fullTimeBirth, 60000.0, false, 
                                        Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        assertEquals(2, Staff.getExtent().size());
        assertTrue(Staff.getExtent().contains(regularStaff));
        assertTrue(Staff.getExtent().contains(fullTime));
        
        assertTrue(Staff.getExtent().stream().anyMatch(s -> s instanceof FullTime));
    }

    @Test
    @DisplayName("Should inherit Staff and Person validations")
    void testInheritedValidations() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        
        // Null firstName
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime(null, "Wilson", birthDate, 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday)));
        
        // Negative salary
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", birthDate, -1000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday)));
        
        // Future birthDate
        assertThrows(IllegalArgumentException.class,
            () -> new FullTime("Emma", "Wilson", LocalDate.now().plusDays(1), 60000.0, false, 
                             Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday)));
    }

    @Test
    @DisplayName("Should implement equals correctly with different weekends")
    void testEqualsWithWeekends() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        FullTime fullTime1 = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                         Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        FullTime fullTime2 = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                         Arrays.asList(DayOfWeek.Friday, DayOfWeek.Saturday));
        
        // Different weekends - not equal
        assertNotEquals(fullTime1, fullTime2);
        
        FullTime fullTime3 = new FullTime("Emma", "Wilson", birthDate, 60000.0, false, 
                                         Arrays.asList(DayOfWeek.Sunday, DayOfWeek.Saturday));
        // Same days regardless of order - equal
        assertEquals(fullTime1, fullTime3);
    }

    @Test
    @DisplayName("Should handle all valid weekend combinations")
    void testVariousWeekendCombinations() {
        LocalDate birthDate = LocalDate.of(1988, 4, 12);
        
        FullTime ft1 = new FullTime("E1", "W1", birthDate, 50000.0, false, 
                                   Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        FullTime ft2 = new FullTime("E2", "W2", birthDate, 50000.0, false, 
                                   Arrays.asList(DayOfWeek.Friday, DayOfWeek.Saturday));
        FullTime ft3 = new FullTime("E3", "W3", birthDate, 50000.0, false, 
                                   Arrays.asList(DayOfWeek.Monday, DayOfWeek.Tuesday));
        
        assertEquals(3, Staff.getExtent().size());
    }

    @Test
    @DisplayName("Should persist FullTime with Staff extent")
    void testPersistence() throws Exception {
        LocalDate staffBirth = LocalDate.of(1990, 5, 15);
        Staff regularStaff = new Staff("Bob", "Smith", staffBirth, 50000.0, true);
        
        LocalDate fullTimeBirth = LocalDate.of(1988, 4, 12);
        FullTime fullTime = new FullTime("Emma", "Wilson", fullTimeBirth, 60000.0, false, 
                                        Arrays.asList(DayOfWeek.Saturday, DayOfWeek.Sunday));
        
        Staff.saveExtent();
        Staff.clearExtent();
        Staff.loadExtent();
        
        assertEquals(2, Staff.getExtent().size());
        
        FullTime loadedFullTime = (FullTime) Staff.getExtent().stream()
            .filter(s -> s instanceof FullTime)
            .findFirst()
            .orElseThrow();
        
        assertEquals("Emma", loadedFullTime.getFirstName());
        assertEquals(60000.0, loadedFullTime.getBaseSalary());
        assertEquals(2, loadedFullTime.getWeekends().size());
        assertTrue(loadedFullTime.getWeekends().contains(DayOfWeek.Saturday));
        assertTrue(loadedFullTime.getWeekends().contains(DayOfWeek.Sunday));
    }
}
