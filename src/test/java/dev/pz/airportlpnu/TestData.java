package dev.pz.airportlpnu;

import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.PriceHistory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestData {

    public static PriceHistory priceHistory(ClassType classType, BigDecimal price) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setClassType(classType);
        priceHistory.setPrice(price);
        priceHistory.setUpdatedAt(LocalDateTime.now());
        return priceHistory;
    }
}
