package dev.pz.airportlpnu.mappers;

import dev.pz.airportlpnu.dto.PriceHistoryPlotDTO;
import dev.pz.airportlpnu.entities.PriceHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PriceHistoryMapper {

    public PriceHistoryPlotDTO toPriceHistoryPlotDTO(List<PriceHistory> priceHistories) {
        Map<String, List<PriceHistoryPlotDTO.PriceTime>> historyByClass = priceHistories.stream()
                .collect(Collectors.groupingBy(
                        priceHistory -> priceHistory.getClassType().name(),
                        Collectors.mapping(priceHistory -> {
                            PriceHistoryPlotDTO.PriceTime priceTime = new PriceHistoryPlotDTO.PriceTime();
                            priceTime.setPrice(priceHistory.getPrice().toString());
                            priceTime.setTimestamp(priceHistory.getUpdatedAt());
                            return priceTime;
                        }, Collectors.toList())
                ));

        PriceHistoryPlotDTO dto = new PriceHistoryPlotDTO();
        dto.setHistoryByClass(historyByClass);
        return dto;
    }
}
