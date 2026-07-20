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
package de.ba.oiam.bundidsim.utils;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class XmlParserToolsTest {

    @Test
    void parseValidXmlString() throws ParserConfigurationException, IOException, SAXException {
        String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><test>Hello World</test></root>";

        Document result = XmlParserTools.parseXML(validXml);

        assertThat(result.getDocumentElement().getNodeName()).isEqualTo("root");
        assertThat(result.getElementsByTagName("test").item(0).getTextContent()).isEqualTo("Hello World");
    }

    @Test
    void findValueByTagnameNonExistentTag() throws ParserConfigurationException, IOException, SAXException {
        String validXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><test>Hello World</test></root>";
        Document doc = XmlParserTools.parseXML(validXml);

        String result = XmlParserTools.findValueByTagname(doc, "nonexistent");

        assertThat(result).isNull();
    }

    @Test
    void parseXml() throws Exception {
        String samlRequest = "PHNhbWxwOkF1dGhuUmVxdWVzdCB4bWxuczpzYW1scD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOnByb3RvY29sIiB4bWxucz0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgeG1sbnM6c2FtbD0idXJuOm9hc2lzOm5hbWVzOnRjOlNBTUw6Mi4wOmFzc2VydGlvbiIgQXNzZXJ0aW9uQ29uc3VtZXJTZXJ2aWNlVVJMPSJodHRwczovL3NlY3VyZS5kZXYub2NwLndlYmFwcC5pZHN0LmliYWludGVybi5kZS9laWQtaW5mcmEvYXV0aC9yZWFsbXMvQnVuZElELUlkZW50L2Jyb2tlci9idW5kaWQvZW5kcG9pbnQiIEF0dHJpYnV0ZUNvbnN1bWluZ1NlcnZpY2VJbmRleD0iMCIgRGVzdGluYXRpb249Imh0dHBzOi8vc2VjdXJlLmRldi5vY3Aud2ViYXBwLmlkc3QuaWJhaW50ZXJuLmRlL2VpZC1pbmZyYS9pZHBtb2NrL3NhbWwiIEZvcmNlQXV0aG49ImZhbHNlIiBJRD0iSURfNTVhYTFkODYtYmIzZi00YmJkLTkyMDktNWNlZTVkMzMxN2E3IiBJc3N1ZUluc3RhbnQ9IjIwMjMtMTItMTFUMTM6MzM6MjQuODUyWiIgUHJvdG9jb2xCaW5kaW5nPSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoyLjA6YmluZGluZ3M6SFRUUC1QT1NUIiBWZXJzaW9uPSIyLjAiPjxzYW1sOklzc3Vlcj5odHRwczovL3NlY3VyZS5kZXYub2NwLndlYmFwcC5pZHN0LmliYWludGVybi5kZS9laWQtaW5mcmEvYXV0aC9yZWFsbXMvQnVuZElELUlkZW50PC9zYW1sOklzc3Vlcj48c2FtbHA6RXh0ZW5zaW9ucz48YWtkYjpBdXRoZW50aWNhdGlvblJlcXVlc3QgeG1sbnM6YWtkYj0iaHR0cHM6Ly93d3cuYWtkYi5kZS9yZXF1ZXN0LzIwMTgvMDkiIFZlcnNpb249IjIiPjxha2RiOlJlcXVlc3RlZEF0dHJpYnV0ZXM+PGFrZGI6UmVxdWVzdGVkQXR0cmlidXRlIE5hbWU9InVybjpvaWQ6MS4yLjQwLjAuMTAuMi4xLjEuMjI1NTk5IiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48YWtkYjpSZXF1ZXN0ZWRBdHRyaWJ1dGUgTmFtZT0idXJuOm9pZDoxLjMuNi4xLjQuMS4yNTQ4NC40OTQ0NTAuMTAuMSIgUmVxdWlyZWRBdHRyaWJ1dGU9ImZhbHNlIi8+PGFrZGI6UmVxdWVzdGVkQXR0cmlidXRlIE5hbWU9InVybjpvaWQ6MS4yLjQwLjAuMTAuMi4xLjEuMjYxLjk0IiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48YWtkYjpSZXF1ZXN0ZWRBdHRyaWJ1dGUgTmFtZT0idXJuOm9pZDoyLjUuNC43IiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48YWtkYjpSZXF1ZXN0ZWRBdHRyaWJ1dGUgTmFtZT0idXJuOm9pZDoyLjUuNC4xNiIgUmVxdWlyZWRBdHRyaWJ1dGU9ImZhbHNlIi8+PGFrZGI6UmVxdWVzdGVkQXR0cmlidXRlIE5hbWU9InVybjpvaWQ6MS4zLjYuMS40LjEuMjU0ODQuNDk0NDUwLjIiIFJlcXVpcmVkQXR0cmlidXRlPSJmYWxzZSIvPjxha2RiOlJlcXVlc3RlZEF0dHJpYnV0ZSBOYW1lPSJ1cm46b2lkOjIuNS40LjQiIFJlcXVpcmVkQXR0cmlidXRlPSJmYWxzZSIvPjxha2RiOlJlcXVlc3RlZEF0dHJpYnV0ZSBOYW1lPSJ1cm46b2lkOjEuMy42LjEuNS41LjcuOS4yIiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48YWtkYjpSZXF1ZXN0ZWRBdHRyaWJ1dGUgTmFtZT0idXJuOm9pZDoyLjUuNC4xNyIgUmVxdWlyZWRBdHRyaWJ1dGU9ImZhbHNlIi8+PGFrZGI6UmVxdWVzdGVkQXR0cmlidXRlIE5hbWU9InVybjpvaWQ6MC45LjIzNDIuMTkyMDAzMDAuMTAwLjEuNDAiIFJlcXVpcmVkQXR0cmlidXRlPSJmYWxzZSIvPjxha2RiOlJlcXVlc3RlZEF0dHJpYnV0ZSBOYW1lPSJ1cm46b2lkOjEuMi40MC4wLjEwLjIuMS4xLjU1IiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48YWtkYjpSZXF1ZXN0ZWRBdHRyaWJ1dGUgTmFtZT0idXJuOm9pZDoyLjUuNC40MiIgUmVxdWlyZWRBdHRyaWJ1dGU9ImZhbHNlIi8+PGFrZGI6UmVxdWVzdGVkQXR0cmlidXRlIE5hbWU9InVybjpvaWQ6MS4yLjQwLjAuMTAuMi4xLjEuMjI1NTY2IiBSZXF1aXJlZEF0dHJpYnV0ZT0iZmFsc2UiLz48L2FrZGI6UmVxdWVzdGVkQXR0cmlidXRlcz48L2FrZGI6QXV0aGVudGljYXRpb25SZXF1ZXN0Pjwvc2FtbHA6RXh0ZW5zaW9ucz48c2FtbHA6TmFtZUlEUG9saWN5IEFsbG93Q3JlYXRlPSJmYWxzZSIgRm9ybWF0PSJ1cm46b2FzaXM6bmFtZXM6dGM6U0FNTDoxLjE6bmFtZWlkLWZvcm1hdDp1bnNwZWNpZmllZCIvPjxzYW1scDpSZXF1ZXN0ZWRBdXRobkNvbnRleHQgQ29tcGFyaXNvbj0ibWluaW11bSI+PHNhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+U1RPUkstUUFBLUxldmVsLTM8L3NhbWw6QXV0aG5Db250ZXh0Q2xhc3NSZWY+PC9zYW1scDpSZXF1ZXN0ZWRBdXRobkNvbnRleHQ+PC9zYW1scDpBdXRoblJlcXVlc3Q+";
        String decodedSamlRequest = new String(Base64.getDecoder().decode(samlRequest));

        Document doc = XmlParserTools.parseXML(decodedSamlRequest);
        Element e = doc.getDocumentElement();

        String serviceUrl = e.getAttribute("AssertionConsumerServiceURL");
        assertThat(serviceUrl)
                .isNotBlank()
                .matches(".*endpoint$");

        String id = e.getAttribute("ID");
        assertThat(id)
                .isNotBlank()
                .matches("^ID_.*");

        String valueIssuer = XmlParserTools.findValueByTagname(doc, "saml:Issuer");
        String valueReqAutnLevel = XmlParserTools.findValueByTagname(doc, "samlp:RequestedAuthnContext");

        assertThat(valueReqAutnLevel).isEqualToIgnoringCase("STORK-QAA-Level-3");
        assertThat(valueIssuer).isEqualToIgnoringCase("https://secure.dev.ocp.webapp.idst.ibaintern.de/eid-infra/auth/realms/BundID-Ident");

    }
}
