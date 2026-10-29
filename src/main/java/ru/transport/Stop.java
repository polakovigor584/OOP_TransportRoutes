package ru.transport;

import java.util.Objects;

/**
 * Остановка городского транспорта.
 * Ключ равенства — название остановки. Класс неизменяемый.
 */
public class Stop {

    private final String name;

    public Stop(String name) {
        Objects.requireNonNull(name, "Название остановки не может быть null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Название остановки не может быть пустым");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Stop other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}