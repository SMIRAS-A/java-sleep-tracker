package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analyzers.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SleepTrackerAppTest {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private static LocalDateTime dt(String s) {
        return LocalDateTime.parse(s, DTF);
    }

    private final List<SleepingSession> empty = List.of();

    private final List<SleepingSession> sample = List.of(
            new SleepingSession(dt("01.10.25 22:15"), dt("02.10.25 08:00"), Quality.GOOD),
            new SleepingSession(dt("02.10.25 23:00"), dt("03.10.25 08:00"), Quality.NORMAL),
            new SleepingSession(dt("03.10.25 14:30"), dt("03.10.25 15:20"), Quality.NORMAL),
            new SleepingSession(dt("03.10.25 23:30"), dt("04.10.25 06:20"), Quality.BAD)
    );

    @Test
    void totalSessions() {
        assertEquals(0, new TotalSleepSessions().apply(empty).value());
        assertEquals(4, new TotalSleepSessions().apply(sample).value());
    }

    @Test
    void minDuration() {
        assertEquals(0L, new MinSleepDuration().apply(empty).value());
        assertEquals(50L, new MinSleepDuration().apply(sample).value());
    }

    @Test
    void maxDuration() {
        assertEquals(0L, new MaxSleepDuration().apply(empty).value());
        assertEquals(585L, new MaxSleepDuration().apply(sample).value());
    }

    @Test
    void averageDuration() {
        assertEquals(0L, new AverageSleepDuration().apply(empty).value());
        long avg = Math.round(sample.stream()
                .mapToLong(s -> java.time.Duration.between(s.start(), s.end()).toMinutes())
                .average()
                .getAsDouble());
        assertEquals(avg, new AverageSleepDuration().apply(sample).value());
    }

    @Test
    void badQuality() {
        assertEquals(0L, new BadQualitySessionsCount().apply(empty).value());
        assertEquals(1L, new BadQualitySessionsCount().apply(sample).value());
    }

    @Test
    void sleeplessNights_empty() {
        assertEquals(0L, new SleeplessNightsCount().apply(empty).value());
    }

    @Test
    void sleeplessNights_noSleepless() {
        assertEquals(0L, new SleeplessNightsCount().apply(sample).value());
    }

    @Test
    void sleeplessNights_oneMissing() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 01:00"), dt("01.10.25 07:00"), Quality.GOOD),
                new SleepingSession(dt("03.10.25 01:00"), dt("03.10.25 07:00"), Quality.GOOD)
        );
        assertEquals(1L, new SleeplessNightsCount().apply(sessions).value());
    }

    @Test
    void sleeplessNights_dayOnly_firstAfterNoon() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 14:00"), dt("01.10.25 16:00"), Quality.NORMAL)
        );
        assertEquals(1L, new SleeplessNightsCount().apply(sessions).value());
    }

    @Test
    void sleeplessNights_lateWakeEarlyBed() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 23:00"), dt("02.10.25 05:00"), Quality.NORMAL),
                new SleepingSession(dt("02.10.25 02:00"), dt("02.10.25 08:00"), Quality.NORMAL)
        );
        assertEquals(0L, new SleeplessNightsCount().apply(sessions).value());
    }

    @Test
    void chronotype_owl() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 23:30"), dt("02.10.25 10:00"), Quality.GOOD),
                new SleepingSession(dt("02.10.25 00:10"), dt("02.10.25 09:30"), Quality.GOOD)
        );
        assertEquals("Сова", new UserChronotype().apply(sessions).value());
    }

    @Test
    void chronotype_lark() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 21:00"), dt("02.10.25 06:30"), Quality.GOOD),
                new SleepingSession(dt("02.10.25 20:45"), dt("02.10.25 06:00"), Quality.GOOD)
        );
        assertEquals("Жаворонок", new UserChronotype().apply(sessions).value());
    }

    @Test
    void chronotype_dove_majority() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 22:30"), dt("02.10.25 07:30"), Quality.GOOD),
                new SleepingSession(dt("02.10.25 23:10"), dt("03.10.25 08:10"), Quality.GOOD),
                new SleepingSession(dt("03.10.25 22:00"), dt("04.10.25 07:00"), Quality.GOOD)
        );
        assertEquals("Голубь", new UserChronotype().apply(sessions).value());
    }

    @Test
    void chronotype_tie_fallsToDove() {
        var sessions = List.of(
                new SleepingSession(dt("01.10.25 23:30"), dt("02.10.25 10:00"), Quality.GOOD),
                new SleepingSession(dt("02.10.25 21:00"), dt("03.10.25 06:00"), Quality.GOOD)
        );
        assertEquals("Голубь", new UserChronotype().apply(sessions).value());
    }
}