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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.util.Base64;

/**
 * Tool zur Objekt-Serialisierung.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ObjectStringConverter {

    /**
     * Serialisiert ein Java-Object nach JSON (String) und liefert den
     * Base64-encodeten String.
     *
     * @param value
     * @return
     */
    @SneakyThrows
    public static String serializeAndEncode(Object value) {
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writer().writeValueAsString(value);
        return Base64.getEncoder().encodeToString(s.getBytes());
    }

    /**
     * wandelt eine Zeichenkette (Base64-encoded) in ein Java-Objekt um.
     *
     * @param data
     * @param toValueType
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T decodeAndDeserialize(String data, Class<T> toValueType) {
        String s = new String(Base64.getDecoder().decode(data.getBytes()));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(s, toValueType);
    }
}
