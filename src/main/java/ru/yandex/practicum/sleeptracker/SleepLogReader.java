package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SleepLogReader {
    public static List<SleepingSession> read(String path) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        return Files.lines(Paths.get(path))
                .map(line -> {
                    String[] parts = line.split(";");
                    LocalDateTime start = LocalDateTime.parse(parts[0], formatter);
                    LocalDateTime end = LocalDateTime.parse(parts[1], formatter);
                    Quality quality = Quality.valueOf(parts[2]);
                    return new SleepingSession(start, end, quality);
                })
                .toList();
    }
}