
package ru.transport;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    void shouldCalculateTotalTravelTime() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Университет");
        Stop third = new Stop("Вокзал");

        Route route = new Route("10", List.of(first, second, third));
        route.addSegment(Duration.ofMinutes(12));
        route.addSegment(Duration.ofMinutes(18));

        assertEquals(Duration.ofMinutes(30), route.getTotalTravelTime());
    }

    @Test
    void shouldRejectRouteWithOneStop() {
        Stop stop = new Stop("Центр");

        assertThrows(
                IllegalArgumentException.class,
                () -> new Route("10", List.of(stop))
        );
    }

    @Test
    void shouldRejectNonPositiveTravelTime() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Вокзал");

        assertThrows(
                IllegalArgumentException.class,
                () -> new RouteSegment(first, second, Duration.ZERO)
        );
    }

    @Test
    void shouldCalculateArrivalTime() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Вокзал");

        Route route = new Route("10", List.of(first, second));
        route.addSegment(Duration.ofMinutes(25));

        Trip trip = new Trip(
                route,
                LocalTime.of(10, 0),
                TransportType.BUS
        );

        assertEquals(LocalTime.of(10, 25), trip.getArrivalTime());
    }

    @Test
    void shouldSortTripsByDepartureTime() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Вокзал");

        Route route = new Route("10", List.of(first, second));
        route.addSegment(Duration.ofMinutes(20));

        Schedule schedule = new Schedule();

        schedule.addTrip(new Trip(
                route, LocalTime.of(12, 0), TransportType.BUS
        ));
        schedule.addTrip(new Trip(
                route, LocalTime.of(9, 0), TransportType.TRAM
        ));

        assertEquals(
                LocalTime.of(9, 0),
                schedule.getTrips().get(0).getDepartureTime()
        );
    }

    @Test
    void shouldFindRouteBetweenStops() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Университет");
        Stop third = new Stop("Вокзал");

        Route route = new Route("10", List.of(first, second, third));
        RoutePlanner planner = new RoutePlanner();
        planner.addRoute(route);

        assertEquals(
                1,
                planner.findRoutesBetweenStops("Центр", "Вокзал").size()
        );

        assertTrue(
                planner.findRoutesBetweenStops("Вокзал", "Центр").isEmpty()
        );
    }
}
