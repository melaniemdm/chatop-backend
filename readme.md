# Chatop API

Ce projet est une application Java développée avec Spring Boot pour gérer les locations, les utilisateurs et les
messages. Elle utilise une architecture RESTful avec une documentation Swagger intégrée pour faciliter l'exploration des
endpoints.

## Prérequis

- Java 21 ou supérieur
- Maven pour la gestion des dépendances
- MySQL pour la base de données
- Spring Boot 3.x

## Installation

Clonez le projet, puis installez les dépendances et configurez la base de données en suivant les étapes ci-dessous.

### Installation de MySQL

1. Téléchargez et installez MySQL depuis
   le [ https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/).
2. Suivez les instructions d'installation et configurez un mot de passe pour l'utilisateur `root`.
3. Une fois l'installation terminée, démarrez le serveur MySQL.

### Configuration de la base de données

1. Connectez-vous à MySQL en utilisant le terminal ou un client comme MySQL Workbench :
   ```bash
   mysql -u root -p
   ```
2. Créez une base de données pour l'application :
   ```sql
   CREATE DATABASE chatopdb;
   ```
3. Importez le script SQL de structure et de données initiales depuis le fichier `script.sql` dans le dossier
   `src/main/resources/sql/` :
   ```bash
   mysql -u root -p chatopdb < src/main/resources/sql/script.sql
   ```
4. Dans le fichier `application.properties` ou `application.yml`, configurez les informations de connexion MySQL :

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/chatopdb
    spring.datasource.username=YOUR_USERNAME
    spring.datasource.password=YOUR_PASSWORD
    spring.jpa.hibernate.ddl-auto=update
    jwt.secret=YOUR_SECRET_KEY
    ```

5. Assurez-vous que le serveur MySQL est en cours d'exécution.

### Installer les dépendances et compiler le projet

Pour installer les dépendances et compiler le projet, exécutez :

```bash
mvn clean install
```

## Démarrer le Serveur de Développement

Pour lancer l'application en mode développement, exécutez :

```bash
mvn spring-boot:run
```

L'API sera accessible à l'adresse [http://localhost:3001/](http://localhost:3001/).

## Documentation de l'API (Swagger)

Une documentation Swagger est intégrée et disponible à l'adresse suivante une fois le serveur
démarré : [http://localhost:3001/api/swagger-ui/index.html#/](http://localhost:3001/swagger-ui/index.html).

## Architecture du Projet

L'application est organisée en modules pour faciliter la maintenance et le développement :

- **controllers** : Contient les contrôleurs REST qui définissent les endpoints pour les fonctionnalités principales.
- **services** : Contient la logique métier pour chaque entité (User, Rental, Message).
- **dto** : Contient les objets de transfert de données (DTO) utilisés pour structurer les données échangées via les
  endpoints.
- **repositories** : Contient les interfaces d'accès aux données, qui interagissent avec la base de données MySQL.
- **config** : Contient la configuration de sécurité, y compris JWT pour l'authentification.

## Fonctionnalités Implémentées

- **Gestion des utilisateurs** : Inscription, connexion et récupération des informations utilisateur.
- **Gestion des locations** : Création, mise à jour et suppression des propriétés de location.
- **Gestion des messages** : Envoi de messages pour les locations par les utilisateurs.
- **Sécurité** : Authentification via JWT pour sécuriser les endpoints.

## Fichiers Clés

- **AuthController.java** : Gère l'authentification et l'inscription des utilisateurs.
- **RentalController.java** : Gère les fonctionnalités liées aux locations.
- **MessageController.java** : Permet aux utilisateurs d'envoyer des messages liés aux locations.
- **JwtService.java** : Fournit les services de génération et de validation des tokens JWT.
- **SwaggerConfig.java** : Configuration de Swagger pour la documentation de l'API.


