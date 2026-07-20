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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Repräsentiert den Saml-Status für den SamlResponse.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Status {

    public enum StatusType {
        OK, CANCEL, ERROR
    }

    private StatusType statusType;
    private String majorCode;
    private String minorCode;
    private String message;

    public boolean isStatusOK() {
        return getStatusType() == StatusType.OK;
    }

    public static Status buildOkStatus() {
        return Status.builder()
                .statusType(StatusType.OK)
                .majorCode("urn:oasis:names:tc:SAML:2.0:status:Success")
                .build();
    }

    public static Status buildCancelStatus() {
        return Status.builder()
                .statusType(StatusType.CANCEL)
                .majorCode("urn:oasis:names:tc:SAML:2.0:status:Requester")
                .minorCode("urn:oasis:names:tc:SAML:2.0:status:AuthnFailed")
                .message("Authentication was canceled by the user.")
                .build();
    }

    public static Status buildErrorStatus() {
        return Status.builder()
                .statusType(StatusType.ERROR)
                .majorCode("urn:oasis:names:tc:SAML:2.0:status:Requester")
                .minorCode("urn:oasis:names:tc:SAML:2.0:status:NoAuthnContext")
                .message("An error has occurred.")
                .build();
    }

    public static Status createStatusFromKey(String key) {
        return switch (key) {
            case "OK" -> buildOkStatus();
            case "CANCEL" -> buildCancelStatus();
            default -> buildErrorStatus();
        };
    }

}
