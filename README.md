# 🎓 UniSchool - Système de Gestion Scolaire

---

## 📖 Description

**UniSchool** est une application de gestion scolaire développée dans le cadre du module **Développement Java IHM** à l'École Nationale des Sciences Appliquées d'Oujda (ENSAO). Elle permet de gérer de manière centralisée et efficace les étudiants, les enseignants, les matières, les notes, les absences, les messages et les emplois du temps d'un établissement d'enseignement supérieur.

L'application offre trois interfaces distinctes adaptées aux besoins de chaque rôle :

- **👑 Administrateur** : Gestion complète des étudiants, enseignants, matières, notes, absences, emplois du temps et communication
- **👨‍🏫 Enseignant** : Consultation de ses cours, gestion des notes de ses étudiants, statistiques de ses classes et communication
- **👨‍🎓 Étudiant** : Consultation de son bulletin de notes, de ses moyennes, de ses absences et communication

---

## 📹 Vidéo de démonstration

**Lien d'accès :** https://drive.google.com/drive/folders/1OKnxs5jlokKc4JpYgklrkyAFwi-FpxOK?usp=sharing 

La vidéo de démonstration couvre l'ensemble des fonctionnalités de l'application :
- Présentation de l'interface (menus, onglets, navigation)
- Démonstration des fonctionnalités CRUD (étudiants, enseignants, notes)
- Les contrôles JavaFX utilisés (Slider, Spinner, DatePicker, ComboBox, TableView)
- Recherche, filtrage et affichage des statistiques
- Export CSV des données
- Communication entre utilisateurs

---

## 🚀 Fonctionnalités principales

- **Authentification** : Connexion sécurisée avec gestion des rôles (Admin, Enseignant, Étudiant)
- **Gestion des étudiants** : CRUD complet, filtrage par filière/année, suivi des absences
- **Gestion des enseignants** : CRUD complet, assignation des matières
- **Gestion des notes** : CRUD complet, calcul automatique des moyennes, validation des notes
- **Gestion des absences** : Enregistrement, justification et suivi des absences
- **Emploi du temps** : Gestion et consultation des plannings par filière et enseignant
- **Communication** : Messagerie interne entre administrateurs, enseignants et étudiants
- **Statistiques** : Tableau de bord, graphiques, répartition des notes et des étudiants
- **Export CSV** : Export des données (notes, emploi du temps, bulletins)

---

## 🛠️ Technologies utilisées

- **Java 21 LTS** : Langage de programmation
- **JavaFX 21.0.1** : Framework d'interface graphique
- **MySQL 8.0.33** : Base de données
- **JDBC 8.0.33** : Connexion Java-MySQL
- **Maven 3.8.x** : Gestionnaire de dépendances
- **CSS** : Personnalisation de l'interface
- **FXML** : Définition des interfaces
- **IntelliJ IDEA 2024.1** : Environnement de développement
- **Git** : Gestion de versions
- **GitHub** : Hébergement du code source

---

## 📋 Prérequis

Avant de lancer l'application, assurez-vous d'avoir installé :

**1. Java Development Kit (JDK)**
- Version requise : Java 21 LTS ou supérieur
- Téléchargement : https://www.oracle.com/java/technologies/downloads/

**2. JavaFX SDK**
- Version requise : JavaFX 21.0.1
- Téléchargement : https://gluonhq.com/products/javafx/
- Important : Télécharger la version SDK (pas JMODS)

**3. MySQL Server**
- Version requise : MySQL 8.0 ou supérieur
- Téléchargement : https://dev.mysql.com/downloads/mysql/

**4. Serveur local (WAMP / XAMPP)**
- Recommandé : XAMPP 3.3.0 ou WAMP 3.x
- Téléchargement : https://www.apachefriends.org/

**5. IDE (Recommandé)**
- IntelliJ IDEA : https://www.jetbrains.com/idea/download/
- VS Code : https://code.visualstudio.com/ (avec extensions JavaFX)

---

## 🔧 Installation

**1. Cloner le projet**

`git clone https://github.com/votre-username/unischool.git`

`cd unischool`

**2. Configurer la base de données**

- Démarrer MySQL via XAMPP/WAMP
- Ouvrir phpMyAdmin (http://localhost/phpmyadmin)
- Créer une nouvelle base de données nommée `unischool`
- Importer le script d'initialisation `init_db.sql`

**3. Configurer la connexion**

Dans le fichier `src/unischool/dao/Database.java`, vérifiez les paramètres :

`private static final String URL = "jdbc:mysql://localhost:3306/unischool";`
`private static final String USER = "root";`
`private static final String PASSWORD = "";`

**4. Ajouter le driver MySQL**

- Télécharger `mysql-connector-j-8.0.33.jar` depuis https://mvnrepository.com/artifact/mysql/mysql-connector-java
- Créer un dossier `lib/` à la racine du projet
- Placer le fichier `.jar` dans le dossier `lib/`
- Ajouter la bibliothèque dans IntelliJ (File → Project Structure → Libraries → + → Java)

**5. Configurer JavaFX dans IntelliJ**

- Télécharger JavaFX SDK depuis https://gluonhq.com/products/javafx/
- Extraire le fichier dans `C:\javafx-sdk-21.0.11`
- Dans IntelliJ, aller dans Run → Edit Configurations
- Ajouter les VM options :
`--module-path "C:\javafx-sdk-21.0.11\lib" --add-modules javafx.controls,javafx.fxml`

**6. Ouvrir le projet dans IntelliJ IDEA**

- File → Open → Sélectionner le dossier du projet
- File → Project Structure → Project → Vérifier le SDK Java 21
- Build → Rebuild Project
- Run → Run 'MainApp'

---

## 🚀 Lancement de l'application

**Depuis IntelliJ IDEA :**
- Ouvrir le fichier `MainApp.java`
- Configurer les VM options comme indiqué ci-dessus
- Cliquer sur le bouton Run (▶️)
- Ou utiliser le raccourci `Shift + F10`

---

## 🔑 Comptes de test

- Administrateur : admin@unischool.com / admin123
- Enseignant Mathématiques : prof.math@unischool.com / enseignant123
- Enseignant Informatique : prof.info@unischool.com / enseignant123
- Enseignant Physique : prof.physique@unischool.com / enseignant123
- Étudiant 1 : ali.ahmed@uni.com / etudiant123
- Étudiant 2 : sarah.ben@uni.com / etudiant123
- Étudiant 3 : karim.soussi@uni.com / etudiant123

---

## 📚 Références

- Documentation JavaFX : https://openjfx.io/
- Documentation MySQL : https://dev.mysql.com/doc/
- JavaFX Tutorial - Oracle : https://docs.oracle.com/javafx/
- JDBC Documentation - Oracle : https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/
- Scene Builder - Gluon : https://gluonhq.com/products/scene-builder/
- Maven Documentation : https://maven.apache.org/guides/
- Git Documentation : https://git-scm.com/doc

---

## 📝 Licence

Ce projet a été réalisé dans le cadre du module Développement Java IHM à l'École Nationale des Sciences Appliquées d'Oujda (ENSAO).
