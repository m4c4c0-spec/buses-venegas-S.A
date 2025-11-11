package backend.src.main.java.cl.app.presentation.http.controller;

import backend.src.main.java.cl.app.application.usecase.SearchTripsService;
import backend.src.main.java.cl.app.presentation.http.response.TripResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class TripsController {
  private final SearchTripsService service;
  public TripsController(SearchTripsService s){ this.service=s; }

  @GetMapping("/trips")
  public List<TripResponse> get(@RequestParam String from,
                                @RequestParam String to,
                                @RequestParam String date){
    return service.handle(from, to, LocalDate.parse(date))
                  .stream().map(TripResponse::from).toList();
  }
}