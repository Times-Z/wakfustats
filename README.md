# BorderlessFX

Application JavaFX démontrant une fenêtre sans bordure, déplaçable, avec opacité configurable et toujours au premier plan.

## Caractéristiques

- Fenêtre sans bordure standard du système d'exploitation
- Contrôle d'opacité via un slider
- Capacité de déplacer la fenêtre en la faisant glisser
- Mode "always on top" (toujours au premier plan)
- Bouton de fermeture personnalisé
- Coins arrondis avec bordure légère

## Prérequis

- Java 17 ou supérieur
- Gradle 7.0+ (ou utiliser le wrapper Gradle inclus)

## Exécution de l'application

### Via Gradle

```bash
# Sous Linux/Mac
./gradlew run

# Sous Windows
./gradlew.bat run
```

### Via JAR (après compilation)

```bash
# Construction du JAR
./gradlew build

# Exécution du JAR
java -jar build/libs/borderlessfx-1.0-SNAPSHOT.jar
```

## Exécution des tests

```bash
# Sous Linux/Mac
./gradlew test

# Sous Windows
gradlew.bat test
```

## Structure du projet

```
borderlessfx/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── borderlessfx/
│   │   │               ├── BorderlessApp.java     # Classe principale de l'application
│   │   │               └── BorderlessWindow.java  # Composant fenêtre sans bordure
│   │   └── resources/                            # Ressources (images, CSS, etc.)
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── borderlessfx/
│                       └── BorderlessWindowTest.java  # Tests unitaires
├── build.gradle                                  # Configuration Gradle
├── settings.gradle                               # Paramètres Gradle
└── README.md                                     # Ce fichier
```

## Personnalisation

Vous pouvez personnaliser l'application en modifiant les éléments suivants :

- Couleurs et styles dans la classe `BorderlessWindow`
- Dimensions de la fenêtre dans `BorderlessApp`
- Opacité par défaut dans `BorderlessApp`

## Licence

Ce projet est sous licence MIT. Voir le fichier LICENSE pour plus de détails.
