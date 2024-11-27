package dev.pz.airportlpnu;

import dev.pz.airportlpnu.entities.FlightSubscription;
import dev.pz.airportlpnu.repositories.FlightSubscriptionRepository;
import dev.pz.airportlpnu.services.FlightService;
import dev.pz.airportlpnu.services.email.EmailService;
import dev.pz.airportlpnu.services.subscription.FlightSubscriptionService;
import dev.pz.airportlpnu.services.email.templates.SubscriptionConfirmationTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.mockito.Mockito.*;

class FlightSubscriptionServiceTest {

    @InjectMocks
    private FlightSubscriptionService flightSubscriptionService;

    @Mock
    private FlightSubscriptionRepository subscriptionRepository;

    @Mock
    private FlightService flightService;

    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSubscription() {
        // Arrange
        String email = "test@example.com";
        when(subscriptionRepository.save(any(FlightSubscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        flightSubscriptionService.createSubscription(
                email, "Lviv", "Kyiv", "2024-11-01", null, 1, "ECONOMY", null, null, null, null
        );

        // Assert
        verify(subscriptionRepository, times(1)).save(any(FlightSubscription.class));
        verify(emailService, times(1)).sendEmail(eq(email), eq("Subscription Confirmation"), any(SubscriptionConfirmationTemplate.class));
    }

    @Test
    void testProcessSubscriptions() {
        // Arrange
        FlightSubscription subscription = new FlightSubscription();
        subscription.setEmail("test@example.com");
        subscription.setDepartureLocation("Lviv");
        subscription.setArrivalLocation("Kyiv");
        subscription.setIsActive(true);

        when(subscriptionRepository.findAllByIsActive(true)).thenReturn(List.of(subscription));
        when(flightService.searchFlights(
                anyString(), anyString(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean(), anyInt(), anyInt()
        )).thenReturn(Page.empty());

        // Act
        flightSubscriptionService.processSubscriptions();

        // Assert
        verify(subscriptionRepository, times(1)).findAllByIsActive(true);
        verify(emailService, never()).sendEmail(anyString(), anyString(), any());
    }
}
