package cl.venegas.buses_api.presentation.http.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.venegas.buses_api.presentation.http.request.HoldRequest;

@RestController
@RequestMapping("/api/v1")
public class SeatHoldController {

    @PostMapping("/holds")
    public ResponseEntity<?> hold(
            @Validated @RequestBody HoldRequest req,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Endpoint en construcción
        return ResponseEntity.ok(Map.of("message", "Endpoint en construcción"));
    }
}