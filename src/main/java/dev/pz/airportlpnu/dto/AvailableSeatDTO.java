package dev.pz.airportlpnu.dto;

import dev.pz.airportlpnu.entities.ClassType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AvailableSeatDTO {
    private ClassType classType;
    private String seatOption;
    private BigDecimal price;
    private int amount;
}
