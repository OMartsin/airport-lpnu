package dev.pz.airportlpnu.services.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlightSubscriptionScheduler {
    private final FlightSubscriptionService flightSubscriptionService;

    @Scheduled(cron = "0 * * * * ?")
    public void processSubscriptionsTask() {
        System.out.println("Processing subscriptions...");
        flightSubscriptionService.processSubscriptions();
    }
}
