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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse stellt Methoden bereit, die in Thymeleaf-Templates verwendet werden können.
 */
@Slf4j
public class TemplateFormatTools {

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("dd.mm.yyyy");

    @Getter
    private static TemplateFormatTools instance = new TemplateFormatTools();

    /**
     * konvertiert Date YYYY-MM-DD in DD.MM.YYYY für Darstellung
     *
     * @param date
     * @return
     */
    public String formatDate(String date) {
        try {
            Date d = sdf1.parse(date);
            return sdf2.format(d);
        } catch (ParseException _) {
            return "Datum";
        }
    }
}
