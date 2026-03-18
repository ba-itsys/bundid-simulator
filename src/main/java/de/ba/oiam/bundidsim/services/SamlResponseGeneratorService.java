package de.ba.oiam.bundidsim.services;

import de.ba.oiam.bundidsim.model.BundIdUser;
import de.ba.oiam.bundidsim.model.SamlResponseValues;
import de.ba.oiam.bundidsim.model.Status;
import de.ba.oiam.bundidsim.utils.ResourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class SamlResponseGeneratorService {

    private static final Duration SUBJECT_CONFIRMATION_VALIDITY = Duration.ofMinutes(5);

    @Value("classpath:views/saml_response_template.xml")
    private Resource fileResource;

    @Value("classpath:views/saml_response_error_template.xml")
    private Resource fileResourceError;

    @Autowired
    private UserAttributesService userAttributesService;


    /**
     * generiert aus den Userdaten und weiteren Attributen einen encoded SAML-Response.
     *
     * Via Thymeleaf wird das XML-Template mit Daten befüllt und Base64-encoded geliefert.
     *
     * weitere Daten:
     * - erreichtes Vertrauensniveau (Stork) und Übersetzung in TrustLevel (HOCH etc.)
     *
     * @param user
     * @return
     */
    public String generateSamlResponse(Status samlStatus, BundIdUser user, SamlResponseValues params) {

        log.debug("start generateSamlResponse for status [{}]", samlStatus.getStatusType().name());
        SamlResponseValues responseParams = enrichResponseParams(params);

        String renderedXmlTemplate = samlStatus.isStatusOK() ?
                renderXmlTemplate(user, responseParams) : renderXmlErrorTemplate(samlStatus, responseParams);
        log.debug("renderedXmlTemplate [{}]", renderedXmlTemplate);

        return Base64.getEncoder().encodeToString(renderedXmlTemplate.getBytes());
    }

    /**
     * Umwandlung
     * STORK-QAA-Level-4 -> HOCH
     * STORK-QAA-Level-3 -> SUBSTANTIELL
     * STORK-QAA-Level-1 -> NORMAL
     *
     * @param authnLevel
     * @return
     */
    private String convertAuthLevelToTrustLevel(String authnLevel) {
        switch (authnLevel) {
            case "STORK-QAA-Level-4":
                return "HOCH";
            case "STORK-QAA-Level-3":
                return "SUBSTANTIELL";
            case "STORK-QAA-Level-1":
                return "NORMAL";
            default:
                throw new IllegalStateException(String.format("unknown AuthnLevel: [%s]", authnLevel));
        }
    }

    private String renderXmlTemplate(BundIdUser user, SamlResponseValues params) {
        SpringTemplateEngine templateEngine = createTemplateEngine();
        String xml = ResourceUtils.loadResourceToString(fileResource);

        Context myContext = new Context();
        List<UserAttributesService.UserAttribute> list = userAttributesService.getUserAttributes(user,
                convertAuthLevelToTrustLevel(params.getUserAuthnLevel()));

        myContext.setVariable("attributes", list);
        myContext.setVariable("params", params);

        return templateEngine.process(xml, myContext);
    }

    private String renderXmlErrorTemplate(Status samlStatus, SamlResponseValues params) {
        SpringTemplateEngine templateEngine = createTemplateEngine();
        String xml = ResourceUtils.loadResourceToString(fileResourceError);

        Context myContext = new Context();

        myContext.setVariable("status", samlStatus);
        myContext.setVariable("params", params);

        return templateEngine.process(xml, myContext);
    }

    private SamlResponseValues enrichResponseParams(SamlResponseValues params) {
        Instant createdAt = params.getCreated() != null
                ? params.getCreated()
                : Instant.now().truncatedTo(ChronoUnit.SECONDS);
        String created = createdAt.toString();
        String responseId = normalizeSamlId(params.getId());
        String subjectConfirmationRecipient = StringUtils.hasText(params.getSubjectConfirmationRecipient())
                ? params.getSubjectConfirmationRecipient()
                : params.getAscUrl();
        String subjectConfirmationNotOnOrAfter = StringUtils.hasText(params.getSubjectConfirmationNotOnOrAfter())
                ? params.getSubjectConfirmationNotOnOrAfter()
                : createdAt.plus(SUBJECT_CONFIRMATION_VALIDITY).toString();
        String assertionId = normalizeSamlId(params.getAssertionId());
        String authnInstant = StringUtils.hasText(params.getAuthnInstant())
                ? params.getAuthnInstant()
                : created;
        String sessionNotOnOrAfter = StringUtils.hasText(params.getSessionNotOnOrAfter())
                ? params.getSessionNotOnOrAfter()
                : createdAt.plus(SUBJECT_CONFIRMATION_VALIDITY).toString();
        String sessionIndex = StringUtils.hasText(params.getSessionIndex())
                ? params.getSessionIndex()
                : assertionId;
        String nameId = StringUtils.hasText(params.getNameId())
                ? params.getNameId()
                : params.getUser();

        return params.toBuilder()
                .id(responseId)
                .created(createdAt)
                .assertionId(assertionId)
                .nameId(nameId)
                .authnInstant(authnInstant)
                .sessionNotOnOrAfter(sessionNotOnOrAfter)
                .sessionIndex(sessionIndex)
                .subjectConfirmationRecipient(subjectConfirmationRecipient)
                .subjectConfirmationNotOnOrAfter(subjectConfirmationNotOnOrAfter)
                .build();
    }

    private String normalizeSamlId(String samlId) {
        if (!StringUtils.hasText(samlId)) {
            return "_" + UUID.randomUUID();
        }
        return samlId.startsWith("_") ? samlId : "_" + samlId;
    }

    private SpringTemplateEngine createTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        StringTemplateResolver resolver = new StringTemplateResolver();
        resolver.setTemplateMode(TemplateMode.TEXT);
        templateEngine.setTemplateResolver(resolver);
        return templateEngine;
    }
}
