package ru.transport;

import ru.transport.exception.NoDirectRouteException;
import ru.transport.exception.RouteException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Планировщик маршрутов. Хранит маршруты, ищет по номеру и остановке,
 * находит прямые маршруты между остановками и считает время в пути.
 */
public class RoutePlanner {

    private final List<Route> routes = new ArrayList<>();

    public void addRoute(Route route) throws RouteException {
        Objects.requireNonNull(route, "Маршрут не может быть null");
        if (findRouteByNumber(route.getNumber()) != null) {
            throw new RouteException(
                    "Маршрут с номером " + route.getNumber() + " уже существует");
        }
        routes.add(route);
    }

    public List<Route> getRoutes() {
        return List.copyOf(routes);
    }

    public Route findRouteByNumber(String number) {
        Objects.requireNonNull(number, "Номер маршрута не может быть null");
        return routes.stream()
                .filter(r -> r.getNumber().equals(number))
                .findFirst()
                .orElse(null);
    }

    public List<Route> findRoutesByStop(String stopName) {
        Objects.requireNonNull(stopName, "Название остановки не может быть null");
        return routes.stream()
                .filter(r -> r.getStops().stream()
                        .anyMatch(s -> s.getName().equalsIgnoreCase(stopName)))
                .toList();
    }

    /**
     * Маршруты, связывающие две остановки без пересадки (в правильном порядке).
     * @throws NoDirectRouteException если таких маршрутов нет
     */
    public List<Route> findDirectRoutes(Stop from, Stop to) throws NoDirectRouteException {
        Objects.requireNonNull(from, "Начальная остановка не может быть null");
        Objects.requireNonNull(to, "Конечная остановка не может быть null");
        if (from.equals(to)) {
            throw new IllegalArgumentException("Остановки отправления и прибытия совпадают");
        }

        List<Route> result = routes.stream()
                .filter(r -> r.goesFromTo(from, to))
                .toList();

        if (result.isEmpty()) {
            throw new NoDirectRouteException(from.getName(), to.getName());
        }
        return result;
    }

    /**
     * Время в пути от {@code from} до {@code to} по указанному маршруту.
     * @throws RouteException если остановки не входят в маршрут или порядок неверен
     */
    public Duration getTravelTime(Route route, Stop from, Stop to) throws RouteException {
        Objects.requireNonNull(route, "Маршрут не может быть null");
        Objects.requireNonNull(from, "Начальная остановка не может быть null");
        Objects.requireNonNull(to, "Конечная остановка не может быть null");

        int i = route.indexOf(from);
        int j = route.indexOf(to);
        if (i < 0 || j < 0) {
            throw new RouteException("Остановка отсутствует в маршруте " + route.getNumber());
        }
        if (j <= i) {
            throw new RouteException("Остановка " + to.getName()
                    + " не следует после " + from.getName()
                    + " в маршруте " + route.getNumber());
        }
        Duration total = Duration.ZERO;
        for (int k = i; k < j; k++) {
            total = total.plus(route.getSegments().get(k).getTravelTime());
        }
        return total;
    }
}