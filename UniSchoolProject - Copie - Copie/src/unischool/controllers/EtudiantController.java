package unischool.controllers;

import unischool.dao.*;
import unischool.models.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.util.HashSet;
import java.util.Set;

import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class EtudiantController implements Initializable {

    // ============================================================
    // SESSION UTILISATEUR
    // ============================================================
    private Utilisateur utilisateurConnecte;
    private int etudiantId;

    // ============================================================
    // DAO
    // ============================================================
    private EtudiantDAO etudiantDAO;
    private NoteDAO noteDAO;
    private AbsenceDAO absenceDAO;

    // ============================================================
    // COMPOSANTS FXML
    // ============================================================
    @FXML private Label userLabel;
    @FXML private Label statusLabel;

    // Informations étudiant
    @FXML private Label etudiantNomLabel;
    @FXML private Label etudiantFiliereLabel;
    @FXML private Label etudiantAnneeLabel;
    @FXML private Label etudiantMatriculeLabel;
    @FXML private Label moyenneGeneraleLabel;
    @FXML private Label mentionLabel;
    @FXML private Label totalAbsencesLabel;
    @FXML private Label totalNotesLabel;

    // Tableau des notes
    @FXML private TableView<Note> notesTable;
    @FXML private TableColumn<Note, Integer> noteIdCol;
    @FXML private TableColumn<Note, String> noteMatiereCol;
    @FXML private TableColumn<Note, Double> noteValeurCol;
    @FXML private TableColumn<Note, Double> noteCoeffCol;
    @FXML private TableColumn<Note, Double> notePointsCol;
    @FXML private TableColumn<Note, String> noteTypeCol;
    @FXML private TableColumn<Note, String> noteDateCol;
    @FXML private TableColumn<Note, String> noteAppreciationCol;
    @FXML private TableColumn<Note, Boolean> noteValideeCol;

    // Liste des matières
    @FXML private ListView<String> matieresListView;

    // ============================================================
    // OBSERVABLE LISTS
    // ============================================================
    private ObservableList<Note> notesData = FXCollections.observableArrayList();

    // ============================================================
// COMPOSANTS FXML - ONGLET COMMUNICATION
// ============================================================
    @FXML private ComboBox<String> etudiantDestinataireCombo;
    @FXML private TextField etudiantMessageObjetField;
    @FXML private TextArea etudiantMessageContentArea;
    @FXML private Label etudiantDestinataireAffiche;
    @FXML private Label etudiantMessagesEnvoyesLabel;
    @FXML private TableView<Message> etudiantMessagesTable;
    @FXML private TableColumn<Message, String> etudiantMsgDateCol;
    @FXML private TableColumn<Message, String> etudiantMsgExpediteurCol;
    @FXML private TableColumn<Message, String> etudiantMsgDestinataireCol;
    @FXML private TableColumn<Message, String> etudiantMsgObjetCol;
    @FXML private TableColumn<Message, String> etudiantMsgContenuCol;

    // ============================================================
// DAO
// ============================================================
    private MessageDAO messageDAO;
    private EnseignantDAO enseignantDAO;

    // ============================================================
    // SETTER POUR L'UTILISATEUR
    // ============================================================
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        if (userLabel != null) {
            userLabel.setText("👨‍🎓 " + utilisateur.getNomComplet());
        }

        chargerEtudiantId();
        chargerDonneesEtudiant();
        chargerNotes();
        chargerAbsences();
        chargerMessagesEtudiant();

        System.out.println("✅ Étudiant connecté : " + utilisateur.getNomComplet());
    }

    // ============================================================
    // INITIALISATION
    // ============================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les DAO
        etudiantDAO = new EtudiantDAO();
        noteDAO = new NoteDAO();
        absenceDAO = new AbsenceDAO();
        messageDAO = new MessageDAO();
        enseignantDAO = new EnseignantDAO();

        // Configurer les colonnes
        setupNotesTableColumns();
        setupEtudiantMessagesTableColumns();

        setupEtudiantCommunication();

        System.out.println("✅ Interface Étudiant initialisée");
    }

    // ============================================================
    // CHARGEMENT DE L'ID ÉTUDIANT
    // ============================================================
    private void chargerEtudiantId() {
        try {
            Etudiant etudiant = etudiantDAO.readByUtilisateurId(utilisateurConnecte.getId());
            if (etudiant != null) {
                this.etudiantId = etudiant.getId();
                System.out.println("✅ Étudiant ID : " + etudiantId);
            } else {
                System.err.println("❌ Étudiant non trouvé pour l'utilisateur : " + utilisateurConnecte.getId());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement étudiant : " + e.getMessage());
        }
    }

    // ============================================================
    // CONFIGURATION DES COLONNES
    // ============================================================
    private void setupNotesTableColumns() {
        noteIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        noteMatiereCol.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        noteValeurCol.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        noteCoeffCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        notePointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        noteTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        noteDateCol.setCellValueFactory(cellData -> {
            var date = cellData.getValue().getDateEvaluation();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        noteAppreciationCol.setCellValueFactory(new PropertyValueFactory<>("appreciation"));
        noteValideeCol.setCellValueFactory(new PropertyValueFactory<>("validee"));

        noteValideeCol.setCellFactory(column -> new TableCell<Note, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✅" : "⏳");
                }
            }
        });

        notesTable.setItems(notesData);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES
    // ============================================================
    private void chargerDonneesEtudiant() {
        try {
            Etudiant etudiant = etudiantDAO.read(etudiantId);
            if (etudiant != null) {
                etudiantNomLabel.setText(etudiant.getNomComplet());
                etudiantFiliereLabel.setText(etudiant.getFiliere());
                etudiantAnneeLabel.setText(String.valueOf(etudiant.getAnneeEtude()));
                etudiantMatriculeLabel.setText(etudiant.getMatricule());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement infos étudiant : " + e.getMessage());
        }
    }

    private void chargerNotes() {
        try {
            List<Note> notes = noteDAO.getNotesByEtudiant(etudiantId);
            notesData.clear();
            notesData.addAll(notes);
            notesTable.setItems(notesData);

            // Moyenne
            double moyenne = noteDAO.getMoyenneByEtudiant(etudiantId);
            moyenneGeneraleLabel.setText(String.format("%.2f/20", moyenne));

            // Mention
            mentionLabel.setText(obtenirMention(moyenne));

            // Nombre de notes
            totalNotesLabel.setText(String.valueOf(notes.size()));

            // Liste des matières
            ObservableList<String> matieres = FXCollections.observableArrayList();
            for (Note n : notes) {
                if (!matieres.contains(n.getNomMatiere())) {
                    matieres.add(n.getNomMatiere());
                }
            }
            matieresListView.setItems(matieres);

        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement notes : " + e.getMessage());
        }
    }

    private void chargerAbsences() {
        try {
            int absences = absenceDAO.countByEtudiant(etudiantId);
            totalAbsencesLabel.setText(String.valueOf(absences) + " absence(s)");
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement absences : " + e.getMessage());
        }
    }

    private String obtenirMention(double moyenne) {
        if (moyenne >= 16) return "🏆 Très Bien";
        else if (moyenne >= 14) return "⭐ Bien";
        else if (moyenne >= 12) return "✅ Assez Bien";
        else if (moyenne >= 10) return "📖 Passable";
        else return "❌ Insuffisant";
    }

    // ============================================================
    // FONCTIONS GLOBALES
    // ============================================================
    @FXML
    private void handleRefresh() {
        chargerNotes();
        chargerAbsences();
        chargerDonneesEtudiant();
        statusLabel.setText("🔄 Données rafraîchies");
    }

    @FXML
    private void handleLogout() {
        try {
            Stage stage = (Stage) userLabel.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("UniSchool - Connexion");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afficherAlerte(String titre, String contenu) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(contenu);
        alert.showAndWait();
    }

    // ============================================================
// CONFIGURATION COMMUNICATION
// ============================================================

    private void setupEtudiantMessagesTableColumns() {
        if (etudiantMsgDateCol == null) return;
        etudiantMsgDateCol.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        etudiantMsgExpediteurCol.setCellValueFactory(new PropertyValueFactory<>("nomExpediteur"));
        etudiantMsgDestinataireCol.setCellValueFactory(new PropertyValueFactory<>("destinataireType"));
        etudiantMsgObjetCol.setCellValueFactory(new PropertyValueFactory<>("objet"));
        etudiantMsgContenuCol.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        etudiantMessagesTable.setItems(messagesData);
    }

// ============================================================
// CHARGEMENT DES MESSAGES
// ============================================================

    private ObservableList<Message> messagesData = FXCollections.observableArrayList();

    private void chargerMessagesEtudiant() {
        try {
            List<Message> messages = messageDAO.getAllMessages();
            messagesData.clear();

            Etudiant etudiant = etudiantDAO.read(etudiantId);
            String nomEtudiant = etudiant.getNomComplet();
            String filiereEtudiant = etudiant.getFiliere();

            System.out.println("📧 Chargement des messages pour : " + nomEtudiant);
            System.out.println("📧 Filière : " + filiereEtudiant);

            for (Message m : messages) {
                String destType = m.getDestinataireType();
                boolean estPourMoi = false;

                // 1. Messages envoyés par l'étudiant
                if (m.getExpediteurId() == utilisateurConnecte.getId()) {
                    estPourMoi = true;
                }
                // 2. Messages envoyés à tous les étudiants
                else if ("Tous les étudiants".equals(destType) || "Tous les utilisateurs".equals(destType)) {
                    estPourMoi = true;
                }
                // 3. Messages envoyés spécifiquement à un étudiant
                else if ("Étudiant spécifique".equals(destType)) {
                    // Vérifier si le destinataire contient le nom de l'étudiant
                    String destinataire = m.getDestinataireType();
                    if (destinataire != null && destinataire.contains(nomEtudiant)) {
                        estPourMoi = true;
                    }
                }
                // 4. Messages envoyés par filière
                else if ("Par filière".equals(destType) && m.getFiliere() != null &&
                        m.getFiliere().equals(filiereEtudiant)) {
                    estPourMoi = true;
                }
                // 5. Messages envoyés par année
                else if ("Par année".equals(destType) && m.getAnnee() != null &&
                        m.getAnnee() == etudiant.getAnneeEtude()) {
                    estPourMoi = true;
                }

                if (estPourMoi) {
                    messagesData.add(m);
                }
            }

            // Trier par date décroissante
            messagesData.sort((m1, m2) -> m2.getDateEnvoi().compareTo(m1.getDateEnvoi()));

            etudiantMessagesTable.setItems(messagesData);
            etudiantMessagesEnvoyesLabel.setText(String.valueOf(messagesData.size()));

            System.out.println("📧 " + messagesData.size() + " messages affichés pour " + nomEtudiant);

        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement messages : " + e.getMessage());
            e.printStackTrace();
        }
    }

// ============================================================
// ENVOI DE MESSAGES
// ============================================================

    @FXML
    private void handleEtudiantEnvoyerMessage() {
        String destinataire = etudiantDestinataireCombo.getValue();
        String objet = etudiantMessageObjetField.getText();
        String contenu = etudiantMessageContentArea.getText();

        if (destinataire == null || objet.isEmpty() || contenu.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Nettoyer le nom du destinataire (enlever les icônes)
        String destinataireClean = destinataire
                .replace("👑 ", "")
                .replace("👨‍🏫 ", "")
                .replace(" (", " - ")
                .replace(")", "");

        try {
            Message message = new Message(
                    utilisateurConnecte.getId(),
                    "ETUDIANT",
                    destinataireClean,
                    objet,
                    contenu
            );

            messageDAO.create(message);
            messagesData.add(0, message);
            etudiantMessagesEnvoyesLabel.setText(String.valueOf(messagesData.size()));

            afficherAlerte("Succès", "✅ Message envoyé avec succès !\n\n" +
                    "📋 Destinataire : " + destinataireClean + "\n" +
                    "📝 Objet : " + objet);

            etudiantMessageObjetField.clear();
            etudiantMessageContentArea.clear();
            etudiantDestinataireAffiche.setText("📋 Destinataire : " + destinataireClean);
            statusLabel.setText("✉️ Message envoyé à " + destinataireClean);

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible d'envoyer : " + e.getMessage());
        }
    }

    @FXML
    private void handleEtudiantEffacerMessage() {
        etudiantMessageObjetField.clear();
        etudiantMessageContentArea.clear();
        etudiantDestinataireAffiche.setText("Aucun destinataire sélectionné");
        statusLabel.setText("✉️ Message effacé");
    }

    private void setupEtudiantCommunication() {
        if (etudiantDestinataireCombo == null) return;

        ObservableList<String> destinataires = FXCollections.observableArrayList();
        destinataires.add("👑 Administrateur");

        // Charger les enseignants depuis la base de données
        try {
            // Récupérer tous les enseignants
            List<Enseignant> enseignants = enseignantDAO.readAll();
            for (Enseignant e : enseignants) {
                // Ne pas inclure l'enseignant connecté (si c'est le cas)
                String nomEnseignant = "👨‍🏫 " + e.getNomComplet() + " (" + e.getSpecialite() + ")";
                if (!destinataires.contains(nomEnseignant)) {
                    destinataires.add(nomEnseignant);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement enseignants : " + e.getMessage());
        }

        // Ajouter l'option "Tous les enseignants" s'il y a des enseignants
        if (destinataires.size() > 1) {
            destinataires.add("👨‍🏫 Tous les enseignants");
        }

        etudiantDestinataireCombo.setItems(destinataires);
        etudiantDestinataireCombo.setValue("👑 Administrateur");

        // Écouteur pour afficher le destinataire sélectionné
        etudiantDestinataireCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && etudiantDestinataireAffiche != null) {
                etudiantDestinataireAffiche.setText("📋 Destinataire : " + newVal);
            }
        });
    }
}