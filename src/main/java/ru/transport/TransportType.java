package ru.transport;

public enum TransportType {
    BUS(40, 50),
    TROLLEYBUS(35, 60),
    TRAM(45, 150);

    private final int averageSpeed;
    private final int capacity;

    TransportType(int averageSpeed, int capacity) {
        this.averageSpeed = averageSpeed;
        this.capacity = capacity;
    }

    public int getAverageSpeed() {
        return averageSpeed;
    }

    public int getCapacity() {
        return capacity;
    }
}