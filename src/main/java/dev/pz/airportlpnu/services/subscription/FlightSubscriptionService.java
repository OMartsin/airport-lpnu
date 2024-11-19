package dev.pz.airportlpnu.services.subscription;

import dev.pz.airportlpnu.entities.FlightSubscription;
import dev.pz.airportlpnu.repositories.FlightSubscriptionRepository;
import dev.pz.airportlpnu.services.email.EmailService;
import dev.pz.airportlpnu.services.FlightService;
import dev.pz.airportlpnu.services.email.templates.FlightFoundTemplate;
import dev.pz.airportlpnu.services.email.templates.SubscriptionConfirmationTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightSubscriptionService {

    private final FlightSubscriptionRepository subscriptionRepository;
    private final FlightService flightService;
    private final EmailService emailService;
    @Value("${frontend.url}")
    private String frontendUrl;

    private final Integer page = 0;
    private final Integer size = 20;

    public void createSubscription(String email, String departureLocation, String arrivalLocation,
                                   String departureDate, String arrivalDate, Integer passengers, String classType) {
        FlightSubscription subscription = new FlightSubscription();
        subscription.setEmail(email);
        subscription.setDepartureLocation(departureLocation);
        subscription.setArrivalLocation(arrivalLocation);
        subscription.setDepartureDate(departureDate);
        subscription.setArrivalDate(arrivalDate);
        subscription.setPassengers(passengers);
        subscription.setClassType(classType);
        subscriptionRepository.save(subscription);

        emailService.sendEmail(email, "Subscription Confirmation", new SubscriptionConfirmationTemplate(frontendUrl));
    }

    public void processSubscriptions() {
        List<FlightSubscription> subscriptions = subscriptionRepository.findAllByIsActive(true);

        for (FlightSubscription subscription : subscriptions) {
            var flights = flightService.searchFlights(
                    subscription.getDepartureLocation(),
                    subscription.getArrivalLocation(),
                    subscription.getDepartureDate(),
                    subscription.getArrivalDate(),
                    subscription.getPassengers(),
                    subscription.getClassType(),
                    false,
                    page,
                    size
            );

            if(!flights.isEmpty()) {
                emailService.sendEmail(subscription.getEmail(), "New Flights Available",
                        new FlightFoundTemplate(flights.stream().toList(), frontendUrl));
                subscription.setIsActive(false);
                subscriptionRepository.save(subscription);
            }
        }
    }
}
