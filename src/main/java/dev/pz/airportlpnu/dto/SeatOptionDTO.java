package dev.pz.airportlpnu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SeatOptionDTO {
    private String name;
    private BigDecimal price;
}
