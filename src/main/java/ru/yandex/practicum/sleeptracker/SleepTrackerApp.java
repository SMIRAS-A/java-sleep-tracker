package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analyzers.*;

import java.io.IOException;
import java.util.List;

public class SleepTrackerApp {

    private static final List<SleepAnalyzer> ANALYZERS = List.of(
            new TotalSleepSessions(),
            new MinSleepDuration(),
            new MaxSleepDuration(),
            new AverageSleepDuration(),
            new BadQualitySessionsCount(),
            new SleeplessNightsCount(),
            new UserChronotype()
    );

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Укажите путь к файлу с логом сна");
            return;
        }

        List<SleepingSession> sessions = SleepLogReader.read(args[0]);

        ANALYZERS.forEach(analyzer -> {
            SleepAnalysisResult result = analyzer.apply(sessions);
            System.out.println(result.description() + ": " + result.value());
        });
    }
}