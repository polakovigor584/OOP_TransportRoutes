package ru.transport;

import java.util.Objects;

public class Stop {
    private final String name;

    public Stop(String name) {
        this.name = Objects.requireNonNull(name, "Название остановки не может быть null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Название остановки не может быть пустым");
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Stop other)) {
            return false;
        }

        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}