package com.byt.s30062;

import com.byt.s30062.model.complex.WorkingHours;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class WorkingHoursTest {

    @Test
    @DisplayName("Should create working hours with valid attributes")
    void testValidWorkingHours() {
        WorkingHours wh = new WorkingHours(9.0, 17.0);
        
        assertEquals(9.0, wh.getStartHour());
        assertEquals(17.0, wh.getFinishHour());
        assertEquals(8.0, wh.getDuration());
    }

    @Test
    @DisplayName("Should reject null startHour (NaN)")
    void testNaNStartHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(Double.NaN, 17.0));
    }

    @Test
    @DisplayName("Should reject null finishHour (NaN)")
    void testNaNFinishHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(9.0, Double.NaN));
    }

    @Test
    @DisplayName("Should reject infinite startHour")
    void testInfiniteStartHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(Double.POSITIVE_INFINITY, 17.0));
    }

    @Test
    @DisplayName("Should reject infinite finishHour")
    void testInfiniteFinishHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(9.0, Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("Should reject negative startHour")
    void testNegativeStartHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(-1.0, 17.0));
    }

    @Test
    @DisplayName("Should reject startHour > 24")
    void testStartHourExceedsLimit() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(25.0, 26.0));
    }

    @Test
    @DisplayName("Should reject negative finishHour")
    void testNegativeFinishHour() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(9.0, -1.0));
    }

    @Test
    @DisplayName("Should reject finishHour > 24")
    void testFinishHourExceedsLimit() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(9.0, 25.0));
    }

    @Test
    @DisplayName("Should reject when startHour equals finishHour")
    void testEqualHours() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(9.0, 9.0));
    }

    @Test
    @DisplayName("Should reject when startHour is after finishHour")
    void testStartAfterFinish() {
        assertThrows(IllegalArgumentException.class,
            () -> new WorkingHours(17.0, 9.0));
    }

    @Test
    @DisplayName("Should calculate duration correctly")
    void testDuration() {
        WorkingHours wh1 = new WorkingHours(9.0, 17.0);
        assertEquals(8.0, wh1.getDuration());
        
        WorkingHours wh2 = new WorkingHours(8.5, 16.5);
        assertEquals(8.0, wh2.getDuration());
        
        WorkingHours wh3 = new WorkingHours(0.0, 24.0);
        assertEquals(24.0, wh3.getDuration());
    }

    @Test
    @DisplayName("Should handle decimal hours")
    void testDecimalHours() {
        WorkingHours wh = new WorkingHours(9.5, 17.75);
        assertEquals(9.5, wh.getStartHour());
        assertEquals(17.75, wh.getFinishHour());
        assertEquals(8.25, wh.getDuration());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        WorkingHours wh1 = new WorkingHours(9.0, 17.0);
        WorkingHours wh2 = new WorkingHours(9.0, 17.0);
        WorkingHours wh3 = new WorkingHours(8.0, 16.0);
        
        assertEquals(wh1, wh2);
        assertNotEquals(wh1, wh3);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        WorkingHours wh1 = new WorkingHours(9.0, 17.0);
        WorkingHours wh2 = new WorkingHours(9.0, 17.0);
        
        assertEquals(wh1.hashCode(), wh2.hashCode());
    }

    @Test
    @DisplayName("Should generate proper toString")
    void testToString() {
        WorkingHours wh = new WorkingHours(9.0, 17.0);
        String result = wh.toString();
        
        assertTrue(result.contains("9.0"));
        assertTrue(result.contains("17.0"));
    }

    @Test
    @DisplayName("Should handle boundary values")
    void testBoundaryValues() {
        WorkingHours wh1 = new WorkingHours(0.0, 1.0);
        assertEquals(0.0, wh1.getStartHour());
        assertEquals(1.0, wh1.getFinishHour());
        
        WorkingHours wh2 = new WorkingHours(23.0, 24.0);
        assertEquals(23.0, wh2.getStartHour());
        assertEquals(24.0, wh2.getFinishHour());
    }
}
