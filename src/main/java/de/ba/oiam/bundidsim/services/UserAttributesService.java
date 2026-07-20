package de.ba.oiam.bundidsim.services;

import de.ba.oiam.bundidsim.model.BundIdUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Klasse repräsentiert einen {@link BundIdUser} angereichert um weitere für
 * den SAML-Response relevante Daten (aus Field-Definition)
 */
@Service
@Slf4j
public class UserAttributesService {

    @Autowired
    private UserFieldDefinitionService userFieldDefinitionService;

    public static final String TRUSTLEVEL_NORMAL = "NORMAL";
    public static final String TRUSTLEVEL_HOCH = "HOCH";
    public static final String TRUSTLEVEL_SUBSTANTIELL = "SUBSTANTIELL";
    public static final String TRUSTLEVEL_UNTERGEORDNET = "UNTERGEORDNET";

    /**
     * build and return list of userattributes
     *
     * <p>Value TrustLevel: NORMAL SUBSTANTIELL HIGH UNTERGEORDNET
     *
     * @return
     */
    public List<UserAttribute> getUserAttributes(BundIdUser user, String trustLevel) {
        String[] fieldList = userFieldDefinitionService.getUserFieldNames();
        Map<String, UserAttribute> dict = createAttributeDict();
        List<UserAttribute> result = new ArrayList<>();

        for (String fieldName : fieldList) {
            String value = getValueFromField(fieldName, user);
            if (StringUtils.hasText(value)) {
                UserAttribute attribute = dict.get(fieldName);
                attribute.setValue(value);
                if (!StringUtils.hasText(attribute.getTrustLevel())) {
                    // Trustlevel nicht überschreiben!
                    attribute.setTrustLevel(trustLevel);
                }
                result.add(attribute);
            }
        }
        return result;
    }

    /**
     * Einlesen der Fielddefinitionen
     */
    private Map<String, UserAttribute> createAttributeDict() {
        List<UserFieldDefinitionService.FieldNode> listFieldNodes =
                userFieldDefinitionService.getFieldNodes();
        Map<String, UserAttribute> dict = new HashMap<>();
        // dict aufbauen
        listFieldNodes
                .forEach(
                        (node) -> {
                            dict.put(
                                    node.getName(),
                                    UserAttribute.builder()
                                            .name(node.getAttributes().getName())
                                            .friendlyName(node.getAttributes().getFriendlyName())
                                            .trustLevel(node.getAttributes().getTrustLevel())
                                            .build());
                        });
        return dict;
    }

    /**
     * Ermittelt den Wert einer Variable per Reflection
     *
     * @param fieldName
     * @return
     */
    private String getValueFromField(String fieldName, BundIdUser user) {
        try {
            Class obj = user.getClass();
            Field field = obj.getDeclaredField(fieldName);
            // make private field accessible
            field.setAccessible(true);
            return (String) field.get(user);
        } catch (NoSuchFieldException _) {
            return null;
        } catch (IllegalAccessException e2) {
            throw new IllegalStateException(e2);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class UserAttribute {
        private String name; // fielddefinition
        private String friendlyName; // fielddefinition
        private String trustLevel; // fielddefinition
        private String value; // User
    }
}
