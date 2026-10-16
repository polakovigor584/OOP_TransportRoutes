
package ru.transport;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Route {
    private final String number;
    private final List<Stop> stops;
    private final List<RouteSegment> segments;

    public Route(String number, List<Stop> stops) {
        Objects.requireNonNull(number, "Номер маршрута не может быть null");
        Objects.requireNonNull(stops, "Список остановок не может быть null");

        if (number.isBlank()) {
            throw new IllegalArgumentException("Номер маршрута не может быть пустым");
        }

        if (stops.size() < 2) {
            throw new IllegalArgumentException("Маршрут должен содержать хотя бы две остановки");
        }

        if (stops.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Остановки не могут быть null");
        }

        this.number = number;
        this.stops = new ArrayList<>(stops);
        this.segments = new ArrayList<>();
    }

    public String getNumber() {
        return number;
    }

    public List<Stop> getStops() {
        return List.copyOf(stops);
    }

    public List<RouteSegment> getSegments() {
        return List.copyOf(segments);
    }

    public void addSegment(Duration travelTime) {
        if (segments.size() >= stops.size() - 1) {
            throw new RouteException("Для всех участков маршрута уже задано время");
        }

        Stop from = stops.get(segments.size());
        Stop to = stops.get(segments.size() + 1);

        segments.add(new RouteSegment(from, to, travelTime));
    }

    public boolean isComplete() {
        return segments.size() == stops.size() - 1;
    }

    public Duration getTotalTravelTime() {
        return segments.stream()
                .map(RouteSegment::getTravelTime)
                .reduce(Duration.ZERO, Duration::plus);
    }

    @Override
    public String toString() {
        return "Маршрут " + number + ": " + stops;
    }
}
