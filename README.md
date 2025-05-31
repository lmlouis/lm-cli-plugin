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

## 🧪 Utilisation dans un projet Maven
Ajoute ce plugin dans la section <build><plugins> de ton pom.xml :
```xml
<build>
  <plugins>
    <plugin>
      <groupId>dev.lmlouis</groupId>
      <artifactId>lm-cli-plugin</artifactId>
      <version>1.0.0</version>
    </plugin>
  </plugins>
</build>
```