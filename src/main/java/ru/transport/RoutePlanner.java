
package ru.transport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RoutePlanner {
    private final List<Route> routes = new ArrayList<>();

    public void addRoute(Route route) {
        Objects.requireNonNull(route, "Маршрут не может быть null");

        if (findRouteByNumber(route.getNumber()) != null) {
            throw new RouteException(
                    "Маршрут с номером " + route.getNumber() + " уже существует"
            );
        }

        routes.add(route);
    }

    public List<Route> getRoutes() {
        return List.copyOf(routes);
    }

    public Route findRouteByNumber(String number) {
        Objects.requireNonNull(number, "Номер маршрута не может быть null");

        return routes.stream()
                .filter(route -> route.getNumber().equals(number))
                .findFirst()
                .orElse(null);
    }

    public List<Route> findRoutesByStop(String stopName) {
        Objects.requireNonNull(stopName, "Название остановки не может быть null");

        return routes.stream()
                .filter(route -> route.getStops().stream()
                        .anyMatch(stop -> stop.getName().equalsIgnoreCase(stopName)))
                .toList();
    }

    public List<Route> findRoutesBetweenStops(String from, String to) {
        Objects.requireNonNull(from, "Начальная остановка не может быть null");
        Objects.requireNonNull(to, "Конечная остановка не может быть null");

        return routes.stream()
                .filter(route -> {
                    List<Stop> stops = route.getStops();
                    int fromIndex = -1;
                    int toIndex = -1;

                    for (int i = 0; i < stops.size(); i++) {
                        if (fromIndex == -1
                                && stops.get(i).getName().equalsIgnoreCase(from)) {
                            fromIndex = i;
                        } else if (fromIndex != -1
                                && stops.get(i).getName().equalsIgnoreCase(to)) {
                            toIndex = i;
                            break;
                        }
                    }

                    return fromIndex != -1 && toIndex > fromIndex;
                })
                .toList();
    }
}
