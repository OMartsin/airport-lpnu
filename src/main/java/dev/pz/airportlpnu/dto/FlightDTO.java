package dev.pz.airportlpnu.dto;

import dev.pz.airportlpnu.entities.ClassType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FlightDTO {
    private Long id;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String airline;
    private String duration;
    private BigDecimal minPrice;
    private List<ClassType> classTypes;
}
