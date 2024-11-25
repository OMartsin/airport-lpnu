package dev.pz.airportlpnu.controllers;

import dev.pz.airportlpnu.services.subscription.FlightSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class FlightSubscriptionController {

    private final FlightSubscriptionService subscriptionService;

    @PostMapping
    public void createSubscription(@RequestParam String email,
                                   @RequestParam(required = false) String departureLocation,
                                   @RequestParam(required = false) String arrivalLocation,
                                   @RequestParam(required = false) String departureDate,
                                   @RequestParam(required = false) String arrivalDate,
                                   @RequestParam(required = false) Integer passengers,
                                   @RequestParam(required = false) String classType,
                                   @RequestParam(required = false) Integer minDurationMinutes,
                                   @RequestParam(required = false) Integer maxDurationMinutes,
                                   @RequestParam(required = false) Integer minPrice,
                                   @RequestParam(required = false) Integer maxPrice) {
        subscriptionService.createSubscription(email, departureLocation, arrivalLocation, departureDate, arrivalDate,
                passengers, classType, minDurationMinutes, maxDurationMinutes, minPrice, maxPrice);
    }
}
