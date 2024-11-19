package dev.pz.airportlpnu.services.email.templates;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.stereotype.Service;

@Service
public class TemplateProcessingService {
    private final TemplateEngine templateEngine;

    public TemplateProcessingService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String generateContent(EmailTemplate template) {
        Context context = new Context();
        context.setVariables(template.getTemplateVariables());
        return templateEngine.process(template.getTemplateName(), context);
    }
}
