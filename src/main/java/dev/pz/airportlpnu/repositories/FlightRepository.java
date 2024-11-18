package dev.pz.airportlpnu.repositories;

import dev.pz.airportlpnu.entities.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.lang.Nullable;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long>, JpaSpecificationExecutor<Flight> {
    @Override
    @Query("SELECT f FROM Flight f")
    @EntityGraph(attributePaths = {"seats", "seats.seatOption"})
    Page<Flight> findAll(@Nullable Specification<Flight> specification, Pageable pageable);
}
