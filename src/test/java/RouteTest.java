import org.junit.jupiter.api.Test;
import ru.transport.*;

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
        assertThrows(
                IllegalArgumentException.class,
                () -> new Route("10", List.of(new Stop("Центр")))
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
        Stop route = new Stop(
                "10",
                List.of(new Stop("Центр"), new Stop("Вокзал"))
        );
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
        Stop route = createRoute("10");
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
        Stop route = new Stop(
                "10",
                List.of(
                        new Stop("Центр"),
                        new Stop("Университет"),
                        new Stop("Вокзал")
                )
        );

        Stop planner = new Stop();
        planner.addRoute(route);

        assertEquals(
                1,
                planner.findRoutesBetweenStops("Центр", "Вокзал").size()
        );
        assertTrue(
                planner.findRoutesBetweenStops("Вокзал", "Центр").isEmpty()
        );
    }

    @Test
    void shouldRejectDuplicateRouteNumber() {
        RoutePlanner planner = new RoutePlanner();
        planner.addRoute(createRoute("10"));

        assertThrows(
                RouteException.class,
                () -> planner.addRoute(createRoute("10"))
        );
    }

    @Test
    void shouldFindRoutesByStopIgnoringCase() {
        RoutePlanner planner = new RoutePlanner();
        planner.addRoute(createRoute("10"));

        assertEquals(1, planner.findRoutesByStop("центр").size());
    }

    @Test
    void shouldFilterTripsByTimeInterval() {
        Route route = createRoute("10");
        Schedule schedule = new Schedule();

        schedule.addTrip(new Trip(
                route, LocalTime.of(8, 0), TransportType.BUS
        ));
        schedule.addTrip(new Trip(
                route, LocalTime.of(10, 0), TransportType.TRAM
        ));
        schedule.addTrip(new Trip(
                route, LocalTime.of(12, 0), TransportType.BUS
        ));

        assertEquals(
                1,
                schedule.getTripsBetween(
                        LocalTime.of(9, 0),
                        LocalTime.of(11, 0)
                ).size()
        );
    }

    @Test
    void shouldRejectInvalidTimeInterval() {
        Schedule schedule = new Schedule();

        assertThrows(
                IllegalArgumentException.class,
                () -> schedule.getTripsBetween(
                        LocalTime.of(12, 0),
                        LocalTime.of(9, 0)
                )
        );
    }

    @Test
    void shouldRejectArrivalCalculationForIncompleteRoute() {
        Route route = new Route(
                "10",
                List.of(
                        new Stop("Центр"),
                        new Stop("Университет"),
                        new Stop("Вокзал")
                )
        );

        Trip trip = new Trip(
                route, LocalTime.of(10, 0), TransportType.BUS
        );

        assertThrows(RouteException.class, trip::getArrivalTime);
    }

    @Test
    void shouldCompareStopsByName() {
        Stop first = new Stop("Центр");
        Stop second = new Stop("Центр");

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void shouldRejectSegmentWithSameStops() {
        Stop stop = new Stop("Центр");

        assertThrows(
                IllegalArgumentException.class,
                () -> new RouteSegment(
                        stop, new Stop("Центр"), Duration.ofMinutes(10)
                )
        );
    }

    @Test
    void shouldRejectBlankStopName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Stop(" ")
        );
    }

    @Test
    void shouldRejectBlankRouteNumber() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Route(" ", List.of(
                        new Stop("Центр"),
                        new Stop("Вокзал")
                ))
        );
    }

    private Route createRoute(String number) {
        Route route = new Route(
                number,
                List.of(new Stop("Центр"), new Stop("Вокзал"))
        );
        route.addSegment(Duration.ofMinutes(20));
        return route;
    }
}
