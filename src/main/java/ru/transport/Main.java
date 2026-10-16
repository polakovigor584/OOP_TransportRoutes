
package ru.transport;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        RoutePlanner planner = new RoutePlanner();
        Schedule schedule = new Schedule();

        for (int i = 1; i <= 10; i++) {
            Stop start = new Stop("Начальная остановка " + i);
            Stop end = new Stop("Конечная остановка " + i);

            Route route = new Route(
                    String.valueOf(i),
                    List.of(start, end)
            );

            route.addSegment(Duration.ofMinutes(10 + i * 2));
            planner.addRoute(route);

            Trip trip = new Trip(
                    route,
                    LocalTime.of(8, 0).plusMinutes(i * 30L),
                    TransportType.values()[(i - 1) % TransportType.values().length]
            );

            schedule.addTrip(trip);
        }

        System.out.println("ГОРОДСКИЕ ТРАНСПОРТНЫЕ МАРШРУТЫ");
        System.out.println("Количество маршрутов: " + planner.getRoutes().size());
        System.out.println("Количество поездок: " + schedule.getTripCount());

        System.out.println("\nВСЕ МАРШРУТЫ");
        for (Route route : planner.getRoutes()) {
            System.out.println(route);
        }

        System.out.println("\nРАСПИСАНИЕ");
        for (Trip trip : schedule.getTrips()) {
            System.out.println(trip);
        }

        System.out.println("\nПОИСК МАРШРУТА № 5");
        System.out.println(planner.findRouteByNumber("5"));

        System.out.println("\nПОИСК ПОЕЗДОК МАРШРУТА № 3");
        for (Trip trip : schedule.getTripsByRoute("3")) {
            System.out.println(trip);
        }

        System.out.println("\nПОИСК ПО ОСТАНОВКЕ");
        for (Route route : planner.findRoutesByStop("Начальная остановка 1")) {
            System.out.println(route);
        }

        System.out.println("\nПОЕЗДКИ С 09:00 ДО 12:00");
        for (Trip trip : schedule.getTripsBetween(
                LocalTime.of(9, 0),
                LocalTime.of(12, 0)
        )) {
            System.out.println(trip);
        }
    }
}
