package dev.pz.airportlpnu.services.email.templates;

import dev.pz.airportlpnu.dto.FlightDTO;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FlightFoundTemplate implements EmailTemplate {
    private final List<FlightDTO> flights;
    private final String baseUri;

    @Override
    public String getTemplateName() {
        return "flight-found-template.html";
    }

    @Override
    public Map<String, Object> getTemplateVariables() {
        Map<String, Object> variables = new HashMap<>();
        variables.put("flights", flights);
        variables.put("link", baseUri);
        return variables;
    }
}
