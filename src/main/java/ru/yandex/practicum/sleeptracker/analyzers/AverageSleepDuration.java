package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.*;

import java.time.Duration;
import java.util.List;

public class AverageSleepDuration implements SleepAnalyzer {
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avg = sessions.stream()
                .mapToLong(s -> Duration.between(s.start(), s.end()).toMinutes())
                .average()
                .orElse(0.0);
        return new SleepAnalysisResult("Средняя продолжительность сна (минуты)", Math.round(avg));
    }
}