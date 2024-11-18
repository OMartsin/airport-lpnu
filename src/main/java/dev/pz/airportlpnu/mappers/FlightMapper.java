package dev.pz.airportlpnu.mappers;

import dev.pz.airportlpnu.dto.*;
import dev.pz.airportlpnu.entities.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FlightMapper {

    public FlightDTO toDTO(Flight flight, BigDecimal minPrice, List<ClassType> classTypes) {
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setDepartureLocation(flight.getDepartureLocation());
        dto.setArrivalLocation(flight.getArrivalLocation());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDuration(formatDuration(Duration.between(flight.getDepartureTime(), flight.getArrivalTime())));
        dto.setAirline(flight.getAirline());
        dto.setMinPrice(minPrice);
        dto.setClassTypes(classTypes);
        return dto;
    }

    public FlightDetailsDTO toDetailsDTO(Flight flight, List<AvailableSeatDTO> availableSeats,
                                         Map<ClassType, BigDecimal> prices, PriceHistoryPlotDTO priceHistory) {
        FlightDetailsDTO dto = new FlightDetailsDTO();
        dto.setId(flight.getId());
        dto.setDepartureLocation(flight.getDepartureLocation());
        dto.setArrivalLocation(flight.getArrivalLocation());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setDuration(formatDuration(Duration.between(flight.getDepartureTime(), flight.getArrivalTime())));
        dto.setAirline(flight.getAirline());
        dto.setAvailableSeats(availableSeats);
        dto.setPrices(prices);
        dto.setLuggageOptions(flight.getLuggageOptions().stream()
                .map(this::toLuggageOptionDTO)
                .collect(Collectors.toList()));
        dto.setPriceHistory(priceHistory);
        return dto;
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        StringBuilder formattedDuration = new StringBuilder();
        if (hours > 0) {
            formattedDuration.append(hours).append("h ");
        }
        if (minutes > 0) {
            formattedDuration.append(minutes).append("m");
        }
        return formattedDuration.toString().trim();
    }

    private LuggageOptionDTO toLuggageOptionDTO(LuggageOption option) {
        LuggageOptionDTO dto = new LuggageOptionDTO();
        dto.setName(option.getName());
        dto.setDescription(option.getDescription());
        dto.setWeightLimit(option.getWeightLimit());
        dto.setPrice(option.getPrice());
        return dto;
    }
}
