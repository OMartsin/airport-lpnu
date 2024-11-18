package dev.pz.airportlpnu.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SeatOption {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private SeatType name;
    @Column(nullable = false)
    private Double priceMultiplier;
}
