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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
                                         String arrivalDate, Integer passengers, String classType,
                                         Integer minDurationMinutes, Integer maxDurationMinutes, Integer minPrice, Integer maxPrice,
                                         boolean includeFlightWithoutSeats, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedDepartureDate = (departureDate != null && !departureDate.isEmpty())
                ? LocalDate.parse(departureDate, formatter)
                : null;
        LocalDate parsedArrivalDate = (arrivalDate != null && !arrivalDate.isEmpty())
                ? LocalDate.parse(arrivalDate, formatter)
                : null;

        ClassType classTypeEnum = (classType != null && !classType.isEmpty())
                ? ClassType.valueOf(classType)
                : null;

        Specification<Flight> flightSpec = Specification.where(FlightSpecifications.hasDepartureLocation(departureLocation))
                .and(FlightSpecifications.hasArrivalLocation(arrivalLocation))
                .and(FlightSpecifications.departureOrArrivalDateBetween(parsedDepartureDate, parsedArrivalDate))
                .and(FlightSpecifications.hasAvailableSeatsForClass(classTypeEnum, passengers))
                .and(FlightSpecifications.hasAvailableSeats(includeFlightWithoutSeats))
                .and(FlightSpecifications.hasDurationBetween(minDurationMinutes, maxDurationMinutes))
                .and(FlightSpecifications.hasPriceBetween(minPrice, maxPrice));

        Page<Flight> flights = flightRepository.findAll(flightSpec, pageable);

        return flights.map(flight -> {
            BigDecimal minFlightPrice = getCheapestLatestPrice(flight);
            List<ClassType> availableClasses = getAvailableClassesForFlight(flight, passengers);
            return flightMapper.toDTO(flight, minFlightPrice, availableClasses);
        });
    }

    private BigDecimal getCheapestLatestPrice(Flight flight) {
        List<PriceHistory> priceHistories = priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(flight.getId());

        Map<ClassType, PriceHistory> latestPricesByClassType = priceHistories.stream()
                .collect(Collectors.toMap(
                        PriceHistory::getClassType,
                        priceHistory -> priceHistory,
                        (existing, replacement) -> existing.getUpdatedAt().isAfter(replacement.getUpdatedAt()) ? existing : replacement
                ));

        return latestPricesByClassType.values().stream()
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
