package cl.venegas.buses_api.interfaces.web.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record UserResponse(

                String id,

                String email,

                String firstName,

                String lastName,

                String phone,

                String role,

                @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime createdAt) {
}
