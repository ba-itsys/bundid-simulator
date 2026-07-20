/*
 * Copyright 2025. IT-Systemhaus der Bundesagentur fuer Arbeit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ba.oiam.bundidsim.controller;

import de.ba.oiam.bundidsim.model.BundIdUser;
import de.ba.oiam.bundidsim.model.SamlRequestValues;
import de.ba.oiam.bundidsim.model.view.SelectFormData;
import de.ba.oiam.bundidsim.services.UserDefinitionService;
import de.ba.oiam.bundidsim.utils.AuthLevelTools;
import de.ba.oiam.bundidsim.utils.ObjectStringConverter;
import de.ba.oiam.bundidsim.utils.XmlParserTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/**
 * Webcontroller für den Einstiegspunkt "/saml" via POST und GET (nur Test)
 */
@Controller
@Slf4j
public class SamlController {

    @Autowired
    private UserDefinitionService userService;

    /**
     * GET /saml Einstiegspunkt für den Test.
     * Die Parameter "SamlRequest" und "RelayState" sind ein Fake. Mit diesem Aufruf kann die Web-Ui im Browser
     * ohne Keycloak-Anbindung getestet werden. Die Aktion "Person übernehmen" funktioniert nicht korrekt, da die
     * Rücksprung-Url im hier definierten SAML-Request ungültig ist.
     *
     * @param model
     * @return
     * @throws Exception
     */
    @GetMapping(path = "/saml")
    public String samlRequestReceiverGet(Model model) throws Exception {

        String paramSamlRequest = createSkeletonSamlRequestBase64Encoded();
        String paramRelayState =
                "lwN0_rRBl5jHaEJt4a0V6IRFOkaR3Krw4K9HQ-73NSM.1gou_vqz1yw.api-client-public";
        return samlRequestReceiver(model, paramSamlRequest, paramRelayState);
    }

    /**
     * POST /saml Einstiegspunkt in den BundID-Simulator. Der Endpunkt wird mit dem Parametern "SAMLRequest"
     * (siehe SamlRequest in der BundID-Spec) und "RelayState" aufgerufen.
     *
     * @param samlRequest SAML-Request (Base64)
     * @param state       Relaystate
     */
    @PostMapping(path = "/saml")
    public String samlRequestReceiver(Model model,
                                      @RequestParam(value = "SAMLRequest", required = false) String samlRequest,
                                      @RequestParam(value = "RelayState", required = false) String state)
            throws Exception {

        log.debug("Start samlRequestReceiver...");
        log.debug("RelayState: [{}]", state);

        // SAML-Request analysieren und Werte extrahieren
        SamlRequestValues samlRequestModel = analyseSamlRequest(samlRequest, state);

        // Default-Wert für ReqAuthnLevel setzen
        if (!StringUtils.hasText(samlRequestModel.getReqAuthnLevel())) {
            samlRequestModel.setReqAuthnLevel(AuthLevelTools.STORK_1);
        }

        List<BundIdUser> userList = userService.getUserList();
        SelectFormData formData =
                SelectFormData.builder()
                        .status(SelectFormData.STATUS_OK)
                        .userId(userList.getFirst().getId()) // Erster User-Eintrag aktiv
                        .identifikationWith(AuthLevelTools.IDENTIFICATION_EID)
                        .samlRequest(ObjectStringConverter.serializeAndEncode(samlRequestModel))
                        .build();

        model.addAttribute("formdata", formData);
        model.addAttribute("userlist", userList);
        model.addAttribute(
                "identWithList",
                AuthLevelTools.createIdentificationWithList(samlRequestModel.getReqAuthnLevel()));
        log.debug("Model: [{}]", formData.toString());

        return "select_view";
    }


    /**
     * Erzeugt einen Test-SAML-Request mit Beispielwerten und gibt ihn Base64-kodiert zurück.
     * This is not a real SAML-Request. Just the bare skeleton for the following functions to work.
     *
     * @return Base64-kodierter SAML-AuthnRequest
     */
    private String createSkeletonSamlRequestBase64Encoded() {
        String ascUrl = "http://localhost:8443/auth/realms/test/broker/bundid/endpoint";
        String destUrl = "http://localhost:8080/saml";
        String id = "test-request-id-12345";
        String issuer = "http://localhost:8443/auth/realms/test";

        String xml = String.format("""
                <samlp:AuthnRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
                    xmlns="urn:oasis:names:tc:SAML:2.0:assertion"
                    xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion"
                    AssertionConsumerServiceURL="%s"
                    AttributeConsumingServiceIndex="0"
                    Destination="%s"
                    ID="%s"
                    IssueInstant="2025-01-01T00:00:00.000Z"
                    ProtocolBinding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
                    Version="2.0">
                  <saml:Issuer>%s</saml:Issuer>
                  <samlp:RequestedAuthnContext Comparison="minimum">
                    <saml:AuthnContextClassRef>urn:oasis:names:tc:SAML:2.0:ac:classes:Kerberos</saml:AuthnContextClassRef>
                  </samlp:RequestedAuthnContext>
                </samlp:AuthnRequest>""", ascUrl, destUrl, id, issuer);

        return Base64.getEncoder().encodeToString(xml.getBytes(StandardCharsets.UTF_8));
    }

    // Helper **********************************************************************************************************

    /**
     * Der encodierte SAML-Request wird dekodiert und XML-geparst. Die wichtigsten Daten werden in SamlRequestValues
     * gespeichert.
     *
     * @param samlRequest
     * @param state
     * @return
     * @throws Exception
     */
    private SamlRequestValues analyseSamlRequest(String samlRequest, String state) throws Exception {
        String decodedSamlRequest = new String(Base64.getDecoder().decode(samlRequest));
        Document doc = XmlParserTools.parseXML(decodedSamlRequest);

        String serviceUrl = doc.getDocumentElement().getAttribute("AssertionConsumerServiceURL");
        String id = doc.getDocumentElement().getAttribute("ID");
        String valueIssuer = XmlParserTools.findValueByTagname(doc, "saml:Issuer");
        String valueReqAutnLevel =
                XmlParserTools.findValueByTagname(doc, "samlp:RequestedAuthnContext");

        SamlRequestValues samlRequestModel =
                SamlRequestValues.builder()
                        .id(id)
                        .issuer(valueIssuer)
                        .reqAuthnLevel(valueReqAutnLevel)
                        .ascUrl(serviceUrl)
                        .relayState(state)
                        .build();
        log.debug("model samrequest: [{}]", samlRequestModel);
        return samlRequestModel;
    }
}
