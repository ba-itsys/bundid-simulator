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
package de.ba.oiam.bundidsim.model.view;

import de.ba.oiam.bundidsim.utils.AuthLevelTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Pattern;

@Component
@Slf4j
public class EditFormDataValidator implements Validator {

    private static final Pattern DATEPATTERN = Pattern.compile("(\\d{4}-(\\d{2}|xx)-(\\d{2}|xx))",
            Pattern.CASE_INSENSITIVE);

    @Override
    public boolean supports(Class<?> clazz) {
        // return clazz == FormData.class;
        return EditFormData.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        log.debug("start validate EditFormData...");
        EditFormData formData = (EditFormData) target;

        // Validierungen
        // Pflichtfelder in BundIdUser
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.bpk2", "");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.givenname", "");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.surname", "");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.birthdate", "");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.mail", "");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.birthdate", "");

        // Zusammenhangs-Validierungen
        validateAddress1(formData, errors);
        validateAddress2(formData, errors);
        validateEmailAddress(formData, errors);
        validateBirthDate(formData, errors);
        validateMandatoryFieldsForEid(formData, errors);

        // Spezielle Validierung, wenn als Identifizierungsmittel eIDAS ausgewählt wurde
        if (AuthLevelTools.IDENTIFICATION_EIDAS.equalsIgnoreCase(formData.getIdentifikationWith())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "eidasLoa", "");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "eidasCountry", "");
        }
    }

    private void validateAddress1(EditFormData formData, Errors errors) {
        // Wenn Anschrift vorhanden, dann auch Plz und Ort benötigt
        if (StringUtils.hasText(formData.getUser().getPostalAddress())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.postalCode", "");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.localityName", "");
        }
    }

    private void validateAddress2(EditFormData formData, Errors errors) {
        /*
        Für alle Identifizierungsmittel außer eIDAS muss die Adresse vorhanden sein.
         */
        if (!AuthLevelTools.IDENTIFICATION_EIDAS.equalsIgnoreCase(formData.getIdentifikationWith())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.postalAddress", "");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.postalCode", "");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.localityName", "");
        }
    }

    private void validateEmailAddress(EditFormData formData, Errors errors) {
        // Prüfung Email-Addresse
        String emailAddress = formData.getUser().getMail();
        if (StringUtils.hasText(emailAddress) && !EmailValidator.getInstance().isValid(emailAddress)) {
            log.debug("Email-Address [{}] invalid", emailAddress);
            errors.rejectValue("user.mail", "");
        }
    }

    private void validateBirthDate(EditFormData formData, Errors errors) {
        String birthdate = formData.getUser().getBirthdate();
        if (StringUtils.hasText(birthdate) && !DATEPATTERN.matcher(birthdate).matches()) {
            errors.rejectValue("user.birthdate", "");
        }

    }

    private void validateMandatoryFieldsForEid(EditFormData formData, Errors errors) {
        // bei eID sind Geburtsname und Geburtsort Pflichtfelder
        if (!AuthLevelTools.IDENTIFICATION_EID.equalsIgnoreCase(formData.getIdentifikationWith())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.placeOfBirth", "");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "user.birthName", "");
        }
    }

}
