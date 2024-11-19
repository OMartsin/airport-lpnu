package dev.pz.airportlpnu.services.email.templates;

import java.util.Map;

public interface EmailTemplate {
    String getTemplateName();
    Map<String, Object> getTemplateVariables();
}

