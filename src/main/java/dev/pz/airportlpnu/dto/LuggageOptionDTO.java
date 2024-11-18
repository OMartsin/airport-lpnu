package dev.pz.airportlpnu.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LuggageOptionDTO {
    private String name;
    private String description;
    private Integer weightLimit;
    private BigDecimal price;
}
