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

import static org.assertj.core.api.Assertions.assertThat;

class TemplateFormatToolsTest {

    @Test
    void formatDate_shouldConvertValidDateFormat() {
        // Arrange
        TemplateFormatTools formatTools = TemplateFormatTools.getInstance();
        String inputDate = "2023-12-25";
        String expectedDate = "25.12.2023";

        // Act
        String result = formatTools.formatDate(inputDate);

        // Assert
        assertThat(result).isEqualTo(expectedDate);
    }
}
