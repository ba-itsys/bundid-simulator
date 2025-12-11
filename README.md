# BundID-Simulator

## Note: This repo was moved

We have transfered this repo to our new organisation `ba-itsys`.
The location of the BundID-Simulator Docker image has been moved to `ghcr.io/ba-itsys/bundid-simulator`.

```diff
- ghcr.io/opdt/bundid-simulator:<tag>
+ ghcr.io/ba-itsys/bundid-simulator:<tag>
```

## Übersicht

> [!NOTE]
> Der *BundID-Simulator* ist ein fachlicher Mock, der die Funktion von *BundID* nachbildet. Er basiert auf den 
> Ausführungen in der BundID-Spezifikation.

Die Verwendung von *BundID* in *IdentityManagement*-Systemen gestaltet die Implementierung von Testumgebungen kompliziert.
Größere Herausforderungen bestehen mit der Nutzung von Vertrauenniveaus ab `STORK-QAA-Level-3`. Dann werden reale Personalausweise, 
Elster-Zertifikate oder Eidas-Kennungen benötigt, die zum einen beschafft werden müssen und zun anderen eindeutig für 
die nutzende Organisation sein sollten. Die Nutzung von *BundID* in Testumgebungen wird durch einen Mock, der die fachliche Funktion von 
*BundID* simuliert, erleichtert. Dieses Ziel verfolgt die hier vorgestellt Anwendung `BundID-Simulator`. Der 
`BundID-Simulator` nimmt SAML-Requests entgegen und sendet SAML-Responses laut *BundID*-Spezifikation zurück.

Der `BundID-Simulator` wurde von der *Bundesagentur für Arbeit* entwickelt und wird in Verbindung mit dem Produkt
*Keycloak* zur Identifizierung im Portal verwendet. Der Programmcode wird hier öffentlich abgelegt und kann von 
anderen Behörden verwendet werden, die sich in ähnlichen fachlichen Umfeldern bewegen.

### Hinweis

In der *Bundesagentur für Arbeit* wird diese hier auf *Github* bereitgestellte Version des *BundID-Simulators* in Testumgebungen
verwendet. Der Code wird aus diesem Grund fortlaufend weiterentwickelt.

## Architektur und Auslieferung

Der `BundID-Simulator` ist eine Java-Anwendung auf Basis von des Frameworks [Spring Boot](https://spring.io/projects/spring-boot).
Die Anwendung startet einen Webserver, der die grafische Oberfläche anzeigt. Die UI nutzt das CSS-Framework
[Bootstrap](https://getbootstrap.com/) sowie die Template-Engine *Thymeleaf* (Bestandteil von *Spring Boot*). 

Die Anwendung kann via Maven-Build aus dem Sourcecode gebaut werden. Dabei entsteht ein JAR-File, das alle 
Abhängigkeiten beinhaltet. Der Start erfolgt mit der Java-Runtime:

```
java -jar bundid-simulator.jar
```

Eine zweite Variante besteht in der Nutzung eines Docker-Image, das 
in der Github-Registry (siehe: https://github.com/ba-itsys/bundid-simulator/pkgs/container/bundid-simulator) veröffentlicht wird.

```
docker pull ghcr.io/ba-itsys/bundid-simulator:<tag>
```

User können das Image nutzen und in einem Kubernetes-Cluster mit angepasster Konfiguration deployen. Weitere Informationen
zu diesem Thema später.

## Funktionen und UI

Der `BundID-Simulator` wird als SSO-Provider von der Anwendung *Keycloak* per POST-Request aufgerufen. Der POST-Body 
enthält den SAML-Request. Enthalten sind u.a. das geforderte Mindestvertrauensniveau und die Rücksprung-Url des
Aufrufers. Die Anwendung zeigt eine UI:

![Startseite BundID-Simulator](/doc/picture01.jpg)

Im oberen Teil wird eine konfigurierbare Personenliste dargestellt. Die in der Abbildung gezeigten Daten sind in der 
Anwendung enthalten. In der Praxis kann eine Konfigurationsdatei mit anderen Daten verwendet werden. Im unteren 
Teil der Anwendung werden Details der Identifizierung festgelegt. Dazu gehören neben dem Status (`OK`, `CANCEL` oder `ERROR`) 
das Identifizierungsmittel (beispielweise EID für Identifizierungen mit dem Personalausweis). Für Eidas-Identifizierungen
wird zusätzlich das Eidas-Land sowie das Eidas-Vertrauensniveau nach BSI-Notiation (substantiell, hoch) angegeben. Die
Aktion *Person übernehmen* erstellt aus den Personen- und Identifizierungsdaten einen SAML-Response und leitet 
zum Aufrufer weiter.

> [!NOTE]
> Die SAML-Spezifikation kennt nur die Status `success` und `error`. Der Abbruch der Identifizierung in der BundID-Anwendung 
> erhält den SAML-Status `error`. Anhand des Textes in der Detailmessage kann zwischen Abbruch und einem technischen 
> Fehler unterschieden werden.

Das Element `bPK2` (bereichsspezifischen Personenkennzeichen 2. Version) identifiziert eine Person eindeutig. In den Stammdaten 
ist jeder Person ein `bPK2` zugeordnet. Um dennoch mehr als die hinterlegte Anzahl Personen verwenden zu können, wird
das Element *fachlicher Kontext* verwendet. Diese Zeichenkette wird als Suffix in der `bPK2` und in der Email-Adresse 
verwendet. So können unendlich viele Personen simuliert werden.

Wenn die Dateninhalte der definierten Personendaten nicht ausreichen, kann ein Datensatz bearbeitet werden. 
Dazu wird ein Datensatz als Vorlage gewählt und mit der Aktion *Person bearbeiten* können einzelne 
Datensatzbestandteile geändert werden. Die Aktion *Person übernehmen* nutzt diese Daten zur Erstellung
des SAML-Response. Das Element *fachlicher Kontext* gibt es in dieser Auswahl nicht. `bPK2` und Email-Adresse
sollten angepasst werden.

![Person bearbeiten](/doc/picture02.jpg)

## Personendaten anpassen 

Die Liste der angezeigten Personen wird in einer Konfiguration verwaltet. In der Anwendung ist eine Beispielliste 
mit 12 Personen (siehe: `src/main/resources/users.yml`) enthalten. Die Datei im yml-Format enthält eine Datensatzliste. Jeder
Datensatz hat folgendes Formart:

```yaml
- id: "U01"
  surname: "Neumann"
  givenname: "Maria"
  mail: "maria.neumenn@example.com"
  postalAddress: "Thomas-Mann-Straße 3"
  postalCode: "10409"
  localityName: "Berlin"
  country: "DE"
  personalTitle: ""
  gender: "2"
  birthdate: "1964-08-12"
  placeOfBirth: "Berlin"
  birthName: "Winter"
  bpk2: "BUNDIDSIM-U01"
```

Die Schlüssel und Werte/Formate entsprechen der BundID-Spezifikation. Dort sind ebenfalls Angaben zu Pflichtfeldern 
enthalten. Nutzer des `BundID-Simulators` können selbst Personendaten nach eigenen Vorgaben anlegen. Für 
Kubernetes-Deploymnents wird diese selbst erstellte Datei mit deployt und über die Springboot-Konfiguration referenziert.

## Lokale Entwicklung/Ausführung

### Java-Code in einer Entwicklungsumgebung

Zum Verständnis der Anwendung und zur Weiterentwicklung kann der Sourcecode von *Github* ausgecheckt und in eine 
Entwicklungsumgebung (beispielsweise *IntelliJ*) eingebunden werden. Die *SpringBoot*-Anwendug kann in der IDE gestartet 
werden. Der Aufruf der Url:

```
http://localhost:8080/saml
```

im Browser zeigt die UI. Zu beachten ist, dass der *BundID-Simulator* im realen Betrieb per POST aufgerufen wird. In
den POST-Parametern sind wichtige Daten für den Programmablauf enthalten. Beim lokalen Aufruf hingegen erfolgt der 
Aufruf mit der Methode *GET*. Dabei handelt es sich um einen *gefakten* Aufruf mit fest hinterlegten Parametern. Dieser 
Aufruf dient der einzig der UI-Darstellung.

### Lokales Ausführen mit Docker/Podman

Der Sourcecode kann lokal auch als Docker-Image gebaut und ausgeführt werden:

- Jar-File `bundid-simulator.jar` via `mvn clean package` bauen
- Docker-Image  mit `podman build -t bundid-simulator -f Dockerfile .` bauen.
- `BundID Simulator` starten:
```
podman run --rm -it -p 8080:8080 bundid-simulator
```
Wenn `Docker` verwendet wird, muss in den obigen Befehlen `podman` durch `docker` ersetzt werden.

## Künftige Weiterentwicklungen

Der Sourcecode enthält einige Stellen, die auf der BundID-Spezifikation beruhen. Gibt es seitens BundID fachliche 
Änderungen oder Erweiterungen, müssen Teile des Sourcecodes angepasst werden. Nachfolgend eine Aufzählung:

- `resources/field_definitions.yaml`: Enthält alle von BundID unterstützen Attribute 
- `recources/views/saml_response_template.xml`: XML-Vorlage für den SAML-Response mit Platzhaltern, die später durch Werte ersetzt werden
- `resources/views/saml_response_error_template.xml`: XML-Vorlage für den SAML-Response im Fehlerfall

## Konfigurationen in eigenen Umgebungen

### Keycloak

- Anbindung als Provider ohne Security

### Kubernetes

Folgendes Szenario ist als Beispiel zu sehen.

- Das Docker-Image kann als Kubernetes-Deployment ausgeführt werden
- für *LivenessProbe* und *ReadynessProbe* stehen die Endpunkte `/livez` und `/readyz` zur Verfügung

In jedem Fall muss eine Konfiguration `application.yaml` angegeben werden, die die in der Anwendung enthaltene 
Konfiguration überschreibt/ergänzt. Hier ein Beispiel:

```yaml
logging:
  structured:
    format:
      console: logstash
    json:
      add:
        context: oiam
  level:
    de.ba: info
    root: warn
spring:
  application:
    name: "ba-bundid-simulator"
    version: "${IMAGE_TAG}"
app:
  basepath: "/oiam/bundidsimulator"
  env: "Main"
  web:
    title: "BundID-Simulator"
  resources:
    userdefinition: "file:config/users.yaml"
```

Diese Konfigurationserweiterung beinhaltet folgende Bestandteile:

- Definition Logging (json-Logging)
- Festlegung der Log-Level
- Texte, die im Header/Footer dargestellt werden
- Ablageort der eignen User-Definition
- **Wichtig**: Der *basepath* gibt Path innerhalb des Deployments an, sodass die Webressourcen gefunden werden. 

Die Dateien `application.yaml` und `users.yaml` werden in eine Configmap gepackt und als Volume gemountet.
