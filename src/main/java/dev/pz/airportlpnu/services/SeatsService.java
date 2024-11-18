package dev.pz.airportlpnu.services;

import dev.pz.airportlpnu.dto.AvailableSeatDTO;
import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.Flight;
import dev.pz.airportlpnu.entities.Seat;
import dev.pz.airportlpnu.entities.SeatOption;
import dev.pz.airportlpnu.repositories.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatsService {
    private final SeatRepository seatRepository;

    public List<AvailableSeatDTO> getAvailableSeatsForFlight(Flight flight, Map<ClassType, BigDecimal> basePrices) {
        List<Seat> availableSeats = seatRepository.findByFlightIdAndIsAvailableTrue(flight.getId());

        return availableSeats.stream()
                .collect(Collectors.groupingBy(
                        seat -> Map.entry(seat.getClassType(), seat.getSeatOption()),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> {
                    ClassType classType = entry.getKey().getKey();
                    SeatOption seatOption = entry.getKey().getValue();
                    Long count = entry.getValue();

                    BigDecimal basePrice = basePrices.getOrDefault(classType, BigDecimal.ZERO);
                    BigDecimal finalPrice = basePrice.multiply(BigDecimal.valueOf(seatOption.getPriceMultiplier()));

                    AvailableSeatDTO dto = new AvailableSeatDTO();
                    dto.setClassType(classType);
                    dto.setSeatOption(seatOption.getName().toString());
                    dto.setPrice(finalPrice);
                    dto.setAmount(count.intValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
