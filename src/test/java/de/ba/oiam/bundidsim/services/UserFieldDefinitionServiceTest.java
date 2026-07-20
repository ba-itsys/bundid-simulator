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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserFieldDefinitionService.class})
class UserFieldDefinitionServiceTest {

    @Autowired
    private UserFieldDefinitionService service;

    @Test
    void getFieldNodes() {
        List<UserFieldDefinitionService.FieldNode> nodes = service.getFieldNodes();
        assertThat(nodes).hasSizeGreaterThan(0);

        // test first node
        UserFieldDefinitionService.FieldNode node = nodes.getFirst();

        assertThat(node.getName()).isEqualTo("surname");
        assertThat(node.getAttributes().getName()).isEqualTo("urn:oid:2.5.4.4");
        assertThat(node.getAttributes().getFriendlyName()).isEqualTo("surname");
    }
}
