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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Utils für den Umgang mit Vertrauensniveaus
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class AuthLevelTools {

    public static final String STORK_1 = "STORK-QAA-Level-1";   // Vertrauensniveau: NIEDRIG
    public static final String STORK_3 = "STORK-QAA-Level-3";   // Vertrauensniveau: SUBSTANTIELL
    public static final String STORK_4 = "STORK-QAA-Level-4";   // Vertrauensniveau HOCH

    public static final String LOA_HIGH = "HIGH";
    public static final String LOA_SUBSTANTIAL = "SUBSTANTIAL";
    public static final String LOA_LOW = "LOW";

    public static final String IDENTIFICATION_BENUTZERKENNUNG = "Benutzername";
    public static final String IDENTIFICATION_EID = "eID";
    public static final String IDENTIFICATION_SMARTEID = "Smart-eID";
    public static final String IDENTIFICATION_ELSTER = "Elster";
    public static final String IDENTIFICATION_EIDAS = "eIDAS";
    public static final String IDENTIFICATION_FINK = "FINK";

    /**
     * liefert eine Liste mit key/Value für die UI-Darstellung des Identifizierungsmittels in
     * Abhängigkeit des angeforderten Vertrauensniveaus.
     *
     * @param minLevel
     * @return
     */
    public static String[][] createIdentificationWithList(String minLevel) {
        List<String[]> resultList = new ArrayList<>();

        if (!StringUtils.hasText(minLevel) || STORK_1.equalsIgnoreCase(minLevel)) {
            // Kein Vertrauensniveau angegeben
            resultList.add(new String[]{IDENTIFICATION_BENUTZERKENNUNG, "Benutzername"});
            resultList.add(new String[]{IDENTIFICATION_ELSTER, "Elster"});
            resultList.add(new String[]{IDENTIFICATION_SMARTEID, "Smart eID"});
        } else if (STORK_3.equalsIgnoreCase(minLevel)) {
            resultList.add(new String[]{IDENTIFICATION_ELSTER, "Elster"});
            resultList.add(new String[]{IDENTIFICATION_SMARTEID, "Smart eID"});
        }

        resultList.add(new String[]{IDENTIFICATION_EID, "EID (Ausweis)"});
        resultList.add(new String[]{IDENTIFICATION_EIDAS, "eIDAS"});
        resultList.add(new String[]{IDENTIFICATION_FINK, "FINK"});

        return resultList.toArray(new String[resultList.size()][2]);
    }

    public static String getAuthnLevelByIdentificationMethod(String method) {
        if (!StringUtils.hasText(method)) {
            return STORK_1;
        }

        switch (method) {
            case IDENTIFICATION_EIDAS, IDENTIFICATION_EID, IDENTIFICATION_FINK -> {
                return STORK_4;
            }
            case IDENTIFICATION_SMARTEID, IDENTIFICATION_ELSTER -> {
                return STORK_3;
            }
            case IDENTIFICATION_BENUTZERKENNUNG -> {
                return STORK_1;
            }
        }
        return STORK_1;
    }

    public static String getAuthnLevelByLoa(String loa) {
        return switch (loa) {
            case LOA_HIGH -> STORK_4;
            case LOA_SUBSTANTIAL -> STORK_3;
            default -> STORK_1;
        };
    }

}
