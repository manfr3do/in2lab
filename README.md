# Beispielprojekt mit Java, SpringBoot und Gradle

Informationssysteme II, Bachelor Wirtschaftsinformatik

Stefan Sarstedt, stefan.sarstedt(at)haw-hamburg.de  
Nick Alexander, nick.alexander(at)haw-hamburg.de

## A. Einrichtung des JDK und Ausführen der Tests

1. Installiere Java (JDK 21) auf Deinem Rechner:
    - entweder (**ohne Gewähr, aber einfacher als unten, wenn es klappt :-)**:
        - macOS/Linux: führe im Terminal `curl -Ls https://sh.jbang.dev | bash -s - jdk install 21` aus
        - Windows: führe im Terminal (Powershell/PS, nicht cmd/command)
          `iex "& { $(iwr https://ps.jbang.dev) } jdk install 21"` aus (inklusive der Anführungszeichen!)
    - oder:
        - Installiere das [OpenJDK 21](https://jdk.java.net/21/) - nicht das Java Runtime Environment (JRE)
        - Verwende, wenn möglich, dazu einen Paketmanager (unter macOS/Linux bspw. [Homebrew](https://brew.sh)
          oder [SDKMAN!](https://sdkman.io), unter Windows bspw. [Chocolatey](https://community.chocolatey.org)
          oder [Scoop](https://scoop.sh))
        - Unter Windows müssen Umgebungsvariablen gesetzt werden, prüfe dies nach Deiner Installation, ansonsten können
          die Befehle (`java`, `javac`) nicht im Terminal verwendet werden:
            - JAVA_HOME setzen und den Compiler in den PATH
              aufnehmen ([Anleitung hier](https://tecadmin.net/set-java-home-on-windows/)); verwende dort statt des in
              den Screenshots gezeigten `jdk1.8.0_121` entsprechend deine installierte Version!
            - Im Normalfall sollte diese Aufgabe der Paketmanager (s.o.) erledigt haben!

2. Öffne ein Terminal-Fenster.
    - Unter Windows: Nutze die Windows-Kommandozeile (cmd) und nicht die Powershell (PS)! Falls du dich in einer
      Powershell befindest (sichtbar durch das `PS` am Zeilenanfang), rufe `cmd` auf, um eine Windows-Kommandozeile zu
      öffnen. Bei Änderungen der Systemeinstellungen (JAVA_HOME, PATH, ...) muss das Terminal neu geöffnet werden, damit
      die Änderungen effektiv werden.
    - Falls du noch nicht sicher im Umgang mit einem Terminal bist (Verzeichnisse wechseln, anlegen, ansehen, etc.),
      schaue dir ein Tutorial wie
      z.B. [dieses für Windows](https://www.makeuseof.com/tag/a-beginners-guide-to-the-windows-command-line/)
      oder [dieses für Linux](https://ubuntu.com/tutorials/command-line-for-beginners#1-overview)
      oder [diese für macOS](https://www.makeuseof.com/tag/mac-terminal-commands-cheat-sheet/) an.

3. Klone unser Projekt:
    ```bash
    git clone https://git.haw-hamburg.de/lehre-sose-25/srs/in2/in2lab
    ```

4. Prüfe mittels `javac -version` (vergiss das "c" nicht!), ob Du das korrekte JDK verwendest! Falls nicht, achte auf
   die korrekte Einrichtung des JDK (Punkt 1) und ob du in der richtigen Shell (unter Windows: `cmd` anstelle von `PS`)
   bist.

5. Wechsle in das Projektverzeichnis (dort liegt u.a. die Datei `build.gradle`)

6. Übersetze das Projekt und führe die Tests im Terminal aus mittels
     ```bash
     ./gradlew clean build (unter Linux/macOS, bei Bedarf dort zuvor "chmod +x ./gradlew" ausf&uuml;hren, 
                            um die Ausf&uuml;hrungsberechtigung zu setzen)
     ```
   bzw.
     ```bash
     gradlew.bat clean build (unter Windows cmd/command)
     ```
   Gradlew/Gradle ist ein Tool zur Automatisierung (ähnlich `maven`, `make`, `npm`, `msbuild`, ...) und übersetzt das
   Projekt, führt die Tests aus und erzeugt eine Jar-Datei aus den Quellen. Informationen zu gradle findest
   Du [hier](https://gradle.org). Wesentlich ist die Datei `build.gradle`, in der die Projektabhängigkeiten zu externen
   Bibliotheken und Tasks definiert werden. Durch das Java-Plugin stehen Tasks zur Übersetzung, Starten der Applikation,
   etc. zur Verfügung. Du kannst alles verfügbaren Tasks mittels `./gradlew (gradlew.bat) tasks` auflisten.

   Es sollte `Build Successful` erscheinen (falls nein, prüfe noch einmal die vorherigen Punkte). Die erste Ausführung
   des Gradle-Wrappers `gradlew` dauert etwas länger, da zunächst die Gradle-Distribution und dann die abhängigen
   Java-Bibliotheken geladen werden (später kommen sie aus dem lokalen Cache).  
   <br />
   Falls ein Fehler wie `Execution failed for task ':bootJarMainClassName'... Could not resolve ...` auftritt, blockiert
   wahrscheinlich deine Firewall das Herunterladen der Depedencies. Problem und Lösung
   siehe [hier](https://stackoverflow.com/questions/25243342/gradle-build-is-failing-could-not-resolve-all-dependencies-for-configuration):

## B. Einrichtung einer Entwicklungsumgebung (IDE - Integrated Development Environment)

1. Installiere lokal auf Deinem Rechner (achte auf die aktuellen Versionen!):
    - (empfohlen) Jetbrains IntelliJ IDEA **Ultimate**(!), aktuelle Version: https://www.jetbrains.com/idea/ (du kannst
      dies mit Deiner `haw-hamburg.de`-Adresse kostenlos nutzen)
        - Evtl. hattest du schon einmal ein JetBrains-Studierenden-Abo. Falls es abgelaufen ist, kannst du es in deinem
          Profil auf der JetBrains-Seite verlängern.
    - (alternativ) Eclipse mit Lombok
      Plugin: https://projectlombok.org ([Anleitung hier](https://projectlombok.org/setup/intellij)).

2. Starte IntelliJ, **aber öffne das Projekt noch nicht**!

3. Aktiviere in IntelliJ bei den Preferences unter `Editor->Code Style` auf dem Tab `Formatter` die Option
   `Turn formatter on/off with markers in code comments`. Falls diese Option nicht gesetzt ist, führt dies in den
   REST-assured-Testfällen zu unschönen Code-Reformatierungen, die das Lesen dieser Testfälle erschweren.

4. Importiere nun Dein Projekt in IntelliJ (`File->New->Project from Existing Sources`).
    - Öffne unbedingt den Hauptordner, in dem die build.gradle, src-Ordner etc. liegen - nicht einen übergeordneten
      Ordner! Sonst erkennt IntelliJ das Projekt nicht.
    - Es dauert etwas beim ersten Laden.

5. Aktiviere bei den Preferences unter `Build,Execution,Deployment->Compiler->Annotation Processors` das Annotation
   Processing (`Enable annotation processing`). Hierdurch erkennt IntelliJ die erzeugten Lombok-Artefakte korrekt und
   erzeugt keine Warnungen/Fehler mehr im Editor aufgrund (fälschlich) "fehlender" Getter/Setter. Evtl. ist das in
   deiner IntelliJ Ultimate bereits voreingestellt.

6. Setze bei den Preferences unter `Build,Execution,Deployment->Build Tools->Gradle` die Optionen `Build and run using`
   und `Run tests using` auf `Gradle`.

7. Setze unter `File->Project Structure` das JDK (Option `Project Settings->Project`) auf deine JDK-Version (
   entsprechend dein heruntergeladenes JDK - evtl. musst du das JDK erst hinzufügen: Option `Platform Settings->SDKs`).

8. Öffne ein Terminal in IntelliJ (`View->Tool Windows->Terminal` im Menü). Führe die Tests wie unter A.5 beschrieben
   hier im Terminal aus.
    - Unter Windows: evtl. nutzt IntelliJ die Windows Powershell PS. In diesem Falle rufst du dort wieder `cmd`auf (
      siehe A.2) oder kannst auch das Default-Terminal von IntelliJ unter `Preferences->Tools->Terminal->Shell Path`
      ändern.

9. Du kannst auch die Tests durch die IDE ausführen lassen. Gehe dazu mit der rechten Maustaste auf Dein Projekt und
   wähle `Run Tests in se1lab`.
