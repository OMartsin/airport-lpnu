package dev.pz.airportlpnu.repositories;

import dev.pz.airportlpnu.entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByFlightIdAndIsAvailableTrue(Long flightId);
}
