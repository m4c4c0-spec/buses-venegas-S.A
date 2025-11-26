package cl.venegas.buses_api.domain.model.valueobject;

import java.util.Objects;

public class SeatNumber {
    private final String value;

    public SeatNumber(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Seat number cannot be empty");
        }
        // Aquí se podrían agregar validaciones de formato (ej. "A1", "B2")
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SeatNumber that = (SeatNumber) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
