package cl.venegas.buses_api.presentation.http.controller;

import cl.venegas.buses_api.application.usecase.HoldSeatsService;
import cl.venegas.buses_api.presentation.http.request.HoldRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class HoldsController {
  private final HoldSeatsService service;
  public HoldsController(HoldSeatsService s){ this.service=s; }

  @PostMapping("/holds")
  public ResponseEntity<?> hold(@Validated @RequestBody HoldRequest req){
    try {
      var expires = service.handle(req.tripId(), req.seat(), req.userId());
      return ResponseEntity.ok(Map.of("expiresAt", expires.toString()));
    } catch (DataIntegrityViolationException e){
      return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","seat_taken"));
    }
  }
}