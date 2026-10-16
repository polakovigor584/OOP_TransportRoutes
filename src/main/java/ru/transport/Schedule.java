
package ru.transport;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
                .filter(trip -> trip.getRoute().getNumber().equals(routeNumber))
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .toList();
    }

    public List<Trip> getTripsBetween(LocalTime start, LocalTime end) {
        Objects.requireNonNull(start, "Начало интервала не может быть null");
        Objects.requireNonNull(end, "Конец интервала не может быть null");

        if (end.isBefore(start)) {
            throw new IllegalArgumentException("Конец интервала не может быть раньше начала");
        }

        return trips.stream()
                .filter(trip -> !trip.getDepartureTime().isBefore(start)
                        && !trip.getDepartureTime().isAfter(end))
                .sorted(Comparator.comparing(Trip::getDepartureTime))
                .toList();
    }

    public int getTripCount() {
        return trips.size();
    }
}
