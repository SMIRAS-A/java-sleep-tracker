package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;

public class SleeplessNightsCount implements SleepAnalyzer {
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        List<SleepingSession> sorted = sessions.stream()
                .sorted((a, b) -> a.start().compareTo(b.start()))
                .toList();

        LocalDate minWake = sorted.stream()
                .map(s -> s.end().toLocalDate())
                .min(LocalDate::compareTo)
                .get();

        LocalDate maxWake = sorted.stream()
                .map(s -> s.end().toLocalDate())
                .max(LocalDate::compareTo)
                .get();

        long totalNights = Period.between(minWake, maxWake).getDays() + 1;

        long sleptNights = Stream.iterate(minWake, d -> d.plusDays(1))
                .limit(totalNights)
                .filter(night -> sorted.stream().anyMatch(s -> coversNight(s, night)))
                .count();

        return new SleepAnalysisResult("Количество бессонных ночей", totalNights - sleptNights);
    }

    private boolean coversNight(SleepingSession s, LocalDate night) {
        LocalDateTime nightStart = night.atStartOfDay();
        LocalDateTime nightCritical = night.atTime(6, 0);
        return s.start().isBefore(nightCritical) && s.end().isAfter(nightStart);
    }
}