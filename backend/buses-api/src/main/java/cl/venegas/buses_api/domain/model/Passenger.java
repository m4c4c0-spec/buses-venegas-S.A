package cl.venegas.buses_api.domain.model;

import cl.venegas.buses_api.domain.model.valueobject.Email;

public record Passenger(Long id, String firstName, String lastName, String documentType, String documentNumber,
                Email email, String phone) {

}
