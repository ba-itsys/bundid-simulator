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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {UserDefinitionService.class})
class UserDefinitionTest {

    @Autowired
    private UserDefinitionService service;

    @Test
    void readUserList() {
        List<BundIdUser> list = service.getUserList();
        assertThat(list).hasSizeGreaterThan(0);
    }

    @Test
    void readUserMap() {
        Map<String, BundIdUser> map = service.getUserMap();
        assertThat(map).hasSizeGreaterThan(0);
    }
}
