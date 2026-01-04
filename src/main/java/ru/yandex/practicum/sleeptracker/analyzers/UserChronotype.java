package ru.yandex.practicum.sleeptracker.analyzers;

import ru.yandex.practicum.sleeptracker.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserChronotype implements SleepAnalyzer {
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", "Неизвестно");
        }

        List<SleepingSession> sortedByWake = sessions.stream()
                .sorted(Comparator.comparing(SleepingSession::end))
                .toList();

        LocalDate minWake = sortedByWake.getFirst().end().toLocalDate();
        LocalDate maxWake = sortedByWake.getLast().end().toLocalDate();

        long totalNights = Period.between(minWake, maxWake).getDays() + 1;

        Map<Chronotype, Long> counts = Stream.iterate(minWake, d -> d.plusDays(1))
                .limit(totalNights)
                .filter(night -> sessions.stream().anyMatch(s -> coversNight(s, night)))
                .map(night -> getMainSession(sessions, night))
                .map(this::classify)
                .collect(Collectors.groupingBy(t -> t, Collectors.counting()));

        if (counts.isEmpty()) {
            return new SleepAnalysisResult("Хронотип пользователя", "Неизвестно");
        }

        long max = counts.values().stream().mapToLong(v -> v).max().getAsLong();
        long winners = counts.entrySet().stream().filter(e -> e.getValue() == max).count();

        Chronotype result = winners > 1 ? Chronotype.DOVE
                : counts.entrySet().stream().filter(e -> e.getValue() == max).findFirst().get().getKey();

        return new SleepAnalysisResult("Хронотип пользователя", result.getNameRu());
    }

    private boolean coversNight(SleepingSession s, LocalDate night) {
        LocalDateTime nightStart = night.atStartOfDay();
        LocalDateTime nightCritical = night.atTime(6, 0);
        return s.start().isBefore(nightCritical) && s.end().isAfter(nightStart);
    }

    private SleepingSession getMainSession(List<SleepingSession> sessions, LocalDate night) {
        return sessions.stream()
                .filter(s -> coversNight(s, night))
                .min(Comparator.comparing(SleepingSession::start))
                .orElse(null);
    }

    private Chronotype classify(SleepingSession session) {
        if (session == null) {
            return Chronotype.DOVE;
        }

        LocalTime bedTime = session.start().toLocalTime();
        LocalTime wakeTime = session.end().toLocalTime();

        if (bedTime.isAfter(LocalTime.of(23, 0)) && wakeTime.isAfter(LocalTime.of(9, 0))) {
            return Chronotype.OWL;
        }
        if (bedTime.isBefore(LocalTime.of(22, 0)) && wakeTime.isBefore(LocalTime.of(7, 0))) {
            return Chronotype.LARK;
        }
        return Chronotype.DOVE;
    }
}