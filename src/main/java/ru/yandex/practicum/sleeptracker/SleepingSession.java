package ru.yandex.practicum.sleeptracker;

import java.time.LocalDateTime;

public record SleepingSession(LocalDateTime start, LocalDateTime end, Quality quality) {}