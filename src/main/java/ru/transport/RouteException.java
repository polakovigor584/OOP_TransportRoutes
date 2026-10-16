
package ru.transport;

public class RouteException extends RuntimeException {
    public RouteException(String message) {
        super(message);
    }

    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }
}
