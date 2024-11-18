package dev.pz.airportlpnu.dto;

import dev.pz.airportlpnu.entities.ClassType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AvailableSeatDTO {
    private ClassType classType;
    private String seatOption;
    private BigDecimal price;
    private int amount;
}
