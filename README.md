# lm-cli-plugin

Plugin Maven pour télécharger automatiquement le binaire `lm-cli` depuis les releases GitHub.

## 🚀 Fonctionnalités

- Télécharge le binaire `lm-cli` depuis [https://github.com/lmlouis/lm-cli/releases](https://github.com/lmlouis/lm-cli/releases)
- Installe le binaire dans `target/lm-cli-bin/lm-cli`
- Rend le fichier exécutable

---

## 🔧 Installation locale

Pour utiliser ce plugin dans tes projets Maven, commence par l'installer localement :

```bash
git clone https://github.com/lmlouis/lm-cli-plugin.git
cd lm-cli-plugin
mvn clean install
```

## 🧪 Publier le plugin
Ajoute  distributionManagement à la fin du pom.xml:
```xml
  <distributionManagement>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/lmlouis/lm-cli-plugin</url>
    </repository>
</distributionManagement>
```
edite le fichier settings.xml
```bash
idea ~/.m2/settings.xml 
```
en ajoutant le ghp Personal Access Token de ton github
```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>github</id>
            <username>lmlouis</username>
            <password>ghp Personal Access Token</password>
        </server>
    </servers>

</settings>
```
deploi le package avec la commande maven 
```bash
mvn clean deploy
```
## 🧪 Utilisation dans un projet Maven
Ajoute ce plugin dans la section <build><plugins> de ton pom.xml :
```xml
<build>
    <plugins>
        <plugin>
            <groupId>dev.lmlouis</groupId>
            <artifactId>lm-cli-plugin</artifactId>
            <version>1.0.2</version>
            <executions>
                <execution>
                    <id>install-lm-cli</id>
                    <goals>
                        <goal>install-lm-cli</goal>
                    </goals>
                    <phase>initialize</phase>
                    <configuration>
                        <!-- Facultatif : tu peux forcer une version spécifique ici -->
                        <version>1.0.4</version>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```


Le ZIP est téléchargé dans outputDirectory/lm-cli-source/lm-cli-<version>.zip.
Le ZIP est extrait dans ce même dossier lm-cli-source.
Le fichier ZIP est supprimé après extraction.
La méthode newFile protège contre l’attaque Zip Slip (extraction hors dossier prévu).
Le plugin logue chaque étape.