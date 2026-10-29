package ru.transport;

import java.time.Duration;
import java.util.Objects;

/**
 * Участок маршрута: от остановки {@code from} до остановки {@code to}
 * с временем хода. Неизменяемое значение с валидацией в конструкторе.
 */
public class RouteSegment {

    private final Stop from;
    private final Stop to;
    private final Duration travelTime;

    public RouteSegment(Stop from, Stop to, Duration travelTime) {
        this.from = Objects.requireNonNull(from, "Начальная остановка не может быть null");
        this.to = Objects.requireNonNull(to, "Конечная остановка не может быть null");
        this.travelTime = Objects.requireNonNull(travelTime, "Время движения не может быть null");

        if (from.equals(to)) {
            throw new IllegalArgumentException("Остановки должны различаться");
        }
        if (travelTime.isZero() || travelTime.isNegative()) {
            throw new IllegalArgumentException("Время движения должно быть положительным");
        }
    }

    public Stop getFrom()           { return from; }
    public Stop getTo()             { return to; }
    public Duration getTravelTime() { return travelTime; }

    @Override
    public String toString() {
        return from + " -> " + to + " (" + travelTime.toMinutes() + " мин.)";
    }
}