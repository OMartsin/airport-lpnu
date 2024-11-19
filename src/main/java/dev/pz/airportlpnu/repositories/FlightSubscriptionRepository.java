package dev.pz.airportlpnu.repositories;

import dev.pz.airportlpnu.entities.FlightSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlightSubscriptionRepository extends JpaRepository<FlightSubscription, Long> {
    List<FlightSubscription> findAllByIsActive(Boolean isActive);
}
