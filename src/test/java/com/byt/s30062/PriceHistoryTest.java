package com.byt.s30062;

import com.byt.s30062.model.PriceHistory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PriceHistoryTest {

    @Test
    @DisplayName("Should create price history with valid attributes")
    void testValidPriceHistory() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        assertEquals(99.99, ph.getPrice());
        assertEquals(dateFrom, ph.getDateFrom());
        assertNull(ph.getDateTo());
    }

    @Test
    @DisplayName("Should reject negative price")
    void testNegativePrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(-10.0, dateFrom));
    }

    @Test
    @DisplayName("Should reject zero price")
    void testZeroPrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(0.0, dateFrom));
    }

    @Test
    @DisplayName("Should reject NaN price")
    void testNaNPrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(Double.NaN, dateFrom));
    }

    @Test
    @DisplayName("Should reject infinite price")
    void testInfinitePrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(Double.POSITIVE_INFINITY, dateFrom));
    }

    @Test
    @DisplayName("Should reject null dateFrom")
    void testNullDateFrom() {
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(99.99, null));
    }

    @Test
    @DisplayName("Should reject future dateFrom")
    void testFutureDateFrom() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(99.99, futureDate));
    }

    @Test
    @DisplayName("Should reject dateFrom before 1900")
    void testDateFromBeforeLimit() {
        LocalDate oldDate = LocalDate.of(1899, 12, 31);
        assertThrows(IllegalArgumentException.class,
            () -> new PriceHistory(99.99, oldDate));
    }

    @Test
    @DisplayName("Should set dateTo with validation")
    void testSetDateTo() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 12, 31);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        ph.setDateTo(dateTo);
        assertEquals(dateTo, ph.getDateTo());
    }

    @Test
    @DisplayName("Should reject dateTo before dateFrom")
    void testDateToBeforeDateFrom() {
        LocalDate dateFrom = LocalDate.of(2024, 6, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        LocalDate invalidDateTo = LocalDate.of(2024, 1, 1);
        assertThrows(IllegalArgumentException.class,
            () -> ph.setDateTo(invalidDateTo));
    }

    @Test
    @DisplayName("Should reject dateTo equal to dateFrom")
    void testDateToEqualToDateFrom() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        assertThrows(IllegalArgumentException.class,
            () -> ph.setDateTo(dateFrom));
    }

    @Test
    @DisplayName("Should allow future dateTo for planned price expiration")
    void testFutureDateTo() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        // Future dates are allowed as they represent when price will expire
        LocalDate futureDate = LocalDate.now().plusDays(30);
        ph.setDateTo(futureDate);
        assertEquals(futureDate, ph.getDateTo());
    }

    @Test
    @DisplayName("Should allow clearing dateTo by setting to null")
    void testClearDateTo() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 12, 31);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        ph.setDateTo(dateTo);
        assertEquals(dateTo, ph.getDateTo());
        
        ph.setDateTo(null);
        assertNull(ph.getDateTo());
    }

    @Test
    @DisplayName("Should determine if price is active correctly")
    void testIsActive() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory phNoEnd = new PriceHistory(99.99, dateFrom);
        
        // Price without end date is always active
        assertTrue(phNoEnd.isActive());
        
        // Price with end date in the future (relative to today) is active
        LocalDate futureEnd = LocalDate.now().plusDays(30);
        PriceHistory phActive = new PriceHistory(99.99, LocalDate.now().minusDays(60));
        phActive.setDateTo(futureEnd);
        assertTrue(phActive.isActive());
        
        // Price with end date in the past is not active
        LocalDate pastEnd = LocalDate.now().minusDays(1);
        PriceHistory phExpired = new PriceHistory(99.99, LocalDate.of(2020, 1, 1));
        phExpired.setDateTo(pastEnd);
        assertFalse(phExpired.isActive());
    }

    @Test
    @DisplayName("Should implement equals correctly")
    void testEquals() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph1 = new PriceHistory(99.99, dateFrom);
        PriceHistory ph2 = new PriceHistory(99.99, dateFrom);
        PriceHistory ph3 = new PriceHistory(89.99, dateFrom);
        
        assertEquals(ph1, ph2);
        assertNotEquals(ph1, ph3);
    }

    @Test
    @DisplayName("Should implement equals with dateTo correctly")
    void testEqualsWithDateTo() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 12, 31);
        
        PriceHistory ph1 = new PriceHistory(99.99, dateFrom);
        ph1.setDateTo(dateTo);
        
        PriceHistory ph2 = new PriceHistory(99.99, dateFrom);
        ph2.setDateTo(dateTo);
        
        PriceHistory ph3 = new PriceHistory(99.99, dateFrom);
        
        assertEquals(ph1, ph2);
        assertNotEquals(ph1, ph3);
    }

    @Test
    @DisplayName("Should implement hashCode consistently")
    void testHashCode() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph1 = new PriceHistory(99.99, dateFrom);
        PriceHistory ph2 = new PriceHistory(99.99, dateFrom);
        
        assertEquals(ph1.hashCode(), ph2.hashCode());
    }

    @Test
    @DisplayName("Should generate proper toString for price without end date")
    void testToStringNoEndDate() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        String result = ph.toString();
        
        assertTrue(result.contains("99.99"));
        assertTrue(result.contains("2024-01-01"));
        assertTrue(result.contains("from"));
    }

    @Test
    @DisplayName("Should generate proper toString for price with end date")
    void testToStringWithEndDate() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 12, 31);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        ph.setDateTo(dateTo);
        String result = ph.toString();
        
        assertTrue(result.contains("99.99"));
        assertTrue(result.contains("2024-01-01"));
        assertTrue(result.contains("2024-12-31"));
        assertTrue(result.contains("from"));
        assertTrue(result.contains("to"));
    }

    @Test
    @DisplayName("Should handle very small positive price")
    void testVerySmallPrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(0.01, dateFrom);
        assertEquals(0.01, ph.getPrice());
    }

    @Test
    @DisplayName("Should handle very large price")
    void testVeryLargePrice() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(999999.99, dateFrom);
        assertEquals(999999.99, ph.getPrice());
    }

    @Test
    @DisplayName("Should handle decimal precision in price")
    void testDecimalPrecision() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        assertEquals(99.99, ph.getPrice(), 0.001);
    }

    @Test
    @DisplayName("Should create multiple price histories for same date")
    void testMultiplePriceHistories() {
        LocalDate dateFrom = LocalDate.of(2024, 1, 1);
        PriceHistory ph1 = new PriceHistory(99.99, dateFrom);
        PriceHistory ph2 = new PriceHistory(89.99, dateFrom);
        PriceHistory ph3 = new PriceHistory(79.99, dateFrom);
        
        assertEquals(99.99, ph1.getPrice());
        assertEquals(89.99, ph2.getPrice());
        assertEquals(79.99, ph3.getPrice());
    }

    @Test
    @DisplayName("Should handle boundary date values")
    void testBoundaryDates() {
        // Test with minimum valid date
        LocalDate minDate = LocalDate.of(1900, 1, 1);
        PriceHistory ph1 = new PriceHistory(99.99, minDate);
        assertEquals(minDate, ph1.getDateFrom());
        
        // Test with today's date
        LocalDate today = LocalDate.now();
        PriceHistory ph2 = new PriceHistory(99.99, today);
        assertEquals(today, ph2.getDateFrom());
    }

    @Test
    @DisplayName("Should handle date range spanning multiple years")
    void testLongDateRange() {
        LocalDate dateFrom = LocalDate.of(2020, 1, 1);
        LocalDate dateTo = LocalDate.of(2024, 12, 31);
        PriceHistory ph = new PriceHistory(99.99, dateFrom);
        
        ph.setDateTo(dateTo);
        assertEquals(dateFrom, ph.getDateFrom());
        assertEquals(dateTo, ph.getDateTo());
    }
}
