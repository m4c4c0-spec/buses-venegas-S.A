package cl.venegas.buses_api.application.Query;

import java.time.LocalDate;

public class SearchTripsQuery {
    private String origin;
    private String destination;
    private LocalDate date;

    public SearchTripsQuery(String origin, String destination, LocalDate date) {
        this.origin = origin;
        this.destination = destination;
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDate() {
        return date;
    }
}
