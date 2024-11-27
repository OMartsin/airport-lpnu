package dev.pz.airportlpnu;

import dev.pz.airportlpnu.dto.FlightDTO;
import dev.pz.airportlpnu.dto.FlightDetailsDTO;
import dev.pz.airportlpnu.dto.AvailableSeatDTO;
import dev.pz.airportlpnu.dto.PriceHistoryPlotDTO;
import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.Flight;
import dev.pz.airportlpnu.entities.Seat;
import dev.pz.airportlpnu.mappers.FlightMapper;
import dev.pz.airportlpnu.mappers.PriceHistoryMapper;
import dev.pz.airportlpnu.repositories.FlightRepository;
import dev.pz.airportlpnu.repositories.PriceHistoryRepository;
import dev.pz.airportlpnu.services.FlightService;
import dev.pz.airportlpnu.services.SeatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @InjectMocks
    private FlightService flightService;

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private PriceHistoryRepository priceHistoryRepository;

    @Mock
    private FlightMapper flightMapper;

    @Mock
    private SeatsService seatsService;

    @Mock
    private PriceHistoryMapper priceHistoryMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchFlights() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setDepartureLocation("Lviv");
        flight.setArrivalLocation("Kyiv");
        flight.setSeats(new HashSet<>()); // Ensure seats are initialized

        PageRequest pageRequest = PageRequest.of(0, 10);

        when(flightRepository.findAll((Specification<Flight>) any(), eq(pageRequest)))
                .thenReturn(new PageImpl<>(Collections.singletonList(flight)));

        // Act
        Page<FlightDTO> result = flightService.searchFlights(
                "Lviv", "Kyiv", "2024-11-01", null,
                1, ClassType.ECONOMY.name(), null, null, null, null, false, 0, 10
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(flightRepository, times(1)).findAll((Specification<Flight>) any(), eq(pageRequest));
    }

    @Test
    void testGetCheapestLatestPrice() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);

        when(priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(1L))
                .thenReturn(List.of(
                        TestData.priceHistory(ClassType.ECONOMY, BigDecimal.valueOf(100)),
                        TestData.priceHistory(ClassType.BUSINESS, BigDecimal.valueOf(150))
                ));

        // Act
        BigDecimal price = flightService.getCheapestLatestPrice(flight);

        // Assert
        assertEquals(BigDecimal.valueOf(100), price);
    }

    @Test
    void getDetailsSuccess() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);
        flight.setDepartureLocation("Lviv");
        flight.setArrivalLocation("Kyiv");
        var seat = new Seat();
        seat.setClassType(ClassType.ECONOMY);
        seat.setSeatNumber("1A");
        seat.setIsAvailable(true);
        var seat2 = new Seat();
        seat2.setClassType(ClassType.BUSINESS);
        seat2.setSeatNumber("1B");
        seat2.setIsAvailable(true);
        flight.setSeats(Set.of(seat, seat2));

        when(flightRepository.findById(1L)).thenReturn(java.util.Optional.of(flight));
        when(priceHistoryRepository.findByFlightIdOrderByUpdatedAtDesc(1L))
                .thenReturn(List.of(
                        TestData.priceHistory(ClassType.ECONOMY, BigDecimal.valueOf(100)),
                        TestData.priceHistory(ClassType.BUSINESS, BigDecimal.valueOf(150))
                ));

        when(seatsService.getAvailableSeatsForFlight(eq(flight), anyMap()))
                .thenReturn(List.of(
                        new AvailableSeatDTO(ClassType.ECONOMY, "1A", BigDecimal.valueOf(100), 1),
                        new AvailableSeatDTO(ClassType.BUSINESS, "1B", BigDecimal.valueOf(150), 1)
                ));

        when(priceHistoryMapper.toPriceHistoryPlotDTO(anyList()))
                .thenReturn(new PriceHistoryPlotDTO(Map.of(
                        ClassType.ECONOMY.name(), List.of(new PriceHistoryPlotDTO.PriceTime("100", null)),
                        ClassType.BUSINESS.name(), List.of(new PriceHistoryPlotDTO.PriceTime("150", null))
                )));

        when(flightMapper.toDetailsDTO(eq(flight), anyList(), anyMap(), any()))
                .thenReturn(new FlightDetailsDTO(1L, "Lviv", "Kyiv", flight.getDepartureTime(), flight.getArrivalTime(),
                        "1h 0m", "Airline", List.of(), List.of(), Map.of(
                        ClassType.ECONOMY, BigDecimal.valueOf(100),
                        ClassType.BUSINESS, BigDecimal.valueOf(150)
                ), new PriceHistoryPlotDTO(Map.of(
                        ClassType.ECONOMY.name(), List.of(new PriceHistoryPlotDTO.PriceTime("100", null)),
                        ClassType.BUSINESS.name(), List.of(new PriceHistoryPlotDTO.PriceTime("150", null))
                ))));
        // Act
        FlightDetailsDTO result = flightService.getFlightDetails(1L);
        System.out.println(result);

        // Assert
        assertNotNull(result);
        assertEquals("Lviv", result.getDepartureLocation());
        assertEquals("Kyiv", result.getArrivalLocation());
        assertEquals(2, result.getPrices().size());
        assertEquals(BigDecimal.valueOf(100), result.getPrices().get(ClassType.ECONOMY));
        assertEquals(BigDecimal.valueOf(150), result.getPrices().get(ClassType.BUSINESS));
    }


    @Test
    void getDetailsFlightNotFound() {
        // Arrange
        when(flightRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                flightService.getFlightDetails(1L));
        assertEquals("Flight not found", exception.getMessage());
    }
}
