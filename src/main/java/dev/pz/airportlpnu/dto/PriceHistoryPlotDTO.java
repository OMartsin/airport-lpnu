package dev.pz.airportlpnu.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistoryPlotDTO {
    private Map<String, List<PriceTime>> historyByClass;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PriceTime {
        private String price;
        private LocalDateTime timestamp;
    }
}
