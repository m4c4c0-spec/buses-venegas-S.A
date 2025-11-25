package cl.venegas.buses_api.domain.model.valueobject;

import java.util.Objects;

public class Password {
    private final String value;

    public Password(String value) {
        if (value == null || value.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
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
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "******"; // No mostrar password en logs/toString
    }
}
