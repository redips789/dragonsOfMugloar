package com.dragons.shared;

import java.time.Clock;
import java.time.LocalDateTime;

public class TimeMachine {

    private static Clock clock = Clock.systemUTC();

    public static LocalDateTime nowLocalDateAndTime() {
        return LocalDateTime.now(getClock());
    }

    private static Clock getClock() {
        return clock;
    }
}
