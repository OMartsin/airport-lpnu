package dev.pz.airportlpnu.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class FlightSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column
    private String departureLocation;

    @Column
    private String arrivalLocation;

    @Column
    private String departureDate;

    @Column
    private String arrivalDate;

    @Column
    private Integer passengers;

    @Column
    private String classType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private Boolean isActive = true;
}
