package ru.transport.exception;

/**
 * Проверяемое исключение предметной области.
 * Используется для ошибок, которые вызывающий код обязан обработать:
 * дубликат маршрута, отсутствие прямого маршрута, остановка не в маршруте.
 */
public class RouteException extends Exception {

    public RouteException(String message) {
        super(message);
    }

    public RouteException(String message, Throwable cause) {
        super(message, cause);
    }
}