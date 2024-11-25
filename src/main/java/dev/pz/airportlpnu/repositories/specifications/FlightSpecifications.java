package dev.pz.airportlpnu.repositories.specifications;

import dev.pz.airportlpnu.entities.ClassType;
import dev.pz.airportlpnu.entities.Flight;
import dev.pz.airportlpnu.entities.PriceHistory;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    public static Specification<Flight> hasPriceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, criteriaBuilder) -> {
            Join<Flight, PriceHistory> priceHistoryJoin = root.join("priceHistory", JoinType.INNER);

            // Subquery to find the latest `updatedAt` for each flight
            Subquery<BigDecimal> cheapestPriceSubquery = query.subquery(BigDecimal.class);
            Root<PriceHistory> subRoot = cheapestPriceSubquery.from(PriceHistory.class);

            Subquery<LocalDateTime> latestUpdateSubquery = query.subquery(LocalDateTime.class);
            Root<PriceHistory> subRootForLatestUpdate = latestUpdateSubquery.from(PriceHistory.class);
            latestUpdateSubquery.select(criteriaBuilder.greatest(subRootForLatestUpdate.<LocalDateTime>get("updatedAt")))
                    .where(criteriaBuilder.equal(subRootForLatestUpdate.get("flight"), root));

            cheapestPriceSubquery.select(criteriaBuilder.min(subRoot.<BigDecimal>get("price")))
                    .where(
                            criteriaBuilder.equal(subRoot.get("flight"), root),
                            criteriaBuilder.equal(subRoot.get("updatedAt"), latestUpdateSubquery)
                    );

            Predicate predicate = criteriaBuilder.conjunction();
            if (minPrice != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(cheapestPriceSubquery, BigDecimal.valueOf(minPrice))
                );
            }
            if (maxPrice != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(cheapestPriceSubquery, BigDecimal.valueOf(maxPrice))
                );
            }

            return predicate;
        };
    }

    public static Specification<Flight> hasDurationBetween(Integer minDurationMinutes, Integer maxDurationMinutes) {
        return (root, query, criteriaBuilder) -> {
            if (minDurationMinutes == null && maxDurationMinutes == null) {
                return null;
            }

            var departureTime = root.get("departureTime");
            var arrivalTime = root.get("arrivalTime");

            var durationInMinutes = criteriaBuilder.quot(
                    criteriaBuilder.diff(
                            criteriaBuilder.function("UNIX_TIMESTAMP", Long.class, arrivalTime),
                            criteriaBuilder.function("UNIX_TIMESTAMP", Long.class, departureTime)
                    ),
                    60
            );

            var predicate = criteriaBuilder.conjunction();

            if (minDurationMinutes != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(
                                durationInMinutes.as(Integer.class),
                                criteriaBuilder.literal(minDurationMinutes)
                        )
                );
            }

            if (maxDurationMinutes != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(
                                durationInMinutes.as(Integer.class),
                                criteriaBuilder.literal(maxDurationMinutes)
                        )
                );
            }

            return predicate;
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
