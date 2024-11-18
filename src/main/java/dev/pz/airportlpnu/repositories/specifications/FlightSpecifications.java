package dev.pz.airportlpnu.repositories.specifications;

import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.Flight;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class FlightSpecifications {

    public static Specification<Flight> hasDepartureLocation(String departureLocation) {
        return (root, query, criteriaBuilder) -> departureLocation != null ?
                criteriaBuilder.equal(root.get("departureLocation"), departureLocation) : null;
    }

    public static Specification<Flight> hasArrivalLocation(String arrivalLocation) {
        return (root, query, criteriaBuilder) -> arrivalLocation != null ?
                criteriaBuilder.equal(root.get("arrivalLocation"), arrivalLocation) : null;
    }

    public static Specification<Flight> departureTimeBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> (startDate != null && endDate != null) ?
                criteriaBuilder.between(root.get("departureTime"), startDate, endDate) : null;
    }

    public static Specification<Flight> hasAvailableSeatsForClass(ClassType classType, Integer passengers) {
        return (root, query, criteriaBuilder) -> {
            if (classType == null) return null;

            // Join with seats table
            Join<Object, Object> seatsJoin = root.join("seats", JoinType.INNER);

            return criteriaBuilder.and(
                    criteriaBuilder.equal(seatsJoin.get("classType"), classType),
                    passengers != null
                            ? criteriaBuilder.greaterThanOrEqualTo(seatsJoin.get("id"), passengers)
                            : criteriaBuilder.conjunction()
            );
        };
    }

    public static Specification<Flight> hasAvailableSeats(Boolean includeFlightsWithoutSeats) {
        return (root, query, criteriaBuilder) -> {
            if (includeFlightsWithoutSeats == null || includeFlightsWithoutSeats) {
                return null;
            }
            Join<Object, Object> seatsJoin = root.join("seats", JoinType.INNER);
            return criteriaBuilder.isTrue(seatsJoin.get("isAvailable"));
        };
    }

}
