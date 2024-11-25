package dev.pz.airportlpnu.controllers;

import dev.pz.airportlpnu.dto.FlightDTO;
import dev.pz.airportlpnu.dto.FlightDetailsDTO;
import dev.pz.airportlpnu.services.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @GetMapping
    public Page<FlightDTO> getFlights(@RequestParam(required = false) String departureLocation,
                                      @RequestParam(required = false) String arrivalLocation,
                                      @RequestParam(required = false) String departureDate,
                                      @RequestParam(required = false) String arrivalDate,
                                      @RequestParam(required = false) Integer passengers,
                                      @RequestParam(required = false) String classType,
                                      @RequestParam(required = false) Integer minDurationMinutes,
                                      @RequestParam(required = false) Integer maxDurationMinutes,
                                      @RequestParam(required = false) Integer minPrice,
                                      @RequestParam(required = false) Integer maxPrice,
                                      @RequestParam(required = false, defaultValue = "false") boolean includeFlightWithoutSeats,
                                      @RequestParam(required = false, defaultValue = "0") int page,
                                      @RequestParam(required = false, defaultValue = "20") int size) {
        return flightService.searchFlights(departureLocation, arrivalLocation, departureDate, arrivalDate, passengers,
                classType, minDurationMinutes, maxDurationMinutes, minPrice, maxPrice, includeFlightWithoutSeats, page, size);
    }

    @GetMapping("/{id}")
    public FlightDetailsDTO getFlightDetails(@PathVariable Long id) {
        return flightService.getFlightDetails(id);
    }
}
