-- ============================================================
-- BASE DE DONNÉES : UNISCHOOL
-- ============================================================

DROP DATABASE IF EXISTS unischool;

CREATE DATABASE IF NOT EXISTS unischool
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE unischool;

-- ============================================================
-- 1. TABLE UTILISATEUR
-- ============================================================

CREATE TABLE utilisateur (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             email VARCHAR(100) UNIQUE NOT NULL,
                             mot_de_passe VARCHAR(255) NOT NULL,
                             nom VARCHAR(50) NOT NULL,
                             prenom VARCHAR(50) NOT NULL,
                             role ENUM('ADMIN', 'ENSEIGNANT', 'ETUDIANT') NOT NULL DEFAULT 'ETUDIANT',
                             actif BOOLEAN DEFAULT TRUE,
                             derniere_connexion DATETIME,
                             date_creation DATETIME DEFAULT CURRENT_TIMESTAMP,
                             INDEX idx_email (email),
                             INDEX idx_role (role)
) ENGINE=InnoDB;

-- ============================================================
-- 2. TABLE ENSEIGNANT
-- ============================================================

CREATE TABLE enseignant (
                            id INT PRIMARY KEY AUTO_INCREMENT,
                            utilisateur_id INT UNIQUE NOT NULL,
                            specialite VARCHAR(100),
                            date_embauche DATE,
                            grade VARCHAR(50),
                            telephone VARCHAR(20),
                            bureau VARCHAR(50),
                            statut ENUM('ACTIF', 'CONGE', 'DEPART') DEFAULT 'ACTIF',
                            FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 3. TABLE ETUDIANT
-- ============================================================

CREATE TABLE etudiant (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          utilisateur_id INT UNIQUE NOT NULL,
                          date_naissance DATE,
                          telephone VARCHAR(20),
                          adresse TEXT,
                          filiere VARCHAR(100) DEFAULT 'Génie Informatique',
                          niveau VARCHAR(50) DEFAULT 'Licence',
                          annee_etude INT DEFAULT 1,
                          matricule VARCHAR(20) UNIQUE,
                          groupe VARCHAR(20),
                          date_inscription DATE DEFAULT (CURRENT_DATE),
                          statut ENUM('ACTIF', 'INACTIF', 'EXCLU', 'DIPLOME') DEFAULT 'ACTIF',
                          FOREIGN KEY (utilisateur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 4. TABLE MATIERE
-- ============================================================

CREATE TABLE matiere (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         code VARCHAR(20) UNIQUE NOT NULL,
                         nom VARCHAR(100) NOT NULL,
                         description TEXT,
                         credits INT DEFAULT 3,
                         semestre INT DEFAULT 1,
                         filiere VARCHAR(100) DEFAULT 'Génie Informatique',
                         enseignant_responsable_id INT,
                         volume_horaire INT DEFAULT 30,
                         actif BOOLEAN DEFAULT TRUE,
                         FOREIGN KEY (enseignant_responsable_id) REFERENCES enseignant(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 5. ASSOCIATION ENSEIGNANT - MATIERE
-- ============================================================

CREATE TABLE enseignant_matiere (
                                    enseignant_id INT,
                                    matiere_id INT,
                                    annee_academique VARCHAR(20) NOT NULL,
                                    PRIMARY KEY (enseignant_id, matiere_id, annee_academique),
                                    FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
                                    FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 6. TABLE NOTE (CORRIGÉE)
-- ============================================================

CREATE TABLE note (
                      id INT PRIMARY KEY AUTO_INCREMENT,
                      etudiant_id INT NOT NULL,
                      matiere_id INT NOT NULL,
                      enseignant_id INT,  -- ✅ NULLABLE pour ON DELETE SET NULL
                      valeur DECIMAL(5,2) CHECK (valeur >= 0 AND valeur <= 20),
                      date_evaluation DATE,
                      coefficient DECIMAL(3,2) DEFAULT 1.0,
                      appreciation TEXT,
                      type VARCHAR(30) DEFAULT 'Devoir',
                      semestre INT DEFAULT 1,
                      date_saisie DATETIME DEFAULT CURRENT_TIMESTAMP,
                      date_modification DATETIME,
                      validee BOOLEAN DEFAULT FALSE,
                      FOREIGN KEY (etudiant_id) REFERENCES etudiant(id) ON DELETE CASCADE,
                      FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE,
                      FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 7. TABLE ABSENCE
-- ============================================================

CREATE TABLE absence (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         etudiant_id INT NOT NULL,
                         date_absence DATE NOT NULL,
                         date_retour DATE,
                         motif VARCHAR(255),
                         justifiee BOOLEAN DEFAULT FALSE,
                         enseignant_id INT,
                         date_saisie DATETIME DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (etudiant_id) REFERENCES etudiant(id) ON DELETE CASCADE,
                         FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ============================================================
-- 8. TABLE EMPLOI DU TEMPS
-- ============================================================

CREATE TABLE emploi_du_temps (
                                 id INT PRIMARY KEY AUTO_INCREMENT,
                                 matiere_id INT NOT NULL,
                                 enseignant_id INT NOT NULL,
                                 filiere VARCHAR(100) NOT NULL,
                                 annee INT NOT NULL,
                                 jour ENUM('LUNDI', 'MARDI', 'MERCREDI', 'JEUDI', 'VENDREDI', 'SAMEDI') NOT NULL,
                                 heure_debut TIME NOT NULL,
                                 heure_fin TIME NOT NULL,
                                 salle VARCHAR(50),
                                 type_cours ENUM('CM', 'TD', 'TP') DEFAULT 'CM',
                                 semestre INT DEFAULT 1,
                                 FOREIGN KEY (matiere_id) REFERENCES matiere(id) ON DELETE CASCADE,
                                 FOREIGN KEY (enseignant_id) REFERENCES enseignant(id) ON DELETE CASCADE,
                                 UNIQUE KEY unique_edt (filiere, annee, jour, heure_debut, salle)
) ENGINE=InnoDB;

-- ============================================================
-- 9. TABLE MESSAGE
-- ============================================================

CREATE TABLE message (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         expediteur_id INT NOT NULL,
                         expediteur_role VARCHAR(20),
                         destinataire_id INT,
                         destinataire_type VARCHAR(50),
                         objet VARCHAR(200) NOT NULL,
                         contenu TEXT NOT NULL,
                         date_envoi DATETIME DEFAULT CURRENT_TIMESTAMP,
                         lu BOOLEAN DEFAULT FALSE,
                         date_lecture DATETIME,
                         filiere VARCHAR(100),
                         annee INT,
                         FOREIGN KEY (expediteur_id) REFERENCES utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- 10. INSERTION DES DONNÉES
-- ============================================================

-- 10.1 UTILISATEURS
INSERT INTO utilisateur (email, mot_de_passe, nom, prenom, role, actif) VALUES
                                                                            ('admin@unischool.com', 'admin123', 'Admin', 'Système', 'ADMIN', TRUE),
                                                                            ('prof.math@unischool.com', 'enseignant123', 'Martin', 'Sophie', 'ENSEIGNANT', TRUE),
                                                                            ('prof.info@unischool.com', 'enseignant123', 'Dubois', 'Thomas', 'ENSEIGNANT', TRUE),
                                                                            ('prof.physique@unischool.com', 'enseignant123', 'Lefèvre', 'Marie', 'ENSEIGNANT', TRUE),
                                                                            ('prof.algo@unischool.com', 'enseignant123', 'Bernard', 'Pierre', 'ENSEIGNANT', TRUE),
                                                                            ('ali.ahmed@uni.com', 'etudiant123', 'Ahmed', 'Ali', 'ETUDIANT', TRUE),
                                                                            ('sarah.ben@uni.com', 'etudiant123', 'Ben', 'Sarah', 'ETUDIANT', TRUE),
                                                                            ('karim.soussi@uni.com', 'etudiant123', 'Soussi', 'Karim', 'ETUDIANT', TRUE),
                                                                            ('leila.alami@uni.com', 'etudiant123', 'Alami', 'Leila', 'ETUDIANT', TRUE),
                                                                            ('youssef.alaoui@uni.com', 'etudiant123', 'Alaoui', 'Youssef', 'ETUDIANT', TRUE),
                                                                            ('mariam.tazi@uni.com', 'etudiant123', 'Tazi', 'Mariam', 'ETUDIANT', TRUE),
                                                                            ('hamza.oualid@uni.com', 'etudiant123', 'Oualid', 'Hamza', 'ETUDIANT', TRUE);

-- 10.2 ENSEIGNANTS
INSERT INTO enseignant (utilisateur_id, specialite, date_embauche, grade, telephone, bureau, statut) VALUES
                                                                                                         (2, 'Mathématiques Appliquées', '2020-09-01', 'Professeur', '06 11 11 11 11', 'B104', 'ACTIF'),
                                                                                                         (3, 'Informatique - Algorithmique', '2021-09-01', 'Maître de Conférences', '06 22 22 22 22', 'A201', 'ACTIF'),
                                                                                                         (4, 'Physique', '2022-09-01', 'Assistant', '06 33 33 33 33', 'C302', 'ACTIF'),
                                                                                                         (5, 'Algorithmique et Structures de Données', '2020-01-15', 'Professeur', '06 44 44 44 44', 'A105', 'ACTIF');

-- 10.3 ÉTUDIANTS
INSERT INTO etudiant (utilisateur_id, date_naissance, telephone, filiere, niveau, annee_etude, matricule, groupe, statut) VALUES
                                                                                                                              (6, '2002-01-15', '06 44 44 44 44', 'Génie Informatique', 'Licence', 2, 'E2024001', 'G1', 'ACTIF'),
                                                                                                                              (7, '2002-03-20', '06 55 55 55 55', 'Génie Informatique', 'Licence', 2, 'E2024002', 'G1', 'ACTIF'),
                                                                                                                              (8, '2001-07-10', '06 66 66 66 66', 'Génie Informatique', 'Licence', 2, 'E2024003', 'G2', 'ACTIF'),
                                                                                                                              (9, '2002-05-25', '06 77 77 77 77', 'Génie Informatique', 'Licence', 2, 'E2024004', 'G2', 'ACTIF'),
                                                                                                                              (10, '2001-11-02', '06 88 88 88 88', 'Génie Informatique', 'Licence', 2, 'E2024005', 'G1', 'ACTIF'),
                                                                                                                              (11, '2002-09-18', '06 99 99 99 99', 'Génie Informatique', 'Licence', 2, 'E2024006', 'G2', 'ACTIF'),
                                                                                                                              (12, '2002-07-12', '06 00 00 00 00', 'Génie Informatique', 'Licence', 2, 'E2024007', 'G1', 'ACTIF');

-- 10.4 MATIÈRES
INSERT INTO matiere (code, nom, description, credits, semestre, filiere, enseignant_responsable_id, volume_horaire) VALUES
('MATH101', 'Mathématiques I', 'Algèbre linéaire, matrices, systèmes d\'équations', 4, 1, 'Génie Informatique', 1, 45),
('INFO101', 'Algorithmique et Programmation', 'Introduction à la programmation en Java, structures de base', 5, 1, 'Génie Informatique', 2, 60),
('PHY101', 'Physique I', 'Mécanique classique, cinématique, dynamique', 3, 1, 'Génie Informatique', 3, 30),
('ENG101', 'Anglais Technique', 'Anglais scientifique et technique, vocabulaire informatique', 2, 1, 'Génie Informatique', NULL, 20),
('COM101', 'Communication', 'Communication professionnelle, expression orale et écrite', 2, 1, 'Génie Informatique', NULL, 20),
('ALGO101', 'Structures de Données', 'Listes, piles, files, arbres, graphes', 4, 1, 'Génie Informatique', 4, 45),
('MATH102', 'Mathématiques II', 'Analyse vectorielle, équations différentielles', 4, 2, 'Génie Informatique', 1, 45),
('INFO102', 'Programmation Orientée Objet', 'Java avancé, héritage, polymorphisme, design patterns', 5, 2, 'Génie Informatique', 2, 60),
('PHY102', 'Physique II', 'Électricité, magnétisme, circuits électriques', 3, 2, 'Génie Informatique', 3, 30),
('BD101', 'Bases de Données', 'SQL, modélisation relationnelle, conception de bases de données', 4, 2, 'Génie Informatique', 4, 45),
('RES101', 'Réseaux Informatiques', 'Modèle OSI, TCP/IP, routage, adressage', 3, 2, 'Génie Informatique', 2, 30),
('PROJ101', 'Projet de programmation', 'Projet de fin de semestre en équipe', 4, 2, 'Génie Informatique', 4, 60);

-- 10.5 ASSIGNATION ENSEIGNANTS - MATIÈRES
'
INSERT INTO enseignant_matiere (enseignant_id, matiere_id, annee_academique) VALUES
(1, 1, '2025/2026'),
(1, 7, '2025/2026'),
(2, 2, '2025/2026'),
(2, 8, '2025/2026'),
(2, 11, '2025/2026'),
(3, 3, '2025/2026'),
(3, 9, '2025/2026'),
(4, 6, '2025/2026'),
(4, 10, '2025/2026'),
(4, 12, '2025/2026');

-- 10.6 EMPLOI DU TEMPS
INSERT INTO emploi_du_temps (matiere_id, enseignant_id, filiere, annee, jour, heure_debut, heure_fin, salle, type_cours, semestre) VALUES
(1, 1, 'Génie Informatique', 2, 'LUNDI', '08:00', '09:30', 'B101', 'CM', 1),
(2, 2, 'Génie Informatique', 2, 'LUNDI', '09:45', '11:15', 'A201', 'CM', 1),
(6, 4, 'Génie Informatique', 2, 'LUNDI', '11:30', '13:00', 'A105', 'CM', 1),
(3, 3, 'Génie Informatique', 2, 'MARDI', '08:00', '09:30', 'C302', 'CM', 1),
(2, 2, 'Génie Informatique', 2, 'MARDI', '09:45', '11:15', 'A201', 'TD', 1),
(1, 1, 'Génie Informatique', 2, 'MARDI', '11:30', '13:00', 'B101', 'TD', 1),
(6, 4, 'Génie Informatique', 2, 'MERCREDI', '08:00', '09:30', 'A105', 'TP', 1),
(4, 2, 'Génie Informatique', 2, 'MERCREDI', '09:45', '11:15', 'C101', 'CM', 1),
(5, 1, 'Génie Informatique', 2, 'MERCREDI', '11:30', '13:00', 'B102', 'CM', 1),
(3, 3, 'Génie Informatique', 2, 'JEUDI', '08:00', '09:30', 'C302', 'TD', 1),
(1, 1, 'Génie Informatique', 2, 'JEUDI', '09:45', '11:15', 'B101', 'TD', 1),
(2, 2, 'Génie Informatique', 2, 'JEUDI', '11:30', '13:00', 'A201', 'TP', 1),
(4, 2, 'Génie Informatique', 2, 'VENDREDI', '08:00', '09:30', 'C101', 'CM', 1),
(6, 4, 'Génie Informatique', 2, 'VENDREDI', '09:45', '11:15', 'A105', 'CM', 1),
(3, 3, 'Génie Informatique', 2, 'VENDREDI', '11:30', '13:00', 'C302', 'CM', 1);

-- 10.7 NOTES
INSERT INTO note (etudiant_id, matiere_id, enseignant_id, valeur, date_evaluation, coefficient, appreciation, type, semestre) VALUES
(1, 1, 1, 16.0, '2025-12-15', 3.0, 'Excellent travail, bonne maîtrise des concepts', 'Examen', 1),
(1, 1, 1, 14.5, '2025-11-10', 2.0, 'Bon travail, continuez', 'Devoir', 1),
(2, 1, 1, 14.0, '2025-12-15', 3.0, 'Très bon travail', 'Examen', 1),
(3, 1, 1, 10.0, '2025-12-15', 3.0, 'Passable, peut mieux faire', 'Examen', 1),
(4, 1, 1, 17.0, '2025-12-15', 3.0, 'Excellent, félicitations', 'Examen', 1),
(1, 2, 2, 15.0, '2025-12-18', 4.0, 'Bon code, bonne logique', 'Projet', 1),
(2, 2, 2, 16.0, '2025-12-18', 4.0, 'Excellent projet', 'Projet', 1),
(3, 2, 2, 12.0, '2025-12-18', 4.0, 'Projet fonctionnel, peut être optimisé', 'Projet', 1),
(5, 2, 2, 14.0, '2025-12-18', 4.0, 'Bon travail', 'Projet', 1),
(1, 3, 3, 12.0, '2025-12-20', 3.0, 'Compréhension moyenne', 'Examen', 1),
(2, 3, 3, 11.0, '2025-12-20', 3.0, 'Peut mieux faire', 'Examen', 1),
(4, 3, 3, 14.0, '2025-12-20', 3.0, 'Bonne compréhension', 'Examen', 1),
(1, 6, 4, 17.0, '2025-12-22', 4.0, 'Excellent, très bonne maîtrise', 'Examen', 1),
(2, 6, 4, 15.0, '2025-12-22', 4.0, 'Très bon travail', 'Examen', 1),
(3, 6, 4, 11.0, '2025-12-22', 4.0, 'Passable', 'Examen', 1),
(1, 7, 1, 17.0, '2026-05-15', 3.0, 'Excellent, progression remarquable', 'Examen', 2),
(2, 7, 1, 15.0, '2026-05-15', 3.0, 'Très bon travail', 'Examen', 2),
(1, 8, 2, 16.5, '2026-05-20', 4.0, 'Très bon projet', 'Projet', 2),
(2, 8, 2, 17.0, '2026-05-20', 4.0, 'Excellent code', 'Projet', 2),
(4, 8, 2, 18.0, '2026-05-20', 4.0, 'Exceptionnel', 'Projet', 2),
(1, 9, 3, 13.0, '2026-05-25', 3.0, 'Bon travail', 'Examen', 2),
(2, 9, 3, 12.0, '2026-05-25', 3.0, 'Résultats corrects', 'Examen', 2);

-- 10.8 ABSENCES
INSERT INTO absence (etudiant_id, date_absence, date_retour, motif, justifiee, enseignant_id) VALUES
(1, '2026-06-15', '2026-06-16', 'Maladie', TRUE, 2),
(1, '2026-06-10', '2026-06-10', 'Rendez-vous médical', TRUE, 1),
(1, '2026-06-05', '2026-06-05', 'Retard', FALSE, 2),
(2, '2026-06-14', '2026-06-15', 'Absence non justifiée', FALSE, 3),
(2, '2026-06-08', '2026-06-08', 'Maladie', TRUE, 1),
(3, '2026-06-12', '2026-06-13', 'Maladie', TRUE, 4),
(4, '2026-06-15', '2026-06-16', 'Raison personnelle', FALSE, 2),
(4, '2026-06-07', '2026-06-07', 'Retard', TRUE, 3),
(5, '2026-06-13', '2026-06-14', 'Retard', TRUE, 1),
(5, '2026-06-03', '2026-06-04', 'Maladie', TRUE, 4),
(6, '2026-06-11', '2026-06-12', 'Absence non justifiée', FALSE, 2),
(7, '2026-06-18', '2026-06-19', 'Retard', TRUE, 3);

-- ============================================================
-- 11. VUES
-- ============================================================

CREATE OR REPLACE VIEW vue_bulletin AS
SELECT
    e.id AS etudiant_id,
    u.nom,
    u.prenom,
    e.matricule,
    e.filiere,
    e.niveau,
    e.annee_etude,
    AVG(n.valeur * n.coefficient) / AVG(n.coefficient) AS moyenne_generale,
    COUNT(n.id) AS nombre_notes,
    MAX(n.date_evaluation) AS derniere_evaluation,
    e.statut
FROM etudiant e
JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN note n ON n.etudiant_id = e.id AND n.validee = TRUE
GROUP BY e.id;

CREATE OR REPLACE VIEW vue_moyenne_matiere AS
SELECT
    m.id AS matiere_id,
    m.code,
    m.nom AS matiere_nom,
    AVG(n.valeur) AS moyenne,
    COUNT(n.id) AS nombre_notes
FROM matiere m
LEFT JOIN note n ON n.matiere_id = m.id AND n.validee = TRUE
GROUP BY m.id;

CREATE OR REPLACE VIEW vue_statistiques_absences AS
SELECT
    e.id AS etudiant_id,
    u.nom,
    u.prenom,
    COUNT(a.id) AS total_absences,
    SUM(CASE WHEN a.justifiee = TRUE THEN 1 ELSE 0 END) AS absences_justifiees,
    SUM(CASE WHEN a.justifiee = FALSE THEN 1 ELSE 0 END) AS absences_non_justifiees
FROM etudiant e
JOIN utilisateur u ON e.utilisateur_id = u.id
LEFT JOIN absence a ON a.etudiant_id = e.id
GROUP BY e.id;

-- ============================================================
-- FIN DU SCRIPT
-- ============================================================

SELECT '✅ Base de données UNISCHOOL V2 créée avec succès !' AS Message;