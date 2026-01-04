package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class SleepLogReader {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    private SleepLogReader() {
        throw new AssertionError("Utility class - instantiation is not allowed");
    }

    public static List<SleepingSession> read(String path) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(path))) {
            return lines
                    .map(line -> {
                        String[] parts = line.split(";");
                        LocalDateTime start = LocalDateTime.parse(parts[0], FORMATTER);
                        LocalDateTime end = LocalDateTime.parse(parts[1], FORMATTER);
                        Quality quality = Quality.valueOf(parts[2]);
                        return new SleepingSession(start, end, quality);
                    })
                    .toList();
        }
    }
}