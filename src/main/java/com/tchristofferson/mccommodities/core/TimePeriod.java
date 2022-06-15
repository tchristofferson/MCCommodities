package com.tchristofferson.mccommodities.core;

import java.time.LocalDateTime;
import java.time.Period;

public class TimePeriod {

    private final LocalDateTime start;
    private final Period period;

    public TimePeriod(LocalDateTime start, Period period) {
        this.start = start;
        this.period = period;
    }

    public TimePeriod(Period period) {
        this(LocalDateTime.now(), period);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return start.plus(period);
    }

    public Period getPeriod() {
        return period;
    }
}
