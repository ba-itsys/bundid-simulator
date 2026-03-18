package de.ba.oiam.bundidsim.controller;

import de.ba.oiam.bundidsim.model.BundIdUser;
import de.ba.oiam.bundidsim.model.SamlRequestValues;
import de.ba.oiam.bundidsim.model.SamlResponseValues;
import de.ba.oiam.bundidsim.model.Status;
import de.ba.oiam.bundidsim.model.view.EditFormData;
import de.ba.oiam.bundidsim.model.view.EditFormDataValidator;
import de.ba.oiam.bundidsim.model.view.SelectFormData;
import de.ba.oiam.bundidsim.services.SamlResponseGeneratorService;
import de.ba.oiam.bundidsim.services.UserDefinitionService;
import de.ba.oiam.bundidsim.utils.AuthLevelTools;
import de.ba.oiam.bundidsim.utils.ObjectStringConverter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Webcontroller für die Detail-Bearbeitung einer Person.
 */
@Controller
@Slf4j
public class EditViewController {

    @Autowired
    private UserDefinitionService userService;

    @Autowired
    private EditFormDataValidator validator;

    @Autowired
    private SamlResponseGeneratorService samlResponseGeneratorService;

    @InitBinder("eformdata")
    protected void initBinder(WebDataBinder dataBinder) {
        // Form target
        log.debug("call initBinder()");
        dataBinder.setValidator(validator);
    }

    /**
     * Aus dem QuickSelectionView wird über den Button "Person bearbeiten" diese Methode aufgerufen.
     *
     * @param model
     * @param formData
     * @return
     */
    @PostMapping(path = "/edit")
    public String startEditView(Model model, @ModelAttribute("formdata") SelectFormData formData) {

        String userId = formData.getUserId();
        log.debug("call startEditView with user [{}]", userId);

        BundIdUser user = userService.getUserById(userId);
        SamlRequestValues requestParams =
                ObjectStringConverter.DecodeAndDeserialize(
                        formData.getSamlRequest(), SamlRequestValues.class);

        // Formmodel bereitstellen
        EditFormData editFormData =
                EditFormData.builder().samlRequest(formData.getSamlRequest())
                        .status(SelectFormData.STATUS_OK)
                        .identifikationWith(AuthLevelTools.IDENTIFICATION_EID)
                        .user(user).build();

        model.addAttribute("eformdata", editFormData);
        model.addAttribute(
                "identWithList",
                AuthLevelTools.createIdentificationWithList(requestParams.getReqAuthnLevel()));
        log.debug("Model: [{}]", editFormData.toString());
        return "edit_view";
    }

    @PostMapping(path = "/edit/submit")
    public String submitEditView(
            Model model,
            @Valid @ModelAttribute("eformdata") EditFormData formData,
            BindingResult bindingResult) {

        log.debug("call submitEditView");

        SamlRequestValues requestParams =
                ObjectStringConverter.DecodeAndDeserialize(
                        formData.getSamlRequest(), SamlRequestValues.class);

        if (bindingResult != null && bindingResult.hasErrors()) {
            log.debug("validationErrors found...");
            model.addAttribute(
                    "identWithList",
                    AuthLevelTools.createIdentificationWithList(requestParams.getReqAuthnLevel()));
            return "edit_view";
        }

        // Validierung OK, SAML-Response erstellen
        Status samlStatus = Status.createStatusFromKey(formData.getStatus());
        BundIdUser user = formData.getUser();
        user = addDataToUser(user, formData);
        log.debug("BundIdUser: [{}]", user);
        return prepareSamlResponse(model, formData.getSamlRequest(), samlStatus, user, user.getEidCitizenQaaLevel());
    }

    @PostMapping(path = "/edit/cancel")
    public String cancelEditView(
            Model model,
            @ModelAttribute("eformdata") EditFormData formData) {

        log.debug("call cancelEditView");

        Status samlStatus = Status.buildCancelStatus();
        return prepareSamlResponse(model, formData.getSamlRequest(), samlStatus, null, "");
    }

    // private Helper ****************************************************************************************************

    private BundIdUser addDataToUser(BundIdUser user, EditFormData formData) {

        // User-Daten vervollständigen
        user.setAssertionProvedBy(formData.getIdentifikationWith()); // Identifizierungsmittel
        user.setEidCitizenQaaLevel(
                AuthLevelTools.getAuthnLevelByIdentificationMethod(formData.getIdentifikationWith()));
        user.setVersion("2021.7.1");
        if (AuthLevelTools.IDENTIFICATION_EIDAS.equalsIgnoreCase(formData.getIdentifikationWith())) {
            // Speziell für eIDAS-Identifikation
            user.setEidasIssuingCountry(formData.getEidasCountry());
            user.setEidCitizenQaaLevel(AuthLevelTools.getAuthnLevelByLoa(formData.getEidasLoa()));
        }
        return user;
    }

    private String prepareSamlResponse(Model model, String samlRequest, Status samlStatus, BundIdUser user, String authnLevel) {

        SamlRequestValues requestParams = ObjectStringConverter.DecodeAndDeserialize(samlRequest, SamlRequestValues.class);

        SamlResponseValues responseParams =
                SamlResponseValues.builder()
                        .id(UUID.randomUUID().toString())
                        .assertionId(UUID.randomUUID().toString())
                        .requestId(requestParams.getId())
                        .spEntityId(requestParams.getIssuer())
                        .idpId(requestParams.getIssuer())
                        .created(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                        .ascUrl(requestParams.getAscUrl())
                        .nameId(user != null ? user.getBpk2() : null)
                        .userAuthnLevel(authnLevel)
                        .build();
        log.debug("ResponseParams; [{}]", responseParams);

        String samlResponseAsString =
                samlResponseGeneratorService.generateSamlResponse(
                        samlStatus, user, responseParams);

        model.addAttribute("saml_response", samlResponseAsString);
        model.addAttribute("relay_state", requestParams.getRelayState());
        model.addAttribute("post_url", requestParams.getAscUrl());
        return "auth_response";
    }
}
