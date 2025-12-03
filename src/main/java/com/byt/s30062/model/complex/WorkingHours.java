package com.byt.s30062.model.complex;

import java.io.Serializable;
import java.util.Objects;

public class WorkingHours implements Serializable {
    private static final long serialVersionUID = 1L;
    private final double startHour;
    private final double finishHour;

    public WorkingHours(double startHour, double finishHour) {
        if (Double.isNaN(startHour)) throw new IllegalArgumentException("startHour cannot be NaN");
        if (Double.isNaN(finishHour)) throw new IllegalArgumentException("finishHour cannot be NaN");
        if (Double.isInfinite(startHour)) throw new IllegalArgumentException("startHour cannot be infinite");
        if (Double.isInfinite(finishHour)) throw new IllegalArgumentException("finishHour cannot be infinite");
        if (startHour < 0 || startHour > 24) throw new IllegalArgumentException("startHour must be between 0 and 24");
        if (finishHour < 0 || finishHour > 24) throw new IllegalArgumentException("finishHour must be between 0 and 24");
        if (startHour >= finishHour) throw new IllegalArgumentException("startHour must be before finishHour");

        this.startHour = startHour;
        this.finishHour = finishHour;
    }

    public double getStartHour() { return startHour; }

    public double getFinishHour() { return finishHour; }

    public double getDuration() {
        return finishHour - startHour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkingHours)) return false;
        WorkingHours wh = (WorkingHours) o;
        return Double.compare(wh.startHour, startHour) == 0 && Double.compare(wh.finishHour, finishHour) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startHour, finishHour);
    }

    @Override
    public String toString() {
        return String.format("%.1f-%.1f", startHour, finishHour);
    }
}
