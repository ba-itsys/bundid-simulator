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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ObjectStringConverterTest {


    @Test
    void serializeTest() {

        /*
        Objekt serialisieren
        Base64-encoden
         */
        ConfigClass config = ConfigClass.builder()
                .param1("Value 12")
                .param2("Value 24")
                .build();

        String result = ObjectStringConverter.serializeAndEncode(config);
        assertThat(result).isNotNull();
        log.debug("Config encoded: [{}]", result);
    }

    @Test
    void deserializeTest() {
        ConfigClass config = ConfigClass.builder()
                .param1("Parameter 1")
                .param2("Parameter 2")
                .build();

        String s = ObjectStringConverter.serializeAndEncode(config);
        ConfigClass testConfig = ObjectStringConverter.decodeAndDeserialize(s, ConfigClass.class);
        log.debug("Config: [{}]", testConfig);

        assertThat(testConfig.getParam1()).isEqualTo("Parameter 1");
        assertThat(testConfig.getParam2()).isEqualTo("Parameter 2");
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class ConfigClass {
        private String param1;
        private String param2;
    }
}
