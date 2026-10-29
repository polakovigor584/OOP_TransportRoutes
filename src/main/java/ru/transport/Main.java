package ru.transport;

import ru.transport.exception.InvalidRouteException;
import ru.transport.exception.NoDirectRouteException;
import ru.transport.exception.RouteException;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * Демонстрационный сценарий: наполняет модель 60 объектами
 * (10 остановок, 10 маршрутов, 20 поездок, 20 транспортных единиц)
 * и показывает все операции, включая обработку обоих видов исключений.
 */
public class Main {

    public static void main(String[] args) {
        try {
            runDemo();
        } catch (Exception ex) {
            System.err.println("Непредвиденная ошибка: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void runDemo() throws RouteException {
        RoutePlanner planner = new RoutePlanner();
        Schedule schedule = new Schedule();

        Stop[] stops = new Stop[10];
        for (int i = 0; i < 10; i++) {
            stops[i] = new Stop("Остановка " + (i + 1));
        }

        for (int i = 1; i <= 10; i++) {
            Route route = new Route(
                    String.valueOf(i),
                    List.of(stops[(i - 1) % 10], stops[i % 10], stops[(i + 1) % 10])
            );
            route.addSegment(Duration.ofMinutes(10 + i * 2));
            route.addSegment(Duration.ofMinutes(8 + i));
            planner.addRoute(route);

            TransportType tt = TransportType.values()[(i - 1) % TransportType.values().length];
            Vehicle v1 = switch (tt) {
                case BUS        -> new Bus("A" + (100 + i));
                case TRAM       -> new Tram("T" + (200 + i), 2);
                case TROLLEYBUS -> new Trolleybus("TB" + (300 + i));
            };
            Vehicle v2 = switch (tt) {
                case BUS        -> new Bus("A" + (200 + i));
                case TRAM       -> new Tram("T" + (300 + i), 1);
                case TROLLEYBUS -> new Trolleybus("TB" + (400 + i));
            };
            schedule.addTrip(new Trip(route, LocalTime.of(8, 0).plusMinutes(i * 15L), tt, v1));
            schedule.addTrip(new Trip(route, LocalTime.of(9, 30).plusMinutes(i * 15L), tt, v2));
        }

        System.out.println("=== ГОРОДСКИЕ ТРАНСПОРТНЫЕ МАРШРУТЫ ===");
        System.out.println("Маршрутов: " + planner.getRoutes().size()
                + ", поездок: " + schedule.getTripCount());
        for (Route r : planner.getRoutes()) System.out.println("  " + r);

        System.out.println("\n=== Расписание по остановке «" + stops[0].getName() + "» ===");
        for (Map.Entry<Trip, LocalTime> e : schedule.scheduleAt(stops[0])) {
            System.out.printf("  %s — прибытие %s%n",
                    e.getKey().getVehicle().describe(), e.getValue());
        }

        System.out.println("\n=== Прибытие на k-ю остановку ===");
        Trip first = schedule.getTrips().get(0);
        System.out.println("  " + first);
        for (int k = 0; k < first.getRoute().getStops().size(); k++) {
            System.out.printf("    #%d %s → %s%n",
                    k, first.getRoute().getStops().get(k).getName(),
                    schedule.arrivalAt(first, k));
        }

        System.out.println("\n=== Фактический интервал движения ===");
        for (Route r : planner.getRoutes()) {
            System.out.printf("  Маршрут %s: %d мин%n",
                    r.getNumber(), schedule.actualInterval(r).toMinutes());
        }

        System.out.println("\n=== Прямые маршруты между остановками ===");
        try {
            List<Route> direct = planner.findDirectRoutes(stops[0], stops[1]);
            direct.forEach(r -> System.out.println("  " + r));
        } catch (NoDirectRouteException ex) {
            System.out.println("  " + ex.getMessage());
        }

        try {
            planner.findDirectRoutes(stops[0], stops[9]);
        } catch (NoDirectRouteException ex) {
            System.out.println("  Ожидаемое исключение: " + ex.getMessage());
        }

        System.out.println("\n=== Время в пути между остановками ===");
        try {
            Route r1 = planner.findRouteByNumber("1");
            Duration d = planner.getTravelTime(r1, stops[0], stops[2]);
            System.out.println("  Маршрут 1: " + stops[0].getName() + " → "
                    + stops[2].getName() + " = " + d.toMinutes() + " мин");
        } catch (RouteException ex) {
            System.out.println("  " + ex.getMessage());
        }

        System.out.println("\n=== Обработка InvalidRouteException ===");
        try {
            new Route("X", List.of(stops[0]));
        } catch (InvalidRouteException ex) {
            System.out.println("  Поймано: " + ex.getMessage());
        }
    }
}