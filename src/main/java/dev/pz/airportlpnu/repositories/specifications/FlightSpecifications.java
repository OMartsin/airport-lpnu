package dev.pz.airportlpnu.repositories.specifications;

import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.Flight;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class FlightSpecifications {

    public static Specification<Flight> hasDepartureLocation(String departureLocation) {
        return (root, query, criteriaBuilder) -> {
            if (departureLocation != null && !departureLocation.isEmpty()) {
                return criteriaBuilder.equal(root.get("departureLocation"), departureLocation);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Flight> hasArrivalLocation(String arrivalLocation) {
        return (root, query, criteriaBuilder) -> {
            if (arrivalLocation != null && !arrivalLocation.isEmpty()) {
                return criteriaBuilder.equal(root.get("arrivalLocation"), arrivalLocation);
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Flight> departureOrArrivalDateBetween(LocalDate departureDate, LocalDate arrivalDate) {
        return (root, query, criteriaBuilder) -> {
            if (departureDate != null && arrivalDate != null) {
                return criteriaBuilder.or(
                        criteriaBuilder.between(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("departureTime")),
                                departureDate, arrivalDate
                        ),
                        criteriaBuilder.between(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("arrivalTime")),
                                departureDate, arrivalDate
                        )
                );
            } else if (departureDate != null) {
                return criteriaBuilder.or(
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("departureTime")),
                                departureDate
                        ),
                        criteriaBuilder.greaterThanOrEqualTo(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("arrivalTime")),
                                departureDate
                        )
                );
            } else if (arrivalDate != null) {
                return criteriaBuilder.or(
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("departureTime")),
                                arrivalDate
                        ),
                        criteriaBuilder.lessThanOrEqualTo(
                                criteriaBuilder.function("DATE", LocalDate.class, root.get("arrivalTime")),
                                arrivalDate
                        )
                );
            }
            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<Flight> hasAvailableSeatsForClass(ClassType classType, Integer passengers) {
        return (root, query, criteriaBuilder) -> {
            if (classType == null) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> seatsJoin = root.join("seats", JoinType.LEFT);
            return criteriaBuilder.and(
                    criteriaBuilder.equal(seatsJoin.get("classType"), classType),
                    passengers != null ? criteriaBuilder.greaterThanOrEqualTo(seatsJoin.get("id"), passengers) : criteriaBuilder.conjunction()
            );
        };
    }

    public static Specification<Flight> hasAvailableSeats(Boolean includeFlightWithoutSeats) {
        return (root, query, criteriaBuilder) -> {
            if (Boolean.TRUE.equals(includeFlightWithoutSeats)) {
                return criteriaBuilder.conjunction();
            }
            Join<Object, Object> seatsJoin = root.join("seats", JoinType.LEFT);
            return criteriaBuilder.isTrue(seatsJoin.get("isAvailable"));
        };
    }
}
