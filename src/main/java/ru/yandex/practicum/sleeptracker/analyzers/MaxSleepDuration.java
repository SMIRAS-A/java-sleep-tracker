package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.*;

import java.time.Duration;
import java.util.List;

public class MaxSleepDuration implements SleepAnalyzer {
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long max = sessions.stream()
                .mapToLong(s -> Duration.between(s.start(), s.end()).toMinutes())
                .max()
                .orElse(0L);
        return new SleepAnalysisResult("Максимальная продолжительность сна (минуты)", max);
    }
}