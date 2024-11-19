package dev.pz.airportlpnu.services.email.templates;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class SubscriptionConfirmationTemplate implements EmailTemplate {
    private final String confirmationLink;

    @Override
    public String getTemplateName() {
        return "subscription-confirmation-template.html";
    }

    @Override
    public Map<String, Object> getTemplateVariables() {
        return new HashMap<>(
                Map.of("link", confirmationLink)
        );
    }
}
