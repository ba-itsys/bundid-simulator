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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class ResourceUtilsTest {

    @Test
    void shouldReadValidUtf8Resource() {
        String expectedContent = "Test content";
        Resource resource = new ByteArrayResource(expectedContent.getBytes(StandardCharsets.UTF_8));

        String result = ResourceUtils.loadResourceToString(resource);

        assertThat(result).isEqualTo(expectedContent);
    }

    @Test
    void shouldThrowUncheckedIOExceptionWhenResourceIsNull() {
        Resource resource = null;

        assertThatThrownBy(() -> ResourceUtils.loadResourceToString(resource))
                .isInstanceOf(NullPointerException.class);
    }
}
