package com.byt.s30062.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class PriceHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double price;
    private final LocalDate dateFrom;
    private LocalDate dateTo;

    public PriceHistory(double price, LocalDate dateFrom) {
        if (Double.isNaN(price)) throw new IllegalArgumentException("price cannot be NaN");
        if (Double.isInfinite(price)) throw new IllegalArgumentException("price cannot be infinite");
        if (price <= 0) throw new IllegalArgumentException("price must be positive");
        if (dateFrom == null) throw new IllegalArgumentException("dateFrom cannot be null");
        if (dateFrom.isAfter(LocalDate.now())) throw new IllegalArgumentException("dateFrom cannot be in the future");
        if (dateFrom.isBefore(LocalDate.of(1900, 1, 1))) throw new IllegalArgumentException("dateFrom cannot be before 1900");

        this.price = price;
        this.dateFrom = dateFrom;
        this.dateTo = null; // optional
    }

    public double getPrice() { return price; }

    public LocalDate getDateFrom() { return dateFrom; }

    public LocalDate getDateTo() { return dateTo; }

    public void setDateTo(LocalDate dateTo) {
        if (dateTo != null) {
            if (dateTo.isBefore(dateFrom)) throw new IllegalArgumentException("dateTo cannot be before dateFrom");
            if (dateTo.isEqual(dateFrom)) throw new IllegalArgumentException("dateTo cannot be the same as dateFrom");
        }
        this.dateTo = dateTo;
    }

    public boolean isActive() {
        return dateTo == null || LocalDate.now().isBefore(dateTo) || LocalDate.now().isEqual(dateTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PriceHistory)) return false;
        PriceHistory ph = (PriceHistory) o;
        return Double.compare(ph.price, price) == 0 &&
               dateFrom.isEqual(ph.dateFrom) &&
               Objects.equals(dateTo, ph.dateTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, dateFrom, dateTo);
    }

    @Override
    public String toString() {
        if (dateTo == null) {
            return String.format("%.2f (from %s)", price, dateFrom);
        } else {
            return String.format("%.2f (from %s to %s)", price, dateFrom, dateTo);
        }
    }
}
