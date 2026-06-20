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

        // Configurer les colonnes
        setupNotesTableColumns();

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
}