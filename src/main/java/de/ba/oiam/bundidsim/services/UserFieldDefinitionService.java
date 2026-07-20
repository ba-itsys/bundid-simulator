package de.ba.oiam.bundidsim.services;

import de.ba.oiam.bundidsim.utils.ResourceUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Service stellt
 */
@Slf4j
@Service
public class UserFieldDefinitionService {

    @Value("classpath:field_definitions.yml")
    private Resource fileResource;

    /**
     * liefert ein Array mit Fieldnames
     *
     * @return
     */
    public String[] getUserFieldNames() {
        List<String> fieldNames = new ArrayList<>();
        readFieldNodes().forEach((node) -> fieldNames.add(node.getName()));
        return fieldNames.toArray(new String[0]);
    }

    public List<FieldNode> getFieldNodes() {
        return readFieldNodes();
    }

    /**
     * Liste der FieldNodes wird aus der Yaml-Datei eingelesen.
     *
     * @return
     */
    private List<FieldNode> readFieldNodes() {
        List<FieldNode> listFieldNodes;

        Constructor constructor = new Constructor(FieldsNode.class, new LoaderOptions());
        TypeDescription configDesc = new TypeDescription(FieldsNode.class);
        configDesc.addPropertyParameters("fields", FieldNode.class);
        configDesc.addPropertyParameters("attributes", FieldAttributes.class);
        constructor.addTypeDescription(configDesc);
        Yaml yaml = new Yaml(constructor);
        FieldsNode fields = yaml.load(ResourceUtils.loadResourceToString(fileResource));
        listFieldNodes = fields.getFields();
        log.debug("{} fielddefinitions readed.", listFieldNodes.size());
        return listFieldNodes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class FieldsNode {
        private List<FieldNode> fields;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class FieldNode {
        private String name;
        private FieldAttributes attributes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static final class FieldAttributes {
        private String name;
        private String friendlyName;
        private String trustLevel;
    }
}
