package dev.pz.airportlpnu.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferDTO {
    private Long flightId;
    private String departureLocation;
    private String arrivalLocation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String airline;
}
