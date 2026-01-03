package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.*;

import java.time.Duration;
import java.util.List;

public class MinSleepDuration implements SleepAnalyzer {
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long min = sessions.stream()
                .mapToLong(s -> Duration.between(s.start(), s.end()).toMinutes())
                .min()
                .orElse(0L);
        return new SleepAnalysisResult("Минимальная продолжительность сна (минуты)", min);
    }
}