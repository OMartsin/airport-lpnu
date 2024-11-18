package dev.pz.airportlpnu.repositories;

import dev.pz.airportlpnu.entities.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findByFlightIdOrderByUpdatedAtDesc(Long flightId);
}
