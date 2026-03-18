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
package de.ba.oiam.bundidsim.model;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class SamlResponseValues {
    private String id;
    private String assertionId;
    private String requestId;
    private String spEntityId;
    private String idpId;
    private Instant created;
    private String ascUrl;
    private String nameId;
    private String authnInstant;
    private String sessionNotOnOrAfter;
    private String sessionIndex;
    private String subjectConfirmationNotOnOrAfter;
    private String subjectConfirmationRecipient;
    private String userAuthnLevel;
    private String user;
}
