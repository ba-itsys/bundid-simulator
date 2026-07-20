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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static de.ba.oiam.bundidsim.services.UserAttributesService.TRUSTLEVEL_HOCH;
import static de.ba.oiam.bundidsim.services.UserAttributesService.UserAttribute;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserAttributesService.class, UserFieldDefinitionService.class})
class UserAttributesServiceTest {

    @Autowired
    private UserAttributesService service;

    @Test
    void getUserAttributes() {
        BundIdUser user = BundIdUser.builder()
                .surname("Schmidt")
                .givenname("Helmut")
                .gender("1")
                .build();

        String trustLevel = TRUSTLEVEL_HOCH;
        List<UserAttribute> list = service.getUserAttributes(user, trustLevel);

        assertThat(list).hasSize(3);

        UserAttribute u1 = list.getFirst();
        assertThat(u1.getName()).isEqualTo("urn:oid:2.5.4.4");
        assertThat(u1.getFriendlyName()).isEqualTo("surname");
        assertThat(u1.getTrustLevel()).isEqualTo(trustLevel);
        assertThat(u1.getValue()).isEqualTo("Schmidt");

        UserAttribute u2 = list.get(1);
        assertThat(u2.getName()).isEqualTo("urn:oid:2.5.4.42");
        assertThat(u2.getFriendlyName()).isEqualTo("givenName");
        assertThat(u2.getTrustLevel()).isEqualTo(trustLevel);
        assertThat(u2.getValue()).isEqualTo("Helmut");

        UserAttribute u3 = list.get(2);
        assertThat(u3.getName()).isEqualTo("urn:oid:1.3.6.1.4.1.33592.1.3.5");
        assertThat(u3.getFriendlyName()).isEqualTo("gender");
        assertThat(u3.getTrustLevel()).isEqualTo("UNTERGEORDNET");
        assertThat(u3.getValue()).isEqualTo("1");

    }
}
