package ru.transport;

import ru.transport.exception.InvalidRouteException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Транспортный маршрут: упорядоченная последовательность остановок
 * и время хода между соседними остановками.
 * Внутренние списки не утекают наружу — возвращаются неизменяемые копии.
 */
public class Route {

    private final String number;
    private final List<Stop> stops;
    private final List<RouteSegment> segments;

    public Route(String number, List<Stop> stops) {
        Objects.requireNonNull(number, "Номер маршрута не может быть null");
        Objects.requireNonNull(stops, "Список остановок не может быть null");

        if (number.isBlank()) {
            throw new InvalidRouteException("Номер маршрута не может быть пустым");
        }
        if (stops.size() < 2) {
            throw new InvalidRouteException("Маршрут должен содержать хотя бы две остановки");
        }
        if (stops.stream().anyMatch(Objects::isNull)) {
            throw new InvalidRouteException("Остановки не могут быть null");
        }
        for (int i = 0; i < stops.size() - 1; i++) {
            if (stops.get(i).equals(stops.get(i + 1))) {
                throw new InvalidRouteException("Две одинаковые остановки подряд: " + stops.get(i));
            }
        }

        this.number = number;
        this.stops = new ArrayList<>(stops);
        this.segments = new ArrayList<>();
    }

    public String getNumber()               { return number; }
    public List<Stop> getStops()            { return List.copyOf(stops); }
    public List<RouteSegment> getSegments() { return List.copyOf(segments); }

    /** Добавить время хода для следующего участка маршрута. */
    public void addSegment(Duration travelTime) {
        if (segments.size() >= stops.size() - 1) {
            throw new InvalidRouteException("Для всех участков маршрута уже задано время");
        }
        Stop from = stops.get(segments.size());
        Stop to   = stops.get(segments.size() + 1);
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

    /** Время прибытия на k-ю остановку (0-based) при отправлении в {@code departure}. */
    public LocalTime getArrivalTimeForIndex(int k, LocalTime departure) {
        Objects.requireNonNull(departure, "Время отправления не может быть null");
        if (!isComplete()) {
            throw new InvalidRouteException("Не для всех участков маршрута задано время");
        }
        if (k < 0 || k >= stops.size()) {
            throw new IndexOutOfBoundsException("Индекс остановки вне маршрута: " + k);
        }
        LocalTime t = departure;
        for (int i = 0; i < k; i++) {
            t = t.plus(segments.get(i).getTravelTime());
        }
        return t;
    }

    public int indexOf(Stop stop) {
        return stops.indexOf(stop);
    }

    /** {@code true}, если {@code from} следует раньше {@code to} в маршруте. */
    public boolean goesFromTo(Stop from, Stop to) {
        int i = indexOf(from);
        int j = indexOf(to);
        return i >= 0 && j > i;
    }

    @Override
    public String toString() {
        return "Маршрут " + number + ": " + stops;
    }
}