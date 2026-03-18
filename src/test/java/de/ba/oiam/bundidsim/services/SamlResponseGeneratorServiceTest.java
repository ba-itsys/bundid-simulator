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
package de.ba.oiam.bundidsim.services;

import de.ba.oiam.bundidsim.model.BundIdUser;
import de.ba.oiam.bundidsim.model.SamlResponseValues;
import de.ba.oiam.bundidsim.model.Status;
import de.ba.oiam.bundidsim.utils.XmlParserTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(classes={SamlResponseGeneratorService.class,
        UserFieldDefinitionService.class, UserAttributesService.class})
public class SamlResponseGeneratorServiceTest {

    @Autowired
    private SamlResponseGeneratorService service;

    @Test
    void buildSamlResponseTest() throws Exception {
        Instant created = Instant.parse("2026-03-18T10:15:30Z");

        SamlResponseValues params = SamlResponseValues.builder()
                .id(UUID.randomUUID().toString())
                .assertionId(UUID.randomUUID().toString())
                .requestId(UUID.randomUUID().toString())
                .ascUrl("https://samltool-ewg.pre.buergerserviceportal.de/saml/SSO")
                .userAuthnLevel("STORK-QAA-Level-4")
                .created(created)
                .idpId("bundid-simulator")
                .spEntityId("https://samltool-ewg.pre.buergerserviceportal.de")
                .nameId(UUID.randomUUID().toString())     // hier die BPK2 des users
                .build();

        BundIdUser user = BundIdUser.builder()
                .surname("Müller")
                .givenname("Hans")
                .birthdate("1973-11-15")
                .postalAddress("Berlin Stra0e 12")
                .postalCode("90781")
                .localityName("Nürnberg")
                .country("DE")
                .eidCitizenQaaLevel("STORK-QAA-Level-4")
                .assertionProvedBy("EID")
                .build();

        String samlResponse = service.generateSamlResponse(Status.buildOkStatus(), user, params);
        String decodedSamlResponse =
                new String(Base64.getDecoder().decode(samlResponse), StandardCharsets.UTF_8);
        Document samlResponseDoc = XmlParserTools.parseXML(decodedSamlResponse);
        Element response = samlResponseDoc.getDocumentElement();
        Element subjectConfirmationData =
                (Element) samlResponseDoc.getElementsByTagName("saml:SubjectConfirmationData").item(0);
        Element assertion = (Element) samlResponseDoc.getElementsByTagName("saml:Assertion").item(0);
        NodeList authnStatements = samlResponseDoc.getElementsByTagName("saml:AuthnStatement");
        Element userLevelAuthnStatement = (Element) authnStatements.item(0);
        Element passwordAuthnStatement = (Element) authnStatements.item(1);
        Element nameId = (Element) samlResponseDoc.getElementsByTagName("saml:NameID").item(0);
        Element userLevelAuthnContextClassRef =
                (Element) userLevelAuthnStatement.getElementsByTagName("saml:AuthnContextClassRef").item(0);
        Element passwordAuthnContextClassRef =
                (Element) passwordAuthnStatement.getElementsByTagName("saml:AuthnContextClassRef").item(0);

        assertNotNull(samlResponse);
        assertThat(samlResponse).isNotBlank();
        assertThat(response).isNotNull();
        assertThat(response.getAttribute("ID")).isEqualTo("_" + params.getId());
        assertThat(subjectConfirmationData).isNotNull();
        assertThat(subjectConfirmationData.getAttribute("Recipient")).isEqualTo(params.getAscUrl());
        assertThat(subjectConfirmationData.getAttribute("NotOnOrAfter")).isEqualTo("2026-03-18T10:20:30Z");
        assertThat(assertion).isNotNull();
        assertThat(assertion.getAttribute("ID")).isEqualTo("_" + params.getAssertionId());
        assertThat(authnStatements.getLength()).isEqualTo(2);
        assertThat(userLevelAuthnStatement).isNotNull();
        assertThat(userLevelAuthnStatement.getAttribute("AuthnInstant")).isEqualTo(created.toString());
        assertThat(userLevelAuthnStatement.getAttribute("SessionIndex")).isEqualTo("_" + params.getAssertionId());
        assertThat(userLevelAuthnStatement.hasAttribute("SessionNotOnOrAfter")).isFalse();
        assertThat(userLevelAuthnContextClassRef.getTextContent()).isEqualTo(params.getUserAuthnLevel());
        assertThat(passwordAuthnStatement).isNotNull();
        assertThat(passwordAuthnStatement.getAttribute("AuthnInstant")).isEqualTo(created.toString());
        assertThat(passwordAuthnStatement.getAttribute("SessionNotOnOrAfter")).isEqualTo("2026-03-18T10:20:30Z");
        assertThat(passwordAuthnStatement.getAttribute("SessionIndex")).isEqualTo("_" + params.getAssertionId());
        assertThat(passwordAuthnContextClassRef.getTextContent())
                .isEqualTo("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");
        assertThat(nameId).isNotNull();
        assertThat(nameId.getTextContent()).isEqualTo(params.getNameId());
        assertThat(decodedSamlResponse).doesNotContain("placeholder");
        assertThat(decodedSamlResponse).contains("urn:oasis:names:tc:SAML:2.0:ac:classes:Password");

        log.debug("encoded SamlResponse [{}]", samlResponse);
    }

}
