package de.ba.oiam.bundidsim.services;

import de.ba.oiam.bundidsim.model.BundIdUser;
import de.ba.oiam.bundidsim.utils.ResourceUtils;
import java.util.*;
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

/** Liest User-Daten aus der Ressource "users.yml". */
@Slf4j
@Service
public class UserDefinitionService {

  @Value("${app.resources.userdefinition}")
  private Resource fileResource;

  public Map<String, BundIdUser> getUserMap() {
    Map<String, BundIdUser> userMap = new HashMap<>();

    for (BundIdUser item : getUserList()) {
      userMap.put(item.getId(), item);
    }
    return userMap;
  }

  public BundIdUser getUserById(String id) {
    return getUserMap().get(id);
  }

  /**
   * liest Yaml-Inhalt aus "users.yml" in die Datenstruktur {@link BundIdUser} ein. Nur einige
   * Personendaten werden dabei befüllt.
   */
  public List<BundIdUser> getUserList() {
    log.debug("init UserProfilesDefinitionService");

    Constructor constructor = new Constructor(UsersNode.class, new LoaderOptions());
    TypeDescription configDesc = new TypeDescription(UsersNode.class);
    configDesc.addPropertyParameters("users", BundIdUser.class);
    constructor.addTypeDescription(configDesc);

    Yaml yaml = new Yaml(constructor);
    UsersNode nodes = yaml.load(ResourceUtils.loadResourceToString(fileResource));
    List<BundIdUser> userList = nodes.getUsers();
    // Sortieren nach "id".
    userList.sort(Comparator.comparing(BundIdUser::getId));

    log.debug("{} users from resource read.", userList.size());
    return userList;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static final class UsersNode {
    private List<BundIdUser> users;
  }
}
