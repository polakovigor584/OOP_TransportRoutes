
package ru.transport;

import java.time.LocalTime;
import java.util.Objects;

public class Trip {
    private final Route route;
    private final LocalTime departureTime;
    private final TransportType transportType;

    public Trip(Route route, LocalTime departureTime, TransportType transportType) {
        this.route = Objects.requireNonNull(route, "Маршрут не может быть null");
        this.departureTime = Objects.requireNonNull(departureTime, "Время отправления не может быть null");
        this.transportType = Objects.requireNonNull(transportType, "Тип транспорта не может быть null");
    }

    public Route getRoute() {
        return route;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    public LocalTime getArrivalTime() {
        if (!route.isComplete()) {
            throw new RouteException("Не для всех участков маршрута задано время");
        }

        return departureTime.plus(route.getTotalTravelTime());
    }

    @Override
    public String toString() {
        return "Поездка по маршруту " + route.getNumber()
                + ", транспорт: " + transportType
                + ", отправление: " + departureTime
                + ", прибытие: " + getArrivalTime();
    }
}
