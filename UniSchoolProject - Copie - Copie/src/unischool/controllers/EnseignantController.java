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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EnseignantController implements Initializable {

    // ============================================================
    // SESSION UTILISATEUR
    // ============================================================
    private Utilisateur utilisateurConnecte;
    private int enseignantId;
    private String enseignantNom;

    // ============================================================
    // DAO
    // ============================================================
    private EtudiantDAO etudiantDAO;
    private EnseignantDAO enseignantDAO;
    private NoteDAO noteDAO;
    private MatiereDAO matiereDAO;
    private UtilisateurDAO utilisateurDAO;
    private AbsenceDAO absenceDAO;
    private MessageDAO messageDAO;
    private EmploiDuTempsDAO emploiDuTempsDAO;

    // ============================================================
    // COMPOSANTS FXML - ONGLET EMPLOI DU TEMPS
    // ============================================================
    @FXML private ComboBox<String> ensEdtSemestreCombo;
    @FXML private Label ensTotalCoursLabel;
    @FXML private Label ensEdtStatusLabel;
    @FXML private TableView<EmploiDuTemps> ensEmploiDuTempsTable;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtJourCol;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtHoraireCol;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtMatiereCol;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtFiliereCol;
    @FXML private TableColumn<EmploiDuTemps, Integer> ensEdtAnneeCol;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtSalleCol;
    @FXML private TableColumn<EmploiDuTemps, String> ensEdtTypeCol;

    // ============================================================
    // OBSERVABLE LIST - EMPLOI DU TEMPS
    // ============================================================
    private ObservableList<EmploiDuTemps> ensEmploiDuTempsData = FXCollections.observableArrayList();

    // ============================================================
    // COMPOSANTS FXML - ONGLET MES COURS
    // ============================================================
    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private Label coursDetailsLabel;
    @FXML private ListView<String> matieresEnseignees;
    @FXML private TableView<Etudiant> etudiantsCoursTable;
    @FXML private TableColumn<Etudiant, Integer> ecIdCol;
    @FXML private TableColumn<Etudiant, String> ecNomCol;
    @FXML private TableColumn<Etudiant, String> ecPrenomCol;
    @FXML private TableColumn<Etudiant, String> ecEmailCol;
    @FXML private TableColumn<Etudiant, Double> ecNoteCol;

    // ============================================================
    // COMPOSANTS FXML - ONGLET GESTION DES NOTES
    // ============================================================
    @FXML private ComboBox<String> matiereNoteCombo;
    @FXML private ComboBox<String> etudiantNoteCombo;
    @FXML private Label notesStatusLabel;
    @FXML private TableView<Note> notesEnseignantTable;
    @FXML private TableColumn<Note, Integer> neIdCol;
    @FXML private TableColumn<Note, String> neEtudiantCol;
    @FXML private TableColumn<Note, String> neMatiereCol;
    @FXML private TableColumn<Note, Double> neValeurCol;
    @FXML private TableColumn<Note, Double> neCoeffCol;
    @FXML private TableColumn<Note, Double> nePointsCol;
    @FXML private TableColumn<Note, String> neTypeCol;
    @FXML private TableColumn<Note, String> neDateCol;
    @FXML private TableColumn<Note, String> neAppreciationCol;
    @FXML private TableColumn<Note, Boolean> neValideeCol;

    // ============================================================
    // COMPOSANTS FXML - ONGLET STATISTIQUES
    // ============================================================
    @FXML private ComboBox<String> statMatiereCombo;
    @FXML private Label ensMoyenneClasse;
    @FXML private Label ensTauxReussite;
    @FXML private Label ensNbEtudiants;
    @FXML private Label ensNbNotes;
    @FXML private VBox ensGraphiqueContainer;
    @FXML private ListView<String> ensStatListeEtudiants;

    // ============================================================
// COMPOSANTS FXML - ONGLET COMMUNICATION
// ============================================================
    @FXML private ComboBox<String> ensDestinataireTypeCombo;
    @FXML private ComboBox<String> ensDestinataireNomCombo;
    @FXML private ComboBox<String> ensDestinataireFiliereCombo;
    @FXML private Label ensDestinataireAffiche;
    @FXML private Label ensMessagesEnvoyesLabel;
    @FXML private TextField ensMessageObjetField;
    @FXML private TextArea ensMessageContentArea;
    @FXML private TableView<Message> ensMessagesTable;
    @FXML private TableColumn<Message, String> ensMsgDateCol;
    @FXML private TableColumn<Message, String> ensMsgExpediteurCol;
    @FXML private TableColumn<Message, String> ensMsgDestinataireCol;
    @FXML private TableColumn<Message, String> ensMsgObjetCol;
    @FXML private TableColumn<Message, String> ensMsgContenuCol;

    // ============================================================
    // OBSERVABLE LISTS
    // ============================================================
    private ObservableList<String> matieresData = FXCollections.observableArrayList();
    private ObservableList<Note> notesData = FXCollections.observableArrayList();
    private ObservableList<Message> messagesData = FXCollections.observableArrayList();

    // ============================================================
    // SETTER POUR L'UTILISATEUR
    // ============================================================
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        this.enseignantNom = utilisateur.getNomComplet();

        if (userLabel != null) {
            userLabel.setText("👤 " + enseignantNom);
        }

        chargerEnseignantId();
        chargerMatieresEnseignees();
        chargerNotesEnseignant();
        chargerMessages();
        chargerComboboxFiltres();
        rechargerComboboxNotes();

        System.out.println("✅ Enseignant connecté : " + enseignantNom);
    }

    // ============================================================
    // INITIALISATION
    // ============================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les DAO
        etudiantDAO = new EtudiantDAO();
        enseignantDAO = new EnseignantDAO();
        noteDAO = new NoteDAO();
        matiereDAO = new MatiereDAO();
        utilisateurDAO = new UtilisateurDAO();
        absenceDAO = new AbsenceDAO();
        messageDAO = new MessageDAO();
        emploiDuTempsDAO = new EmploiDuTempsDAO();

        // Configurer les colonnes
        setupEtudiantsCoursTableColumns();
        setupNotesEnseignantTableColumns();
        setupMessagesTableColumns();
        setupEnsEmploiDuTempsTableColumns();

        // Charger les données des ComboBox
        chargerComboboxFiltres();

        // Écouteur sélection matière
        if (matieresEnseignees != null) {
            matieresEnseignees.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            chargerEtudiantsParMatiere(newVal);
                            coursDetailsLabel.setText("📚 Matière sélectionnée : " + newVal);
                        }
                    }
            );
        }

        System.out.println("✅ Interface Enseignant initialisée");
    }

    // ============================================================
    // CHARGEMENT DE L'ID ENSEIGNANT
    // ============================================================
    private void chargerEnseignantId() {
        try {
            Enseignant enseignant = enseignantDAO.readByUtilisateurId(utilisateurConnecte.getId());
            if (enseignant != null) {
                this.enseignantId = enseignant.getId();
                System.out.println("✅ Enseignant ID : " + enseignantId);
            } else {
                System.err.println("❌ Enseignant non trouvé pour l'utilisateur : " + utilisateurConnecte.getId());
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement enseignant : " + e.getMessage());
        }
    }

    // ============================================================
    // CONFIGURATION DES COLONNES - ETUDIANTS COURS
    // ============================================================
    private void setupEtudiantsCoursTableColumns() {
        if (ecIdCol == null) return;
        ecIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        ecNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        ecPrenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        ecEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        ecNoteCol.setCellValueFactory(cellData -> {
            Etudiant e = cellData.getValue();
            try {
                double note = noteDAO.getMoyenneByEtudiant(e.getId());
                return new javafx.beans.property.SimpleDoubleProperty(note).asObject();
            } catch (SQLException ex) {
                return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
            }
        });

        etudiantsCoursTable.setItems(FXCollections.observableArrayList());
    }

    // ============================================================
    // CONFIGURATION DES COLONNES - NOTES ENSEIGNANT
    // ============================================================
    private void setupNotesEnseignantTableColumns() {
        if (neIdCol == null) return;
        neIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        neEtudiantCol.setCellValueFactory(new PropertyValueFactory<>("nomEtudiantComplet"));
        neMatiereCol.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        neValeurCol.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        neCoeffCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        nePointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        neTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        neDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateEvaluation();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        neAppreciationCol.setCellValueFactory(new PropertyValueFactory<>("appreciation"));
        neValideeCol.setCellValueFactory(new PropertyValueFactory<>("validee"));

        neValideeCol.setCellFactory(column -> new TableCell<Note, Boolean>() {
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

        notesEnseignantTable.setItems(notesData);
    }

    // ============================================================
    // CONFIGURATION DES COLONNES - MESSAGES
    // ============================================================
    private void setupMessagesTableColumns() {
        if (ensMsgDateCol == null) return;
        ensMsgDateCol.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        ensMsgExpediteurCol.setCellValueFactory(new PropertyValueFactory<>("nomExpediteur"));
        ensMsgDestinataireCol.setCellValueFactory(new PropertyValueFactory<>("destinataireType"));
        ensMsgObjetCol.setCellValueFactory(new PropertyValueFactory<>("objet"));
        ensMsgContenuCol.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        ensMessagesTable.setItems(messagesData);
    }

    // ============================================================
    // CONFIGURATION TABLEAU EMPLOI DU TEMPS
    // ============================================================
    private void setupEnsEmploiDuTempsTableColumns() {
        if (ensEdtJourCol == null) return;
        ensEdtJourCol.setCellValueFactory(new PropertyValueFactory<>("jour"));
        ensEdtHoraireCol.setCellValueFactory(new PropertyValueFactory<>("heureFormatee"));
        ensEdtMatiereCol.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        ensEdtFiliereCol.setCellValueFactory(new PropertyValueFactory<>("filiere"));
        ensEdtAnneeCol.setCellValueFactory(new PropertyValueFactory<>("annee"));
        ensEdtSalleCol.setCellValueFactory(new PropertyValueFactory<>("salle"));
        ensEdtTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeCours"));

        ensEmploiDuTempsTable.setItems(ensEmploiDuTempsData);
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES
    // ============================================================
    private void chargerMatieresEnseignees() {
        if (enseignantId == 0) return;

        try {
            List<Matiere> matieres = enseignantDAO.getMatieresByEnseignant(enseignantId);
            matieresData.clear();
            for (Matiere m : matieres) {
                matieresData.add(m.getNomComplet());
            }
            if (matieresEnseignees != null) {
                matieresEnseignees.setItems(matieresData);
            }

            if (!matieresData.isEmpty() && matieresEnseignees != null) {
                matieresEnseignees.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement matières : " + e.getMessage());
        }
    }

    private void chargerEtudiantsParMatiere(String matiereNom) {
        try {
            List<Matiere> matieres = matiereDAO.readAll();
            int matiereId = 0;
            for (Matiere m : matieres) {
                if (m.getNomComplet().equals(matiereNom)) {
                    matiereId = m.getId();
                    break;
                }
            }

            if (matiereId > 0 && etudiantsCoursTable != null) {
                List<Note> notes = noteDAO.getNotesByMatiere(matiereId);
                List<Etudiant> etudiants = new ArrayList<>();
                for (Note n : notes) {
                    Etudiant e = etudiantDAO.read(n.getEtudiantId());
                    if (e != null && !etudiants.contains(e)) {
                        etudiants.add(e);
                    }
                }
                etudiantsCoursTable.setItems(FXCollections.observableArrayList(etudiants));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement étudiants : " + e.getMessage());
        }
    }


    private void chargerMessages() {
        try {
            List<Message> messages = messageDAO.getAllMessages();
            messagesData.clear();

            // Filtrer les messages pour cet enseignant
            for (Message m : messages) {
                String destType = m.getDestinataireType();
                if (destType != null) {
                    // Messages reçus par l'enseignant
                    if (destType.equals("Tous les enseignants") ||
                            destType.equals("Tous les utilisateurs") ||
                            (destType.equals("Par filière") && m.getFiliere() != null) ||
                            destType.equals("Enseignant spécifique") ||
                            destType.equals("Administrateur")) {
                        messagesData.add(m);
                    }
                    // Messages envoyés par l'enseignant
                    if (m.getExpediteurId() == utilisateurConnecte.getId()) {
                        messagesData.add(m);
                    }
                }
            }

            // Supprimer les doublons
            Set<Integer> ids = new HashSet<>();
            ObservableList<Message> uniqueMessages = FXCollections.observableArrayList();
            for (Message m : messagesData) {
                if (!ids.contains(m.getId())) {
                    ids.add(m.getId());
                    uniqueMessages.add(m);
                }
            }
            messagesData.clear();
            messagesData.addAll(uniqueMessages);

            ensMessagesTable.setItems(messagesData);
            ensMessagesEnvoyesLabel.setText(String.valueOf(messagesData.size()));

        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement messages : " + e.getMessage());
        }
    }

    private void chargerComboboxFiltres() {
        // Matières
        if (matiereNoteCombo != null) {
            matiereNoteCombo.setItems(matieresData);
            matiereNoteCombo.setValue("Toutes les matières");
        }
        if (statMatiereCombo != null) {
            statMatiereCombo.setItems(matieresData);
            statMatiereCombo.setValue("Toutes les matières");
        }

        // Étudiants
        ObservableList<String> etudiantsNoms = FXCollections.observableArrayList();
        etudiantsNoms.add("Tous les étudiants");
        try {
            for (Etudiant e : etudiantDAO.readAll()) {
                etudiantsNoms.add(e.getNomComplet());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (etudiantNoteCombo != null) {
            etudiantNoteCombo.setItems(etudiantsNoms);
            etudiantNoteCombo.setValue("Tous les étudiants");
        }

        // Semestres pour l'emploi du temps
        ObservableList<String> semestres = FXCollections.observableArrayList();
        semestres.add("1");
        semestres.add("2");
        if (ensEdtSemestreCombo != null) {
            ensEdtSemestreCombo.setItems(semestres);
            ensEdtSemestreCombo.setValue("1");
        }

        // ============================================================
        // COMMUNICATION - TYPES DE DESTINATAIRES
        // ============================================================
        ObservableList<String> typesDest = FXCollections.observableArrayList();
        typesDest.addAll("Administrateur", "Enseignant spécifique", "Étudiant spécifique",
                "Tous les enseignants", "Tous les étudiants", "Par filière");
        if (ensDestinataireTypeCombo != null) {
            ensDestinataireTypeCombo.setItems(typesDest);
            ensDestinataireTypeCombo.setValue("Administrateur");
        }

        // Écouteur sur le type de destinataire
        if (ensDestinataireTypeCombo != null) {
            ensDestinataireTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                mettreAJourDestinataireCombo(newVal);
            });
        }

        // Remplir les combos de noms et filières
        remplirCombosDestinataires();

        // Messages envoyés
        ensMessagesEnvoyesLabel.setText(String.valueOf(messagesData.size()));
    }

    // ============================================================
    // CHARGEMENT DE L'EMPLOI DU TEMPS DE L'ENSEIGNANT
    // ============================================================
    @FXML
    private void handleAfficherEmploiDuTempsEnseignant() {
        if (enseignantId == 0) {
            afficherAlerte("Erreur", "Enseignant non identifié.");
            return;
        }

        String semestreStr = ensEdtSemestreCombo.getValue();
        if (semestreStr == null) {
            afficherAlerte("Information", "Veuillez sélectionner un semestre.");
            return;
        }

        try {
            int semestre = Integer.parseInt(semestreStr);
            List<EmploiDuTemps> edtList = emploiDuTempsDAO.getEmploiDuTempsByEnseignant(enseignantId, semestre);

            ensEmploiDuTempsData.clear();
            ensEmploiDuTempsData.addAll(edtList);

            ensTotalCoursLabel.setText(String.valueOf(edtList.size()));
            ensEdtStatusLabel.setText("📅 " + edtList.size() + " cours trouvés pour le semestre " + semestre);
            statusLabel.setText("📅 Emploi du temps chargé pour le semestre " + semestre);

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger l'emploi du temps : " + e.getMessage());
        }
    }

    // ============================================================
    // GESTION DES NOTES (CRUD)
    // ============================================================
    @FXML
    private void handleAjouterNoteEnseignant() {
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une note");
        dialog.setHeaderText("📝 Ajouter une note");

        ButtonType saveButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<Etudiant> etudiantCombo = new ComboBox<>();
        try {
            etudiantCombo.getItems().addAll(etudiantDAO.readAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        etudiantCombo.setPromptText("Sélectionner un étudiant");

        ComboBox<String> matiereCombo = new ComboBox<>();
        matiereCombo.setItems(matieresData);
        matiereCombo.setPromptText("Sélectionner une matière");

        Spinner<Double> noteSpinner = new Spinner<>(0, 20, 10, 0.5);
        Spinner<Double> coeffSpinner = new Spinner<>(0.5, 5, 1, 0.5);
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextArea appreciationArea = new TextArea();
        appreciationArea.setPrefHeight(60);
        appreciationArea.setPromptText("Appréciation...");
        CheckBox valideeCheck = new CheckBox("Validée");

        grid.add(new Label("Étudiant :"), 0, 0);
        grid.add(etudiantCombo, 1, 0);
        grid.add(new Label("Matière :"), 0, 1);
        grid.add(matiereCombo, 1, 1);
        grid.add(new Label("Note /20 :"), 0, 2);
        grid.add(noteSpinner, 1, 2);
        grid.add(new Label("Coefficient :"), 0, 3);
        grid.add(coeffSpinner, 1, 3);
        grid.add(new Label("Date :"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(new Label("Appréciation :"), 0, 5);
        grid.add(appreciationArea, 1, 5);
        grid.add(valideeCheck, 1, 6);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                Etudiant etudiant = etudiantCombo.getValue();
                String matiereNom = matiereCombo.getValue();
                if (etudiant == null || matiereNom == null) {
                    afficherAlerte("Erreur", "Veuillez sélectionner un étudiant et une matière.");
                    return null;
                }

                try {
                    int matiereId = 0;
                    for (Matiere m : matiereDAO.readAll()) {
                        if (m.getNomComplet().equals(matiereNom)) {
                            matiereId = m.getId();
                            break;
                        }
                    }

                    Note note = new Note(
                            etudiant.getId(),
                            matiereId,
                            enseignantId,
                            noteSpinner.getValue(),
                            datePicker.getValue(),
                            coeffSpinner.getValue(),
                            appreciationArea.getText(),
                            "Devoir",
                            1
                    );
                    note.setValidee(valideeCheck.isSelected());
                    noteDAO.create(note);
                    return note;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible d'ajouter : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(note -> {
            chargerNotesEnseignant();
            chargerStatistiques();
            rechargerComboboxNotes();
            afficherAlerte("Succès", "✅ Note ajoutée !");
        });
    }

    @FXML
    private void handleModifierNoteEnseignant() {
        Note selected = notesEnseignantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucune note sélectionnée", "Veuillez sélectionner une note.");
            return;
        }

        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Modifier une note");
        dialog.setHeaderText("✏️ Modifier la note");

        ButtonType modifyButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifyButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        Spinner<Double> noteSpinner = new Spinner<>(0, 20, selected.getValeur(), 0.5);
        Spinner<Double> coeffSpinner = new Spinner<>(0.5, 5, selected.getCoefficient(), 0.5);
        DatePicker datePicker = new DatePicker(selected.getDateEvaluation());
        TextArea appreciationArea = new TextArea(selected.getAppreciation());
        appreciationArea.setPrefHeight(60);
        CheckBox valideeCheck = new CheckBox("Validée");
        valideeCheck.setSelected(selected.isValidee());

        grid.add(new Label("Note /20 :"), 0, 0);
        grid.add(noteSpinner, 1, 0);
        grid.add(new Label("Coefficient :"), 0, 1);
        grid.add(coeffSpinner, 1, 1);
        grid.add(new Label("Date :"), 0, 2);
        grid.add(datePicker, 1, 2);
        grid.add(new Label("Appréciation :"), 0, 3);
        grid.add(appreciationArea, 1, 3);
        grid.add(valideeCheck, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifyButton) {
                try {
                    selected.setValeur(noteSpinner.getValue());
                    selected.setCoefficient(coeffSpinner.getValue());
                    selected.setDateEvaluation(datePicker.getValue());
                    selected.setAppreciation(appreciationArea.getText());
                    selected.setValidee(valideeCheck.isSelected());
                    noteDAO.update(selected);
                    return selected;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible de modifier : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(note -> {
            chargerNotesEnseignant();
            chargerStatistiques();
            rechargerComboboxNotes();
            afficherAlerte("Succès", "✅ Note modifiée !");
        });
    }

    @FXML
    private void handleSupprimerNoteEnseignant() {
        Note selected = notesEnseignantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucune note sélectionnée", "Veuillez sélectionner une note.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la note");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette note ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                noteDAO.delete(selected.getId());
                chargerNotesEnseignant();
                chargerStatistiques();
                rechargerComboboxNotes();
                afficherAlerte("Succès", "✅ Note supprimée !");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAfficherToutesNotesEnseignant() {
        notesEnseignantTable.setItems(notesData);
        if (matiereNoteCombo != null) {
            matiereNoteCombo.setValue("Toutes les matières");
        }
        if (etudiantNoteCombo != null) {
            etudiantNoteCombo.setValue("Tous les étudiants");
        }
        notesStatusLabel.setText("📊 " + notesData.size() + " notes affichées");
        statusLabel.setText("📊 " + notesData.size() + " notes affichées");
    }

    // ============================================================
    // STATISTIQUES
    // ============================================================
    @FXML
    private void handleAfficherStatistiques() {
        String matiereNom = statMatiereCombo.getValue();
        if (matiereNom == null || matiereNom.equals("Toutes les matières")) {
            afficherAlerte("Information", "Veuillez sélectionner une matière spécifique.");
            return;
        }

        try {
            int matiereId = 0;
            for (Matiere m : matiereDAO.readAll()) {
                if (m.getNomComplet().equals(matiereNom)) {
                    matiereId = m.getId();
                    break;
                }
            }

            if (matiereId == 0) {
                afficherAlerte("Erreur", "Matière non trouvée.");
                return;
            }

            List<Note> notes = noteDAO.getNotesByMatiere(matiereId);

            int nbEtudiants = notes.size();
            double somme = 0;
            int reussites = 0;
            for (Note n : notes) {
                somme += n.getValeur();
                if (n.getValeur() >= 10) reussites++;
            }

            double moyenne = nbEtudiants > 0 ? somme / nbEtudiants : 0;
            double taux = nbEtudiants > 0 ? (double) reussites / nbEtudiants * 100 : 0;

            ensMoyenneClasse.setText(String.format("%.2f", moyenne));
            ensTauxReussite.setText(String.format("%.1f%%", taux));
            ensNbEtudiants.setText(String.valueOf(nbEtudiants));
            ensNbNotes.setText(String.valueOf(notes.size()));

            ObservableList<String> listeEtudiants = FXCollections.observableArrayList();
            for (Note n : notes) {
                listeEtudiants.add(n.getNomEtudiantComplet() + " : " + String.format("%.1f", n.getValeur()) + "/20");
            }
            ensStatListeEtudiants.setItems(listeEtudiants);

            afficherGraphiqueStatistiques(notes, matiereNom);

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les statistiques : " + e.getMessage());
        }
    }

    private void afficherGraphiqueStatistiques(List<Note> notes, String matiereNom) {
        ensGraphiqueContainer.setVisible(true);
        ensGraphiqueContainer.getChildren().clear();

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis(0, 20, 2);
        yAxis.setLabel("Notes");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("📊 Distribution des notes - " + matiereNom);
        chart.setPrefHeight(250);

        Map<Double, Long> distribution = notes.stream()
                .collect(Collectors.groupingBy(
                        n -> Math.floor(n.getValeur() / 2) * 2,
                        Collectors.counting()
                ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'étudiants");
        for (Map.Entry<Double, Long> entry : distribution.entrySet()) {
            String label = String.format("%.0f-%.0f", entry.getKey(), entry.getKey() + 2);
            series.getData().add(new XYChart.Data<>(label, entry.getValue()));
        }
        chart.getData().add(series);
        ensGraphiqueContainer.getChildren().add(chart);
    }

    private void chargerStatistiques() {
        try {
            int nbNotes = noteDAO.countByEnseignant(enseignantId);
            ensNbNotes.setText(String.valueOf(nbNotes));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ============================================================
    // COMMUNICATION
    // ============================================================
    @FXML
    private void handleEnseignantEnvoyerMessage() {
        String type = ensDestinataireTypeCombo.getValue();
        String objet = ensMessageObjetField.getText();
        String contenu = ensMessageContentArea.getText();

        if (type == null || objet.isEmpty() || contenu.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        // Construire le destinataire
        String destinataire = construireDestinataire(type);

        if (destinataire == null || destinataire.isEmpty()) {
            afficherAlerte("Erreur", "Aucun destinataire sélectionné.");
            return;
        }

        try {
            Message message = new Message(
                    utilisateurConnecte.getId(),
                    "ENSEIGNANT",
                    destinataire,
                    objet,
                    contenu
            );

            // Ajouter des informations supplémentaires selon le type
            if (type.equals("Par filière") && ensDestinataireFiliereCombo.getValue() != null) {
                message.setFiliere(ensDestinataireFiliereCombo.getValue());
            }

            messageDAO.create(message);
            messagesData.add(0, message);
            ensMessagesEnvoyesLabel.setText(String.valueOf(messagesData.size()));

            afficherAlerte("Succès", "✅ Message envoyé avec succès !\n\n" +
                    "📋 Destinataire : " + destinataire + "\n" +
                    "📝 Objet : " + objet);

            ensMessageObjetField.clear();
            ensMessageContentArea.clear();
            statusLabel.setText("✉️ Message envoyé à " + destinataire);

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible d'envoyer : " + e.getMessage());
        }
    }

    private String construireDestinataire(String type) {
        switch (type) {
            case "Administrateur":
                return "Administrateur";

            case "Enseignant spécifique":
                String enseignant = ensDestinataireNomCombo.getValue();
                return enseignant != null ? "Enseignant : " + enseignant : null;

            case "Étudiant spécifique":
                String etudiant = ensDestinataireNomCombo.getValue();
                return etudiant != null ? "Étudiant : " + etudiant : null;

            case "Tous les enseignants":
                return "Tous les enseignants";

            case "Tous les étudiants":
                return "Tous les étudiants";

            case "Par filière":
                String filiere = ensDestinataireFiliereCombo.getValue();
                return filiere != null ? "Filière : " + filiere : null;

            default:
                return null;
        }
    }

    @FXML
    private void handleEnseignantEffacerMessage() {
        ensMessageObjetField.clear();
        ensMessageContentArea.clear();
        ensDestinataireAffiche.setText("Aucun destinataire sélectionné");
        statusLabel.setText("✉️ Message effacé");
    }

    // ============================================================
    // EXPORT
    // ============================================================
    @FXML
    private void handleExporterNotesCSVEnseignant() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les notes en CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("notes_enseignant_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Étudiant;Matière;Note;Coefficient;Points;Type;Date;Appréciation;Validée\n");
                for (Note n : notesEnseignantTable.getItems()) {
                    writer.write(String.format("%s;%s;%.1f;%.1f;%.1f;%s;%s;%s;%s\n",
                            n.getNomEtudiantComplet(),
                            n.getNomMatiere(),
                            n.getValeur(),
                            n.getCoefficient(),
                            n.getPoints(),
                            n.getType(),
                            n.getDateEvaluation() != null ? n.getDateEvaluation().toString() : "",
                            n.getAppreciation() != null ? n.getAppreciation() : "",
                            n.isValidee() ? "Oui" : "Non"
                    ));
                }
                afficherAlerte("Succès", "✅ Export CSV réussi : " + file.getName());
            } catch (IOException e) {
                afficherAlerte("Erreur", "Erreur lors de l'export : " + e.getMessage());
            }
        }
    }

    // ============================================================
    // FONCTIONS GLOBALES
    // ============================================================
    @FXML
    private void handleRefresh() {
        chargerMatieresEnseignees();
        chargerNotesEnseignant();
        chargerMessages();
        rechargerComboboxNotes();
        afficherAlerte("Info", "🔄 Données rafraîchies !");
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
// RECHARGER LES COMBOBOX DES NOTES - CORRIGÉ
// ============================================================
    private void rechargerComboboxNotes() {
        if (matiereNoteCombo == null) return;

        // 1. Matières
        ObservableList<String> matieresList = FXCollections.observableArrayList();
        matieresList.add("Toutes les matières");

        for (Note n : notesData) {
            String nomMatiere = n.getNomMatiere();
            if (nomMatiere != null && !matieresList.contains(nomMatiere)) {
                matieresList.add(nomMatiere);
            }
        }
        matiereNoteCombo.setItems(matieresList);
        matiereNoteCombo.setValue("Toutes les matières");

        // 2. Étudiants
        ObservableList<String> etudiantsList = FXCollections.observableArrayList();
        etudiantsList.add("Tous les étudiants");

        for (Note n : notesData) {
            String nomEtudiant = n.getNomEtudiantComplet();
            if (nomEtudiant != null && !etudiantsList.contains(nomEtudiant)) {
                etudiantsList.add(nomEtudiant);
            }
        }
        etudiantNoteCombo.setItems(etudiantsList);
        etudiantNoteCombo.setValue("Tous les étudiants");

        System.out.println("📊 ComboBox rechargées : " + matieresList.size() + " matières, " + etudiantsList.size() + " étudiants");
    }

    // ============================================================
// FILTRES NOTES - CORRIGÉ
// ============================================================
    @FXML
    private void handleFiltrerNotesEnseignant() {
        String matiere = matiereNoteCombo.getValue();
        String etudiant = etudiantNoteCombo.getValue();

        System.out.println("🔍 FILTRE - Matière: " + matiere + ", Étudiant: " + etudiant);
        System.out.println("🔍 FILTRE - Notes disponibles: " + notesData.size());

        boolean toutesMatieres = matiere == null || matiere.equals("Toutes les matières");
        boolean tousEtudiants = etudiant == null || etudiant.equals("Tous les étudiants");

        ObservableList<Note> filtered = FXCollections.observableArrayList();
        for (Note n : notesData) {
            boolean matchMatiere = toutesMatieres ||
                    (n.getNomMatiere() != null && n.getNomMatiere().equals(matiere));
            boolean matchEtudiant = tousEtudiants ||
                    (n.getNomEtudiantComplet() != null && n.getNomEtudiantComplet().equals(etudiant));

            if (matchMatiere && matchEtudiant) {
                filtered.add(n);
            }
        }

        notesEnseignantTable.setItems(filtered);
        notesStatusLabel.setText("📊 " + filtered.size() + " notes affichées sur " + notesData.size());
        statusLabel.setText("📊 " + filtered.size() + " notes affichées");

        System.out.println("🔍 FILTRE - Résultat: " + filtered.size() + " notes");
    }

    // ============================================================
// NOTES ENSEIGNANT - AVEC DÉBOGAGE
// ============================================================
    private void chargerNotesEnseignant() {
        if (enseignantId == 0) return;

        try {
            List<Note> notes = noteDAO.getNotesByEnseignant(enseignantId);
            notesData.clear();
            notesData.addAll(notes);
            notesEnseignantTable.setItems(notesData);

            // ✅ DÉBOGAGE
            System.out.println("📝 Notes chargées : " + notesData.size());
            for (Note n : notesData) {
                System.out.println("   - " + n.getNomMatiere() + " | " + n.getNomEtudiantComplet());
            }

            // ✅ Recharger les ComboBox après chargement
            rechargerComboboxNotes();

        } catch (SQLException e) {
            System.err.println("❌ Erreur chargement notes : " + e.getMessage());
        }
    }

    // ============================================================
// GESTION DES DESTINATAIRES
// ============================================================

    private void remplirCombosDestinataires() {
        // Enseignants
        ObservableList<String> enseignantsList = FXCollections.observableArrayList();
        try {
            for (Enseignant e : enseignantDAO.readAll()) {
                if (e.getId() != enseignantId) { // Ne pas inclure l'enseignant lui-même
                    enseignantsList.add(e.getNomComplet());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ensDestinataireNomCombo.setItems(enseignantsList);

        // Filières
        ObservableList<String> filieresList = FXCollections.observableArrayList();
        try {
            for (Etudiant e : etudiantDAO.readAll()) {
                if (e.getFiliere() != null && !filieresList.contains(e.getFiliere())) {
                    filieresList.add(e.getFiliere());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ensDestinataireFiliereCombo.setItems(filieresList);
    }

    private void mettreAJourDestinataireCombo(String type) {
        if (type == null) return;

        // Cacher tous les combos
        ensDestinataireNomCombo.setVisible(false);
        ensDestinataireFiliereCombo.setVisible(false);

        switch (type) {
            case "Enseignant spécifique":
                ensDestinataireNomCombo.setVisible(true);
                remplirCombosDestinataires();
                ensDestinataireAffiche.setText("👤 Sélectionnez un enseignant");
                break;

            case "Étudiant spécifique":
                ensDestinataireNomCombo.setVisible(true);
                // Remplir avec les étudiants
                ObservableList<String> etudiantsList = FXCollections.observableArrayList();
                try {
                    for (Etudiant e : etudiantDAO.readAll()) {
                        etudiantsList.add(e.getNomComplet());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                ensDestinataireNomCombo.setItems(etudiantsList);
                ensDestinataireAffiche.setText("👤 Sélectionnez un étudiant");
                break;

            case "Administrateur":
                ensDestinataireAffiche.setText("👑 Administrateur");
                break;

            case "Tous les enseignants":
                ensDestinataireAffiche.setText("👨‍🏫 Tous les enseignants");
                break;

            case "Tous les étudiants":
                ensDestinataireAffiche.setText("👨‍🎓 Tous les étudiants");
                break;

            case "Par filière":
                ensDestinataireFiliereCombo.setVisible(true);
                ensDestinataireAffiche.setText("📚 Sélectionnez une filière");
                break;

            default:
                ensDestinataireAffiche.setText("Aucun destinataire sélectionné");
        }

        // Mettre à jour l'affichage du destinataire sélectionné
        if (ensDestinataireNomCombo.isVisible()) {
            ensDestinataireNomCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    ensDestinataireAffiche.setText("👤 " + newVal);
                }
            });
        }

        if (ensDestinataireFiliereCombo.isVisible()) {
            ensDestinataireFiliereCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    ensDestinataireAffiche.setText("📚 Filière : " + newVal);
                }
            });
        }
    }
}