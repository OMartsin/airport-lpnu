package dev.pz.airportlpnu.dto;

import dev.pz.airportlpnu.entities.ClassType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@Data
public class FlightDetailsDTO {
    private Long id;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String duration;
    private String airline;
    private List<AvailableSeatDTO> availableSeats;
    private List<LuggageOptionDTO> luggageOptions;
    private Map<ClassType, BigDecimal> prices;
    private PriceHistoryPlotDTO priceHistory;
}
