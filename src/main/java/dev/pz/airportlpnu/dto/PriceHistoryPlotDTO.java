package dev.pz.airportlpnu.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class PriceHistoryPlotDTO {
    private Map<String, List<PriceTime>> historyByClass;

    @Data
    public static class PriceTime {
        private String price;
        private LocalDateTime timestamp;
    }
}
