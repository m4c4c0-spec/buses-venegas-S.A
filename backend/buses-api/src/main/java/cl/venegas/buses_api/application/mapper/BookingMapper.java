package cl.venegas.buses_api.application.mapper;

import cl.venegas.buses_api.domain.model.valueobject.SeatNumber;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public List<SeatNumber> toSeatNumbers(List<String> seats) {
        if (seats == null) {
            return List.of();
        }
        return seats.stream()
                .map(SeatNumber::new)
                .collect(Collectors.toList());
    }
}
