package ru.transport;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Расписание поездок. Хранит рейсы и реализует операции:
 * расписание по остановке, прибытие на k-ю остановку,
 * фактический интервал движения, выборка по маршруту и интервалу.
 */
public class Schedule {

    private final List<Trip> trips = new ArrayList<>();

    public void addTrip(Trip trip) {
        trips.add(Objects.requireNonNull(trip, "Поездка не может быть null"));
    }

    public List<Trip> getTrips() {
        return trips.stream()
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .toList();
    }

    public List<Trip> getTripsByRoute(String routeNumber) {
        Objects.requireNonNull(routeNumber, "Номер маршрута не может быть null");
        return trips.stream()
                .filter(t -> t.getRoute().getNumber().equals(routeNumber))
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .toList();
    }

    /** Расписание по остановке: пары «поездка → время прибытия», по возрастанию времени. */
    public List<Map.Entry<Trip, LocalTime>> scheduleAt(Stop stop) {
        Objects.requireNonNull(stop, "Остановка не может быть null");

        List<Map.Entry<Trip, LocalTime>> result = new ArrayList<>();
        for (Trip t : trips) {
            int idx = t.getRoute().indexOf(stop);
            if (idx >= 0 && t.getRoute().isComplete()) {
                result.add(Map.entry(t,
                        t.getRoute().getArrivalTimeForIndex(idx, t.getDepartureTime())));
            }
        }
        result.sort(Comparator.comparing(Map.Entry::getValue));
        return List.copyOf(result);
    }

    public LocalTime arrivalAt(Trip trip, int k) {
        Objects.requireNonNull(trip, "Поездка не может быть null");
        return trip.getRoute().getArrivalTimeForIndex(k, trip.getDepartureTime());
    }

    /** Средний интервал между последовательными отправлениями по маршруту. */
    public Duration actualInterval(Route route) {
        Objects.requireNonNull(route, "Маршрут не может быть null");

        List<LocalTime> departures = trips.stream()
                .filter(t -> t.getRoute().equals(route))
                .map(Trip::getDepartureTime)
                .sorted()
                .toList();

        if (departures.size() < 2) return Duration.ZERO;

        long total = 0;
        for (int i = 1; i < departures.size(); i++) {
            total += Duration.between(departures.get(i - 1), departures.get(i)).toMinutes();
        }
        return Duration.ofMinutes(total / (departures.size() - 1));
    }

    public List<Trip> getTripsBetween(LocalTime start, LocalTime end) {
        Objects.requireNonNull(start, "Начало интервала не может быть null");
        Objects.requireNonNull(end, "Конец интервала не может быть null");
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Конец интервала не может быть раньше начала");
        }
        return trips.stream()
                .filter(t -> !t.getDepartureTime().isBefore(start)
                        && !t.getDepartureTime().isAfter(end))
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .toList();
    }

    public int getTripCount() {
        return trips.size();
    }
}