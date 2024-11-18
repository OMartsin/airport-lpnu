package dev.pz.airportlpnu.services;

import dev.pz.airportlpnu.dto.AvailableSeatDTO;
import dev.pz.airportlpnu.dto.FlightDTO;
import dev.pz.airportlpnu.dto.FlightDetailsDTO;
import dev.pz.airportlpnu.dto.PriceHistoryPlotDTO;
import dev.pz.airportlpnu.entities.*;
import dev.pz.airportlpnu.mappers.FlightMapper;
import dev.pz.airportlpnu.mappers.PriceHistoryMapper;
import dev.pz.airportlpnu.repositories.FlightRepository;
import dev.pz.airportlpnu.repositories.PriceHistoryRepository;
import dev.pz.airportlpnu.repositories.specifications.FlightSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final FlightMapper flightMapper;
    private final SeatsService seatsService;
    private final PriceHistoryMapper priceHistoryMapper;

    public FlightDetailsDTO getFlightDetails(Long id) {
        Flight flight = flightRepository.findById(id).orElseThrow(() -> new RuntimeException("Flight not found"));

        Map<ClassType, BigDecimal> prices = priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(flight.getId())
                .stream()
                .collect(Collectors.toMap(PriceHistory::getClassType, PriceHistory::getPrice, (a, b) -> a));

        List<AvailableSeatDTO> availableSeats = seatsService.getAvailableSeatsForFlight(flight, prices);

        List<PriceHistory> priceHistory = priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(flight.getId());

        PriceHistoryPlotDTO priceHistoryPlotDTO = priceHistoryMapper.toPriceHistoryPlotDTO(priceHistory);

        return flightMapper.toDetailsDTO(flight, availableSeats, prices, priceHistoryPlotDTO);
    }


    public Page<FlightDTO> searchFlights(String departureLocation, String arrivalLocation, String departureDate,
                                         String arrivalDate, Integer passengers, String classType, boolean includeFlightWithoutSeats, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime departureDateTime = departureDate != null ? LocalDate.parse(departureDate, formatter).atStartOfDay() : null;
        LocalDateTime arrivalDateTime = arrivalDate != null ? LocalDate.parse(arrivalDate, formatter).atTime(23, 59) : null;
        ClassType classTypeObj = classType != null ? ClassType.valueOf(classType) : null;

        Page<Flight> flights = flightRepository.findAll(
                FlightSpecifications.hasDepartureLocation(departureLocation)
                        .and(FlightSpecifications.hasArrivalLocation(arrivalLocation))
                        .and(FlightSpecifications.departureTimeBetween(departureDateTime, arrivalDateTime))
                        .and(FlightSpecifications.hasAvailableSeatsForClass(classTypeObj, passengers))
                        .and(FlightSpecifications.hasAvailableSeats(includeFlightWithoutSeats)),
                pageable
        );

        return flights.map(flight -> {
            BigDecimal minPrice = getMinPriceForFlight(flight);
            List<ClassType> availableClasses = getAvailableClassesForFlight(flight, passengers);
            return flightMapper.toDTO(flight, minPrice, availableClasses);
        });
    }

    private BigDecimal getMinPriceForFlight(Flight flight) {
        return priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(flight.getId())
                .stream()
                .map(PriceHistory::getPrice)
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

    private List<ClassType> getAvailableClassesForFlight(Flight flight, Integer passengers) {
        Map<ClassType, Long> seatAvailability = flight.getSeats().stream()
                .filter(Seat::getIsAvailable)
                .collect(Collectors.groupingBy(Seat::getClassType, Collectors.counting()));

        return seatAvailability.entrySet().stream()
                .filter(entry -> passengers == null || entry.getValue() >= passengers)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
