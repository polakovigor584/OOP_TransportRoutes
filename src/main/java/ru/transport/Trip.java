package ru.transport;

import ru.transport.exception.InvalidRouteException;

import java.time.LocalTime;
import java.util.Objects;

/**
 * Поездка (рейс): конкретный выезд транспорта по маршруту
 * в определённое время. Содержит ссылку на {@link Vehicle}.
 */
public class Trip {

    private final Route route;
    private final LocalTime departureTime;
    private final TransportType transportType;
    private final Vehicle vehicle;

    public Trip(Route route, LocalTime departureTime,
                TransportType transportType, Vehicle vehicle) {
        this.route = Objects.requireNonNull(route, "Маршрут не может быть null");
        this.departureTime = Objects.requireNonNull(departureTime, "Время отправления не может быть null");
        this.transportType = Objects.requireNonNull(transportType, "Тип транспорта не может быть null");
        this.vehicle = Objects.requireNonNull(vehicle, "Транспорт не может быть null");
    }

    public Trip(Route route, LocalTime departureTime, TransportType transportType) {
        this(route, departureTime, transportType, new Bus("DEFAULT-" + route.getNumber()));
    }

    public Route getRoute()                 { return route; }
    public LocalTime getDepartureTime()     { return departureTime; }
    public TransportType getTransportType() { return transportType; }
    public Vehicle getVehicle()             { return vehicle; }

    public LocalTime getArrivalTime() {
        if (!route.isComplete()) {
            throw new InvalidRouteException("Не для всех участков маршрута задано время");
        }
        return departureTime.plus(route.getTotalTravelTime());
    }

    @Override
    public String toString() {
        return "Поездка по маршруту " + route.getNumber()
                + ", транспорт: " + vehicle.describe()
                + ", отправление: " + departureTime
                + ", прибытие: " + getArrivalTime();
    }
}