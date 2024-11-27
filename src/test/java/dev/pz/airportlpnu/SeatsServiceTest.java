package dev.pz.airportlpnu;

import dev.pz.airportlpnu.dto.AvailableSeatDTO;
import dev.pz.airportlpnu.entities.*;
import dev.pz.airportlpnu.repositories.SeatRepository;
import dev.pz.airportlpnu.services.SeatsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SeatsServiceTest {

    @InjectMocks
    private SeatsService seatsService;

    @Mock
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAvailableSeatsForFlight() {
        // Arrange
        Flight flight = new Flight();
        flight.setId(1L);

        Seat seat = new Seat();
        seat.setClassType(ClassType.ECONOMY);
        seat.setIsAvailable(true);
        var seatOption = new SeatOption();
        seatOption.setName(SeatType.WINDOW);
        seatOption.setPriceMultiplier(1.5);
        seat.setSeatOption(seatOption);

        when(seatRepository.findByFlightIdAndIsAvailableTrue(1L))
                .thenReturn(List.of(seat));

        // Act
        List<AvailableSeatDTO> availableSeats = seatsService.getAvailableSeatsForFlight(
                flight, Map.of(ClassType.ECONOMY, BigDecimal.valueOf(100))
        );

        // Assert
        assertNotNull(availableSeats);
        assertEquals(1, availableSeats.size());
        assertEquals(SeatType.WINDOW.toString(), availableSeats.get(0).getSeatOption());
    }
}

