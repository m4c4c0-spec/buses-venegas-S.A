package cl.venegas.buses_api.interfaces.web.mapper;

import org.springframework.stereotype.Component;

import cl.venegas.buses_api.application.Command.HoldSeatsCommand;
import cl.venegas.buses_api.interfaces.web.dto.request.HoldRequest;

@Component
public class SeatHoldDTOMapper {

    public HoldSeatsCommand toCommand(HoldRequest request) {
        return new HoldSeatsCommand(
                Long.parseLong(request.tripId()),
                request.seatNumbers(),
                Long.parseLong(request.userId()));
    }
}