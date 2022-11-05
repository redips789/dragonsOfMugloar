package com.dragons.shared;

import javax.validation.ClockProvider;
import java.time.*;

public class TimeMachine {

    private static Clock clock = Clock.systemUTC();
    private static final ZoneId zoneId = ZoneId.of("UTC");

    public static LocalDate now() {
        return LocalDateTime.now(getClock()).toLocalDate();
    }

    public static LocalDate infiniteFuture() {
        return LocalDate.of(9999, 12, 31);
    }

    public static LocalDate infinitePast() {
        return LocalDate.of(1970, 1, 1);
    }

    public static LocalDateTime nowLocalDateAndTime() {
        return LocalDateTime.now(getClock());
    }

    public static void useFixedClockAt(LocalDateTime date) {
        clock = Clock.fixed(date.atOffset(ZoneOffset.UTC).toInstant(), zoneId);
    }

    private static Clock getClock() {
        return clock;
    }

    public static ClockProvider getClockProvider() {
        return () -> clock;
    }

    // If period contains more than 0 days, adds additional month
    public static Integer getNumberOfMonthsBetweenDates(LocalDate startDate, LocalDate endDate) {
        var period = Period.between(startDate, endDate);

        return period.getYears() * 12 + period.getMonths() + (period.getDays() > 0 ? 1 : 0);
    }
}
