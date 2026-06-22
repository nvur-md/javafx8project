package unischool.controllers;

import javafx.scene.layout.HBox;
import unischool.dao.*;
import unischool.models.*;
import javafx.application.Platform;
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
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AdminController implements Initializable {

    // ============================================================
    // SESSION UTILISATEUR
    // ============================================================
    private Utilisateur utilisateurConnecte;

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
    // COMPOSANTS FXML - ONGLET GESTION ACADÉMIQUE
    // ============================================================
    @FXML private ComboBox<String> filiereFiltreCombo;
    @FXML private Label totalCoursLabel;
    @FXML private Label edtStatusLabel;
    @FXML private ComboBox<String> anneeFiltreCombo;
    @FXML private TableView<Etudiant> etudiantTable;
    @FXML private TableColumn<Etudiant, Integer> etudiantIdCol;
    @FXML private TableColumn<Etudiant, String> etudiantNomCol;
    @FXML private TableColumn<Etudiant, String> etudiantPrenomCol;
    @FXML private TableColumn<Etudiant, String> etudiantEmailCol;
    @FXML private TableColumn<Etudiant, String> etudiantFiliereCol;
    @FXML private TableColumn<Etudiant, Integer> etudiantAnneeCol;
    @FXML private TableColumn<Etudiant, Integer> etudiantAbsencesCol;
    @FXML private TableColumn<Etudiant, Double> etudiantMoyenneCol;
    @FXML private TableColumn<Etudiant, String> etudiantStatutCol;
    @FXML private Label totalEtudiantsFiltres;
    @FXML private Label totalEnseignantsFiltres;
    @FXML private Label enseignantStatusLabel;

    @FXML private Label detailsEtudiantNom;
    @FXML private Label detailsEtudiantFiliere;
    @FXML private Label detailsEtudiantMoyenne;
    @FXML private Label detailsEtudiantMention;
    @FXML private Label detailsEtudiantAbsences;
    @FXML private TableView<Note> detailsNotesTable;


    // ============================================================
    // COMPOSANTS FXML - ONGLET ENSEIGNANTS
    // ============================================================
    @FXML private ComboBox<String> enseignantFiliereFiltre;
    @FXML private ComboBox<String> enseignantMatiereFiltre;
    @FXML private TableView<Enseignant> enseignantTable;
    @FXML private TableColumn<Enseignant, Integer> enseignantIdCol;
    @FXML private TableColumn<Enseignant, String> enseignantNomCol;
    @FXML private TableColumn<Enseignant, String> enseignantPrenomCol;
    @FXML private TableColumn<Enseignant, String> enseignantEmailCol;
    @FXML private TableColumn<Enseignant, String> enseignantFiliereCol;
    @FXML private TableColumn<Enseignant, String> enseignantSpecialiteCol;
    @FXML private TableColumn<Enseignant, String> enseignantStatutCol;


    // ============================================================
    // COMPOSANTS FXML - ONGLET EMPLOI DU TEMPS
    // ============================================================
    @FXML private ComboBox<String> edtFiliereCombo;
    @FXML private ComboBox<String> edtAnneeCombo;
    @FXML private ComboBox<String> edtSemestreCombo;
    @FXML private TableView<EmploiDuTemps> emploiDuTempsTable;
    @FXML private TableColumn<EmploiDuTemps, String> edtJourCol;
    @FXML private TableColumn<EmploiDuTemps, String> edtHoraireCol;
    @FXML private TableColumn<EmploiDuTemps, String> edtMatiereCol;
    @FXML private TableColumn<EmploiDuTemps, String> edtEnseignantCol;
    @FXML private TableColumn<EmploiDuTemps, String> edtSalleCol;
    @FXML private TableColumn<EmploiDuTemps, String> edtTypeCol;

    // ============================================================
    // COMPOSANTS FXML - ONGLET NOTES
    // ============================================================
    @FXML private Label moyenneGeneraleLabel;
    @FXML private TableView<Note> notesTable;
    @FXML private TableColumn<Note, Integer> noteIdCol;
    @FXML private TableColumn<Note, String> noteEtudiantCol;
    @FXML private TableColumn<Note, String> noteMatiereCol;
    @FXML private TableColumn<Note, Double> noteValeurCol;
    @FXML private TableColumn<Note, Double> noteCoeffCol;
    @FXML private TableColumn<Note, Double> notePointsCol;
    @FXML private TableColumn<Note, String> noteTypeCol;
    @FXML private TableColumn<Note, String> noteDateCol;
    @FXML private TableColumn<Note, String> noteAppreciationCol;
    @FXML private TableColumn<Note, Boolean> noteValideeCol;
    @FXML private Label notesStatusLabel;

    @FXML private VBox evolutionChartContainer;

    // ============================================================
    // COMPOSANTS FXML - ONGLET COMMUNICATION
    // ============================================================
    @FXML private ComboBox<String> destinataireTypeCombo;
    @FXML private ComboBox<String> destinataireNomCombo;
    @FXML private ComboBox<String> destinataireFiliereCombo;
    @FXML private ComboBox<String> destinataireAnneeCombo;
    @FXML private TextField messageObjetField;
    @FXML private TextArea messageContentArea;
    @FXML private Label messagesEnvoyesLabel;
    @FXML private Label destinataireAffiche;
    @FXML private TableView<Message> messagesTable;
    @FXML private TableColumn<Message, String> msgDateCol;
    @FXML private TableColumn<Message, String> msgExpediteurCol;
    @FXML private TableColumn<Message, String> msgDestinataireCol;
    @FXML private TableColumn<Message, String> msgObjetCol;
    @FXML private TableColumn<Message, String> msgContenuCol;
    @FXML private TableColumn<Message, Boolean> msgLuCol;

    // ============================================================
    // COMPOSANTS FXML - ONGLET STATISTIQUES
    // ============================================================
    @FXML private Label userLabel;
    @FXML private Label statusLabel;
    @FXML private Label statTotalEtudiants;
    @FXML private Label statTotalEnseignants;
    @FXML private Label statTotalMatieres;
    @FXML private Label statTotalNotes;
    @FXML private Label statTauxReussite;
    @FXML private Label statMoyenneGenerale;
    @FXML private Label statTotalAbsences;
    @FXML private Label statMeilleurEtudiant;
    @FXML private ListView<String> filiereListView;
    @FXML private ListView<String> notesRepartitionListView;

    // ============================================================
    // OBSERVABLE LISTS
    // ============================================================
    private ObservableList<Etudiant> etudiantsData = FXCollections.observableArrayList();
    private ObservableList<Enseignant> enseignantsData = FXCollections.observableArrayList();
    private ObservableList<Note> notesData = FXCollections.observableArrayList();
    private ObservableList<Message> messagesData = FXCollections.observableArrayList();
    private ObservableList<EmploiDuTemps> emploiDuTempsData = FXCollections.observableArrayList();
    private int messagesEnvoyes = 0;

    // ============================================================
    // SETTER POUR L'UTILISATEUR
    // ============================================================
    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateurConnecte = utilisateur;
        if (userLabel != null) {
            userLabel.setText("👑 " + utilisateur.getNomComplet());
        }
        chargerToutesLesDonnees();
        System.out.println("✅ Admin connecté : " + utilisateur.getNomComplet());
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
        setupEtudiantTableColumns();
        setupEnseignantTableColumns();
        setupNotesTableColumns();
        setupDetailsNotesTableColumns();
        setupEmploiDuTempsTableColumns();
        setupMessagesTableColumns();

        // Charger les ComboBox
        chargerComboboxFiltres();

        // Écouteur sélection étudiant
        etudiantTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        afficherDetailsEtudiant(newVal);
                    }
                }
        );

        // Écouteur sélection matière EDT
        edtFiliereCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("Toutes les filières")) {
                handleAfficherEmploiDuTemps();
            }
        });

        System.out.println("✅ Interface Administrateur initialisée");
    }

    // ============================================================
    // CONFIGURATION DES COLONNES
    // ============================================================
    private void setupEtudiantTableColumns() {
        etudiantIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        etudiantNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        etudiantPrenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        etudiantEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        etudiantFiliereCol.setCellValueFactory(new PropertyValueFactory<>("filiere"));
        etudiantAnneeCol.setCellValueFactory(new PropertyValueFactory<>("anneeEtude"));

        etudiantAbsencesCol.setCellValueFactory(cellData -> {
            Etudiant e = cellData.getValue();
            try {
                int absences = absenceDAO.countByEtudiant(e.getId());
                return new javafx.beans.property.SimpleIntegerProperty(absences).asObject();
            } catch (SQLException ex) {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
        });

        etudiantMoyenneCol.setCellValueFactory(cellData -> {
            Etudiant e = cellData.getValue();
            try {
                double moyenne = noteDAO.getMoyenneByEtudiant(e.getId());
                return new javafx.beans.property.SimpleDoubleProperty(moyenne).asObject();
            } catch (SQLException ex) {
                return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
            }
        });

        etudiantStatutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        etudiantStatutCol.setCellFactory(column -> new TableCell<Etudiant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("ACTIF".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if ("INACTIF".equals(item)) {
                        setStyle("-fx-text-fill: #e67e22;");
                    } else if ("EXCLU".equals(item)) {
                        setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #3498db;");
                    }
                }
            }
        });

        etudiantTable.setItems(etudiantsData);
    }

    // ============================================================
// CONFIGURATION TABLEAU ENSEIGNANTS
// ============================================================
    private void setupEnseignantTableColumns() {
        enseignantIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        enseignantNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        enseignantPrenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        enseignantEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        enseignantFiliereCol.setCellValueFactory(cellData -> {
            String filiere = cellData.getValue().getSpecialite();
            return new SimpleStringProperty(filiere != null ? filiere : "Non assigné");
        });
        enseignantSpecialiteCol.setCellValueFactory(new PropertyValueFactory<>("specialite"));
        enseignantStatutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        enseignantStatutCol.setCellFactory(column -> new TableCell<Enseignant, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("ACTIF".equals(item)) {
                        setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    } else if ("CONGE".equals(item)) {
                        setStyle("-fx-text-fill: #f39c12;");
                    } else {
                        setStyle("-fx-text-fill: #e74c3c;");
                    }
                }
            }
        });

        enseignantTable.setItems(enseignantsData);
    }

    private void setupNotesTableColumns() {
        noteIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        noteEtudiantCol.setCellValueFactory(new PropertyValueFactory<>("nomEtudiantComplet"));
        noteMatiereCol.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        noteValeurCol.setCellValueFactory(new PropertyValueFactory<>("valeur"));
        noteCoeffCol.setCellValueFactory(new PropertyValueFactory<>("coefficient"));
        notePointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        noteTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        noteDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateEvaluation();
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

    private void setupDetailsNotesTableColumns() {
        if (detailsNotesTable == null || detailsNotesTable.getColumns().isEmpty()) return;

        TableColumn<Note, String> colMatiere = (TableColumn<Note, String>) detailsNotesTable.getColumns().get(0);
        colMatiere.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));

        TableColumn<Note, Double> colNote = (TableColumn<Note, Double>) detailsNotesTable.getColumns().get(1);
        colNote.setCellValueFactory(new PropertyValueFactory<>("valeur"));

        TableColumn<Note, Double> colCoeff = (TableColumn<Note, Double>) detailsNotesTable.getColumns().get(2);
        colCoeff.setCellValueFactory(new PropertyValueFactory<>("coefficient"));

        TableColumn<Note, Double> colPoints = (TableColumn<Note, Double>) detailsNotesTable.getColumns().get(3);
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));

        TableColumn<Note, String> colDate = (TableColumn<Note, String>) detailsNotesTable.getColumns().get(4);
        colDate.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getDateEvaluation();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });

        TableColumn<Note, String> colAppreciation = (TableColumn<Note, String>) detailsNotesTable.getColumns().get(5);
        colAppreciation.setCellValueFactory(new PropertyValueFactory<>("appreciation"));
    }

    private void setupEmploiDuTempsTableColumns() {
        edtJourCol.setCellValueFactory(new PropertyValueFactory<>("jour"));
        edtHoraireCol.setCellValueFactory(new PropertyValueFactory<>("heureFormatee"));
        edtMatiereCol.setCellValueFactory(new PropertyValueFactory<>("nomMatiere"));
        edtEnseignantCol.setCellValueFactory(new PropertyValueFactory<>("nomEnseignantComplet"));
        edtSalleCol.setCellValueFactory(new PropertyValueFactory<>("salle"));
        edtTypeCol.setCellValueFactory(new PropertyValueFactory<>("typeCours"));

        emploiDuTempsTable.setItems(emploiDuTempsData);
    }

    private void setupMessagesTableColumns() {
        msgDateCol.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
        msgExpediteurCol.setCellValueFactory(new PropertyValueFactory<>("nomExpediteur"));
        msgDestinataireCol.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getDestinataireType();
            return new SimpleStringProperty(type != null ? type : "Tous");
        });
        msgObjetCol.setCellValueFactory(new PropertyValueFactory<>("objet"));
        msgContenuCol.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        msgLuCol.setCellValueFactory(new PropertyValueFactory<>("lu"));

        msgLuCol.setCellFactory(column -> new TableCell<Message, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "✅ Lu" : "📩 Non lu");
                }
            }
        });

        messagesTable.setItems(messagesData);
    }

    // ============================================================
    // CHARGEMENT DES COMBOBOX
    // ============================================================
    private void chargerComboboxFiltres() {
        // Filières
        ObservableList<String> filieres = FXCollections.observableArrayList();
        filieres.add("Toutes les filières");
        try {
            for (Etudiant e : etudiantDAO.readAll()) {
                if (e.getFiliere() != null && !filieres.contains(e.getFiliere())) {
                    filieres.add(e.getFiliere());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        filiereFiltreCombo.setItems(filieres);
        filiereFiltreCombo.setValue("Toutes les filières");
        enseignantFiliereFiltre.setItems(filieres);
        enseignantFiliereFiltre.setValue("Toutes les filières");
        edtFiliereCombo.setItems(filieres);
        edtFiliereCombo.setValue("Toutes les filières");

        // Années
        ObservableList<String> annees = FXCollections.observableArrayList();
        annees.add("Toutes");
        for (int i = 1; i <= 5; i++) annees.add(String.valueOf(i));
        anneeFiltreCombo.setItems(annees);
        anneeFiltreCombo.setValue("Toutes");
        edtAnneeCombo.setItems(annees);
        edtAnneeCombo.setValue("1");

        // Semestres
        ObservableList<String> semestres = FXCollections.observableArrayList();
        semestres.add("1");
        semestres.add("2");
        edtSemestreCombo.setItems(semestres);
        edtSemestreCombo.setValue("1");

        // Matières pour les enseignants
        try {
            List<Matiere> matieres = matiereDAO.readAll();
            ObservableList<String> matieresNoms = FXCollections.observableArrayList();
            matieresNoms.add("Toutes les matières");
            for (Matiere m : matieres) {
                matieresNoms.add(m.getNom());
            }
            enseignantMatiereFiltre.setItems(matieresNoms);
            enseignantMatiereFiltre.setValue("Toutes les matières");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Communication
        ObservableList<String> types = FXCollections.observableArrayList();
        types.addAll("Tous les étudiants", "Tous les enseignants", "Par filière", "Par année");
        destinataireTypeCombo.setItems(types);
        destinataireTypeCombo.setValue("Tous les étudiants");

        ObservableList<String> filieresDest = FXCollections.observableArrayList();
        filieresDest.add("Toutes");
        for (String f : filieres) {
            if (!f.equals("Toutes les filières")) filieresDest.add(f);
        }
        destinataireFiliereCombo.setItems(filieresDest);
        destinataireFiliereCombo.setValue("Toutes");

        ObservableList<String> anneesDest = FXCollections.observableArrayList();
        anneesDest.add("Toutes");
        for (int i = 1; i <= 5; i++) anneesDest.add(String.valueOf(i));
        destinataireAnneeCombo.setItems(anneesDest);
        destinataireAnneeCombo.setValue("Toutes");
    }

    // ============================================================
    // CHARGEMENT DES DONNÉES
    // ============================================================
    private void chargerToutesLesDonnees() {
        try {
            // Étudiants
            List<Etudiant> etudiants = etudiantDAO.readAll();
            etudiantsData.clear();
            etudiantsData.addAll(etudiants);
            etudiantTable.setItems(etudiantsData);

            // Enseignants
            List<Enseignant> enseignants = enseignantDAO.readAll();
            enseignantsData.clear();
            enseignantsData.addAll(enseignants);
            enseignantTable.setItems(enseignantsData);

            // Notes
            List<Note> notes = noteDAO.readAll();
            notesData.clear();
            notesData.addAll(notes);
            notesTable.setItems(notesData);

            // Messages
            List<Message> messages = messageDAO.getAllMessages();
            messagesData.clear();
            messagesData.addAll(messages);
            messagesTable.setItems(messagesData);
            messagesEnvoyes = messagesData.size();
            messagesEnvoyesLabel.setText(String.valueOf(messagesEnvoyes));

            // Statistiques
            chargerStatistiques();

            // Total étudiants
            totalEtudiantsFiltres.setText(String.valueOf(etudiantsData.size()));

        } catch (SQLException e) {
            e.printStackTrace();
            afficherAlerte("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    private void chargerStatistiques() {
        try {
            int nbEtudiants = etudiantDAO.count();
            int nbEnseignants = enseignantDAO.count();
            int nbMatieres = matiereDAO.count();
            int nbNotes = noteDAO.count();
            double moyenne = noteDAO.getMoyenneGenerale();
            int nbAbsences = absenceDAO.countAll();

            statTotalEtudiants.setText(String.valueOf(nbEtudiants));
            statTotalEnseignants.setText(String.valueOf(nbEnseignants));
            statTotalMatieres.setText(String.valueOf(nbMatieres));
            statTotalNotes.setText(String.valueOf(nbNotes));
            statMoyenneGenerale.setText(String.format("%.2f", moyenne));
            statTotalAbsences.setText(String.valueOf(nbAbsences));

            // Taux de réussite
            int reussites = 0;
            for (Note n : notesData) {
                if (n.getValeur() >= 10) reussites++;
            }
            double taux = nbNotes > 0 ? (double) reussites / nbNotes * 100 : 0;
            statTauxReussite.setText(String.format("%.1f%%", taux));

            // Meilleur étudiant
            chargerMeilleurEtudiant();

            // Répartition par filière
            chargerRepartitionFiliere();

            // Répartition des notes
            chargerRepartitionNotes();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void chargerMeilleurEtudiant() {
        try {
            Etudiant meilleur = null;
            double meilleureMoyenne = -1;
            for (Etudiant e : etudiantsData) {
                double moyenne = noteDAO.getMoyenneByEtudiant(e.getId());
                if (moyenne > meilleureMoyenne) {
                    meilleureMoyenne = moyenne;
                    meilleur = e;
                }
            }
            if (meilleur != null) {
                statMeilleurEtudiant.setText(meilleur.getNomComplet() + " (" + String.format("%.2f", meilleureMoyenne) + ")");
            } else {
                statMeilleurEtudiant.setText("-");
            }
        } catch (SQLException e) {
            statMeilleurEtudiant.setText("Erreur");
        }
    }

    private void chargerRepartitionFiliere() {
        Map<String, Long> repartition = etudiantsData.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getFiliere() + " (Année " + e.getAnneeEtude() + ")",
                        Collectors.counting()
                ));

        ObservableList<String> items = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : repartition.entrySet()) {
            items.add(entry.getKey() + " : " + entry.getValue() + " étudiant(s)");
        }
        filiereListView.setItems(items);
    }

    private void chargerRepartitionNotes() {
        Map<String, Long> repartition = new LinkedHashMap<>();
        repartition.put("Excellent (16-20)", 0L);
        repartition.put("Bien (14-16)", 0L);
        repartition.put("Assez Bien (12-14)", 0L);
        repartition.put("Passable (10-12)", 0L);
        repartition.put("Insuffisant (0-10)", 0L);

        for (Note n : notesData) {
            double v = n.getValeur();
            if (v >= 16) repartition.put("Excellent (16-20)", repartition.get("Excellent (16-20)") + 1);
            else if (v >= 14) repartition.put("Bien (14-16)", repartition.get("Bien (14-16)") + 1);
            else if (v >= 12) repartition.put("Assez Bien (12-14)", repartition.get("Assez Bien (12-14)") + 1);
            else if (v >= 10) repartition.put("Passable (10-12)", repartition.get("Passable (10-12)") + 1);
            else repartition.put("Insuffisant (0-10)", repartition.get("Insuffisant (0-10)") + 1);
        }

        ObservableList<String> items = FXCollections.observableArrayList();
        for (Map.Entry<String, Long> entry : repartition.entrySet()) {
            items.add(entry.getKey() + " : " + entry.getValue() + " note(s)");
        }
        notesRepartitionListView.setItems(items);
    }

    // ============================================================
    // DÉTAILS ÉTUDIANT
    // ============================================================
    private void afficherDetailsEtudiant(Etudiant etudiant) {
        try {
            detailsEtudiantNom.setText(etudiant.getNomComplet());
            detailsEtudiantFiliere.setText(etudiant.getFiliere() + " (Année " + etudiant.getAnneeEtude() + ")");

            double moyenne = noteDAO.getMoyenneByEtudiant(etudiant.getId());
            detailsEtudiantMoyenne.setText(String.format("%.2f/20", moyenne));
            detailsEtudiantMention.setText(obtenirMention(moyenne));

            int absences = absenceDAO.countByEtudiant(etudiant.getId());
            detailsEtudiantAbsences.setText(absences + " absence(s)");

            List<Note> notes = noteDAO.getNotesByEtudiant(etudiant.getId());
            ObservableList<Note> notesEtudiant = FXCollections.observableArrayList(notes);
            detailsNotesTable.setItems(notesEtudiant);

        } catch (SQLException e) {
            e.printStackTrace();
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
    // FILTRES
    // ============================================================
    @FXML
    private void handleFiltrerEtudiants() {
        String filiere = filiereFiltreCombo.getValue();
        String anneeStr = anneeFiltreCombo.getValue();

        ObservableList<Etudiant> filtered = FXCollections.observableArrayList();
        for (Etudiant e : etudiantsData) {
            boolean matchFiliere = filiere == null || filiere.equals("Toutes les filières") ||
                    (e.getFiliere() != null && e.getFiliere().equals(filiere));
            boolean matchAnnee = anneeStr == null || anneeStr.equals("Toutes") ||
                    String.valueOf(e.getAnneeEtude()).equals(anneeStr);
            if (matchFiliere && matchAnnee) {
                filtered.add(e);
            }
        }
        etudiantTable.setItems(filtered);
        totalEtudiantsFiltres.setText(String.valueOf(filtered.size()));
        statusLabel.setText("📋 " + filtered.size() + " étudiants affichés");
    }

    @FXML
    private void handleAfficherTousEtudiants() {
        etudiantTable.setItems(etudiantsData);
        filiereFiltreCombo.setValue("Toutes les filières");
        anneeFiltreCombo.setValue("Toutes");
        totalEtudiantsFiltres.setText(String.valueOf(etudiantsData.size()));
        statusLabel.setText("📋 Tous les étudiants affichés");
    }

    // ============================================================
    // CRUD ÉTUDIANTS
    // ============================================================
    @FXML
    private void handleAjouterEtudiant() {
        Dialog<Etudiant> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un étudiant");
        dialog.setHeaderText("📚 Nouvel étudiant");

        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField filiereField = new TextField();
        filiereField.setPromptText("Filière");
        Spinner<Integer> anneeSpinner = new Spinner<>(1, 5, 1);
        TextField matriculeField = new TextField();
        matriculeField.setPromptText("Matricule");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Filière:"), 0, 3);
        grid.add(filiereField, 1, 3);
        grid.add(new Label("Année:"), 0, 4);
        grid.add(anneeSpinner, 1, 4);
        grid.add(new Label("Matricule:"), 0, 5);
        grid.add(matriculeField, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButton) {
                try {
                    Utilisateur utilisateur = new Utilisateur(
                            emailField.getText(), "etudiant123", nomField.getText(),
                            prenomField.getText(), "ETUDIANT"
                    );
                    utilisateurDAO.create(utilisateur);

                    Etudiant etudiant = new Etudiant(
                            utilisateur.getId(), nomField.getText(), prenomField.getText(),
                            emailField.getText(), LocalDate.now(), "",
                            filiereField.getText(), "Licence", anneeSpinner.getValue(),
                            matriculeField.getText(), "G1"
                    );
                    etudiantDAO.create(etudiant);
                    return etudiant;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible d'ajouter l'étudiant : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            chargerToutesLesDonnees();
            afficherAlerte("Succès", "✅ Étudiant ajouté avec succès !");
        });
    }

    @FXML
    private void handleModifierEtudiant() {
        Etudiant selected = etudiantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun étudiant sélectionné", "Veuillez sélectionner un étudiant à modifier.");
            return;
        }

        // Formulaire de modification (simplifié)
        Dialog<Etudiant> dialog = new Dialog<>();
        dialog.setTitle("Modifier un étudiant");
        dialog.setHeaderText("✏️ Modifier : " + selected.getNomComplet());

        ButtonType modifierButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField filiereField = new TextField(selected.getFiliere());
        Spinner<Integer> anneeSpinner = new Spinner<>(1, 5, selected.getAnneeEtude());
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("ACTIF", "INACTIF", "EXCLU", "DIPLOME");
        statutCombo.setValue(selected.getStatut());

        grid.add(new Label("Filière:"), 0, 0);
        grid.add(filiereField, 1, 0);
        grid.add(new Label("Année:"), 0, 1);
        grid.add(anneeSpinner, 1, 1);
        grid.add(new Label("Statut:"), 0, 2);
        grid.add(statutCombo, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifierButton) {
                try {
                    selected.setFiliere(filiereField.getText());
                    selected.setAnneeEtude(anneeSpinner.getValue());
                    selected.setStatut(statutCombo.getValue());
                    etudiantDAO.update(selected);
                    return selected;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible de modifier : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            chargerToutesLesDonnees();
            afficherAlerte("Succès", "✅ Étudiant modifié avec succès !");
        });
    }

    @FXML
    private void handleSupprimerEtudiant() {
        Etudiant selected = etudiantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun étudiant sélectionné", "Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'étudiant");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                etudiantDAO.delete(selected.getId());
                chargerToutesLesDonnees();
                afficherAlerte("Succès", "✅ Étudiant supprimé avec succès !");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    // ============================================================
    // GESTION DES ABSENCES
    // ============================================================
    @FXML
    private void handleGererAbsences() {
        Etudiant selected = etudiantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun étudiant sélectionné", "Veuillez sélectionner un étudiant.");
            return;
        }

        try {
            List<Absence> absences = absenceDAO.getAbsencesByEtudiant(selected.getId());

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Gestion des absences");
            dialog.setHeaderText("📋 Absences de " + selected.getNomComplet());

            ButtonType ajouterButton = new ButtonType("➕ Ajouter", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CLOSE);

            VBox content = new VBox(10);
            content.setPadding(new javafx.geometry.Insets(20));

            // Formulaire d'ajout
            GridPane addGrid = new GridPane();
            addGrid.setHgap(10);
            addGrid.setVgap(10);

            DatePicker datePicker = new DatePicker(LocalDate.now());
            TextField motifField = new TextField();
            motifField.setPromptText("Motif");
            CheckBox justifieeCheck = new CheckBox("Justifiée");

            addGrid.add(new Label("Date:"), 0, 0);
            addGrid.add(datePicker, 1, 0);
            addGrid.add(new Label("Motif:"), 0, 1);
            addGrid.add(motifField, 1, 1);
            addGrid.add(justifieeCheck, 1, 2);

            // Tableau des absences
            TableView<Absence> absenceTable = new TableView<>();
            absenceTable.setPrefHeight(200);

            TableColumn<Absence, String> colDate = new TableColumn<>("Date");
            colDate.setCellValueFactory(new PropertyValueFactory<>("dateFormatee"));
            colDate.setPrefWidth(100);

            TableColumn<Absence, String> colMotif = new TableColumn<>("Motif");
            colMotif.setCellValueFactory(new PropertyValueFactory<>("motif"));
            colMotif.setPrefWidth(200);

            TableColumn<Absence, String> colStatut = new TableColumn<>("Statut");
            colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));
            colStatut.setPrefWidth(120);

            TableColumn<Absence, Void> colAction = new TableColumn<>("Action");
            colAction.setPrefWidth(80);
            colAction.setCellFactory(param -> new TableCell<>() {
                private final Button deleteBtn = new Button("🗑️");
                {
                    deleteBtn.setOnAction(event -> {
                        Absence a = getTableView().getItems().get(getIndex());
                        try {
                            absenceDAO.delete(a.getId());
                            getTableView().getItems().remove(a);
                            chargerStatistiques();
                            detailsEtudiantAbsences.setText(absenceDAO.countByEtudiant(selected.getId()) + " absence(s)");
                            chargerToutesLesDonnees();
                        } catch (SQLException e) {
                            afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
                        }
                    });
                }
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : deleteBtn);
                }
            });

            absenceTable.getColumns().addAll(colDate, colMotif, colStatut, colAction);
            ObservableList<Absence> absencesData = FXCollections.observableArrayList(absences);
            absenceTable.setItems(absencesData);

            content.getChildren().addAll(addGrid, absenceTable);
            dialog.getDialogPane().setContent(content);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ajouterButton) {
                    try {
                        Absence nouvelle = new Absence(
                                selected.getId(),
                                datePicker.getValue(),
                                motifField.getText(),
                                justifieeCheck.isSelected()
                        );
                        absenceDAO.create(nouvelle);
                        absencesData.add(0, nouvelle);
                        chargerStatistiques();
                        chargerToutesLesDonnees();
                        detailsEtudiantAbsences.setText(absenceDAO.countByEtudiant(selected.getId()) + " absence(s)");
                        motifField.clear();
                        justifieeCheck.setSelected(false);
                        afficherAlerte("Succès", "✅ Absence ajoutée !");
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Impossible d'ajouter : " + e.getMessage());
                    }
                }
                return null;
            });

            dialog.showAndWait();

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger les absences : " + e.getMessage());
        }
    }

    // ============================================================
    // EMPLOI DU TEMPS
    // ============================================================
    @FXML
    private void handleAfficherEmploiDuTemps() {
        String filiere = edtFiliereCombo.getValue();
        String anneeStr = edtAnneeCombo.getValue();
        String semestreStr = edtSemestreCombo.getValue();

        if (filiere == null || filiere.equals("Toutes les filières")) {
            afficherAlerte("Information", "Veuillez sélectionner une filière spécifique.");
            return;
        }

        try {
            int annee = Integer.parseInt(anneeStr);
            int semestre = Integer.parseInt(semestreStr);

            List<EmploiDuTemps> edtList = emploiDuTempsDAO.getEmploiDuTemps(filiere, annee, semestre);
            emploiDuTempsData.clear();
            emploiDuTempsData.addAll(edtList);

            statusLabel.setText("📅 Emploi du temps chargé pour " + filiere + " - Année " + annee);

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible de charger l'emploi du temps : " + e.getMessage());
        }
    }

    // ============================================================
    // COMMUNICATION
    // ============================================================
    @FXML
    private void handleEnvoyerMessage() {
        String type = destinataireTypeCombo.getValue();
        String objet = messageObjetField.getText();
        String contenu = messageContentArea.getText();

        if (type == null || objet.isEmpty() || contenu.isEmpty()) {
            afficherAlerte("Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            Message message = new Message(
                    utilisateurConnecte.getId(),
                    "ADMIN",
                    type,
                    objet,
                    contenu
            );

            // Ajouter les filtres
            String filiere = destinataireFiliereCombo.getValue();
            if (filiere != null && !filiere.equals("Toutes")) {
                message.setFiliere(filiere);
            }
            String anneeStr = destinataireAnneeCombo.getValue();
            if (anneeStr != null && !anneeStr.equals("Toutes")) {
                message.setAnnee(Integer.parseInt(anneeStr));
            }

            messageDAO.create(message);
            messagesData.add(0, message);
            messagesEnvoyes++;
            messagesEnvoyesLabel.setText(String.valueOf(messagesEnvoyes));

            afficherAlerte("Succès", "✅ Message envoyé avec succès !");
            messageObjetField.clear();
            messageContentArea.clear();

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Impossible d'envoyer : " + e.getMessage());
        }
    }

    // ============================================================
    // EXPORT
    // ============================================================
    @FXML
    private void handleExporterNotesCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter les notes en CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("releve_notes_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Étudiant;Matière;Note;Coefficient;Points;Type;Date;Appréciation;Validée\n");
                for (Note n : notesTable.getItems()) {
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
        chargerToutesLesDonnees();
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
// FILTRES - ENSEIGNANTS
// ============================================================

    @FXML
    private void handleFiltrerEnseignants() {
        String filiere = enseignantFiliereFiltre.getValue();
        String matiere = enseignantMatiereFiltre.getValue();

        ObservableList<Enseignant> filtered = FXCollections.observableArrayList();
        for (Enseignant e : enseignantsData) {
            boolean matchFiliere = filiere == null ||
                    filiere.equals("Toutes les filières") ||
                    (e.getSpecialite() != null && e.getSpecialite().equals(filiere));

            boolean matchMatiere = true;
            if (matiere != null && !matiere.equals("Toutes les matières")) {
                try {
                    List<Matiere> matieres = enseignantDAO.getMatieresByEnseignant(e.getId());
                    matchMatiere = matieres.stream().anyMatch(m -> m.getNom().equals(matiere));
                } catch (SQLException ex) {
                    matchMatiere = false;
                }
            }

            if (matchFiliere && matchMatiere) {
                filtered.add(e);
            }
        }
        enseignantTable.setItems(filtered);
        totalEnseignantsFiltres.setText(String.valueOf(filtered.size()));
        enseignantStatusLabel.setText("📊 " + filtered.size() + " enseignants affichés");
        statusLabel.setText("👨‍🏫 " + filtered.size() + " enseignants affichés");
    }

    @FXML
    private void handleAfficherTousEnseignants() {
        enseignantTable.setItems(enseignantsData);
        enseignantFiliereFiltre.setValue("Toutes les filières");
        enseignantMatiereFiltre.setValue("Toutes les matières");
        totalEnseignantsFiltres.setText(String.valueOf(enseignantsData.size()));
        enseignantStatusLabel.setText("📊 Tous les enseignants affichés");
        statusLabel.setText("👨‍🏫 Tous les enseignants affichés");
    }

// ============================================================
// CRUD - ENSEIGNANTS
// ============================================================

    @FXML
    private void handleAjouterEnseignant() {
        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un enseignant");
        dialog.setHeaderText("👨‍🏫 Nouvel enseignant");

        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");
        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField specialiteField = new TextField();
        specialiteField.setPromptText("Spécialité");
        TextField gradeField = new TextField();
        gradeField.setPromptText("Grade");
        TextField telephoneField = new TextField();
        telephoneField.setPromptText("Téléphone");
        TextField bureauField = new TextField();
        bureauField.setPromptText("Bureau");
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("ACTIF", "CONGE", "DEPART");
        statutCombo.setValue("ACTIF");

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Spécialité:"), 0, 3);
        grid.add(specialiteField, 1, 3);
        grid.add(new Label("Grade:"), 0, 4);
        grid.add(gradeField, 1, 4);
        grid.add(new Label("Téléphone:"), 0, 5);
        grid.add(telephoneField, 1, 5);
        grid.add(new Label("Bureau:"), 0, 6);
        grid.add(bureauField, 1, 6);
        grid.add(new Label("Statut:"), 0, 7);
        grid.add(statutCombo, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ajouterButton) {
                try {
                    Utilisateur utilisateur = new Utilisateur(
                            emailField.getText(),
                            "enseignant123",
                            nomField.getText(),
                            prenomField.getText(),
                            "ENSEIGNANT"
                    );
                    utilisateurDAO.create(utilisateur);

                    Enseignant enseignant = new Enseignant(
                            utilisateur.getId(),
                            nomField.getText(),
                            prenomField.getText(),
                            emailField.getText(),
                            specialiteField.getText(),
                            LocalDate.now(),
                            gradeField.getText(),
                            telephoneField.getText(),
                            bureauField.getText(),
                            statutCombo.getValue()
                    );
                    enseignantDAO.create(enseignant);
                    return enseignant;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible d'ajouter l'enseignant : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            chargerToutesLesDonnees();
            afficherAlerte("Succès", "✅ Enseignant ajouté avec succès !");
        });
    }


    @FXML
    private void handleModifierEnseignant() {
        Enseignant selected = enseignantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun enseignant sélectionné", "Veuillez sélectionner un enseignant à modifier.");
            return;
        }

        Dialog<Enseignant> dialog = new Dialog<>();
        dialog.setTitle("Modifier un enseignant");
        dialog.setHeaderText("✏️ Modifier : " + selected.getNomComplet());

        ButtonType modifierButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField specialiteField = new TextField(selected.getSpecialite());
        TextField gradeField = new TextField(selected.getGrade());
        TextField telephoneField = new TextField(selected.getTelephone());
        TextField bureauField = new TextField(selected.getBureau());
        ComboBox<String> statutCombo = new ComboBox<>();
        statutCombo.getItems().addAll("ACTIF", "CONGE", "DEPART");
        statutCombo.setValue(selected.getStatut());

        grid.add(new Label("Spécialité:"), 0, 0);
        grid.add(specialiteField, 1, 0);
        grid.add(new Label("Grade:"), 0, 1);
        grid.add(gradeField, 1, 1);
        grid.add(new Label("Téléphone:"), 0, 2);
        grid.add(telephoneField, 1, 2);
        grid.add(new Label("Bureau:"), 0, 3);
        grid.add(bureauField, 1, 3);
        grid.add(new Label("Statut:"), 0, 4);
        grid.add(statutCombo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == modifierButton) {
                try {
                    selected.setSpecialite(specialiteField.getText());
                    selected.setGrade(gradeField.getText());
                    selected.setTelephone(telephoneField.getText());
                    selected.setBureau(bureauField.getText());
                    selected.setStatut(statutCombo.getValue());
                    enseignantDAO.update(selected);
                    return selected;
                } catch (SQLException e) {
                    afficherAlerte("Erreur", "Impossible de modifier l'enseignant : " + e.getMessage());
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(e -> {
            chargerToutesLesDonnees();
            afficherAlerte("Succès", "✅ Enseignant modifié avec succès !");
        });
    }

    @FXML
    private void handleSupprimerEnseignant() {
        Enseignant selected = enseignantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun enseignant sélectionné", "Veuillez sélectionner un enseignant à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'enseignant");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer " + selected.getNomComplet() + " ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                enseignantDAO.delete(selected.getId());
                chargerToutesLesDonnees();
                afficherAlerte("Succès", "✅ Enseignant supprimé avec succès !");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer l'enseignant : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleAssignerMatiere() {
        Enseignant selected = enseignantTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun enseignant sélectionné", "Veuillez sélectionner un enseignant.");
            return;
        }

        // TODO: Implémenter l'assignation de matière
        afficherAlerte("Info", "➡️ Fonctionnalité d'assignation de matière à implémenter");
    }

    @FXML
    private void handleAjouterEmploiDuTemps() {
        Dialog<EmploiDuTemps> dialog = new Dialog<>();
        dialog.setTitle("Ajouter un cours");
        dialog.setHeaderText("📅 Nouveau cours dans l'emploi du temps");

        ButtonType ajouterButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ajouterButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        try {
            // Matières
            ComboBox<Matiere> matiereCombo = new ComboBox<>();
            matiereCombo.getItems().addAll(matiereDAO.readAll());
            matiereCombo.setPromptText("Sélectionner une matière");

            // Enseignants
            ComboBox<Enseignant> enseignantCombo = new ComboBox<>();
            enseignantCombo.getItems().addAll(enseignantDAO.readAll());
            enseignantCombo.setPromptText("Sélectionner un enseignant");

            // Filière
            TextField filiereField = new TextField("Génie Informatique");
            filiereField.setPromptText("Filière");

            // Année
            Spinner<Integer> anneeSpinner = new Spinner<>(1, 5, 1);

            // Jour
            ComboBox<String> jourCombo = new ComboBox<>();
            jourCombo.getItems().addAll("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI");
            jourCombo.setValue("LUNDI");

            // Heure
            Spinner<Integer> heureDebut = new Spinner<>(8, 20, 8);
            Spinner<Integer> minuteDebut = new Spinner<>(0, 59, 0);
            Spinner<Integer> heureFin = new Spinner<>(8, 20, 10);
            Spinner<Integer> minuteFin = new Spinner<>(0, 59, 0);

            // Salle
            TextField salleField = new TextField();
            salleField.setPromptText("Salle");

            // Type
            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("CM", "TD", "TP");
            typeCombo.setValue("CM");

            // Semestre
            Spinner<Integer> semestreSpinner = new Spinner<>(1, 2, 1);

            grid.add(new Label("Matière :"), 0, 0);
            grid.add(matiereCombo, 1, 0);
            grid.add(new Label("Enseignant :"), 0, 1);
            grid.add(enseignantCombo, 1, 1);
            grid.add(new Label("Filière :"), 0, 2);
            grid.add(filiereField, 1, 2);
            grid.add(new Label("Année :"), 0, 3);
            grid.add(anneeSpinner, 1, 3);
            grid.add(new Label("Jour :"), 0, 4);
            grid.add(jourCombo, 1, 4);
            grid.add(new Label("Heure début :"), 0, 5);
            HBox heureDebutBox = new HBox(5, heureDebut, new Label("h"), minuteDebut);
            grid.add(heureDebutBox, 1, 5);
            grid.add(new Label("Heure fin :"), 0, 6);
            HBox heureFinBox = new HBox(5, heureFin, new Label("h"), minuteFin);
            grid.add(heureFinBox, 1, 6);
            grid.add(new Label("Salle :"), 0, 7);
            grid.add(salleField, 1, 7);
            grid.add(new Label("Type :"), 0, 8);
            grid.add(typeCombo, 1, 8);
            grid.add(new Label("Semestre :"), 0, 9);
            grid.add(semestreSpinner, 1, 9);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ajouterButton) {
                    Matiere matiere = matiereCombo.getValue();
                    Enseignant enseignant = enseignantCombo.getValue();
                    if (matiere == null || enseignant == null) {
                        afficherAlerte("Erreur", "Veuillez sélectionner une matière et un enseignant.");
                        return null;
                    }

                    try {
                        LocalTime debut = LocalTime.of(heureDebut.getValue(), minuteDebut.getValue());
                        LocalTime fin = LocalTime.of(heureFin.getValue(), minuteFin.getValue());

                        EmploiDuTemps edt = new EmploiDuTemps(
                                matiere.getId(),
                                enseignant.getId(),
                                filiereField.getText(),
                                anneeSpinner.getValue(),
                                jourCombo.getValue(),
                                debut,
                                fin,
                                salleField.getText(),
                                typeCombo.getValue(),
                                semestreSpinner.getValue()
                        );
                        emploiDuTempsDAO.create(edt);
                        return edt;
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Impossible d'ajouter le cours : " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(edt -> {
                handleAfficherEmploiDuTemps();
                afficherAlerte("Succès", "✅ Cours ajouté avec succès !");
            });

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierEmploiDuTemps() {
        EmploiDuTemps selected = emploiDuTempsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun cours sélectionné", "Veuillez sélectionner un cours à modifier.");
            return;
        }

        Dialog<EmploiDuTemps> dialog = new Dialog<>();
        dialog.setTitle("Modifier un cours");
        dialog.setHeaderText("✏️ Modifier le cours du " + selected.getJour() + " (" + selected.getHeureFormatee() + ")");

        ButtonType modifierButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        try {
            // === CHAMPS MODIFIABLES ===

            // Matière (ComboBox avec toutes les matières)
            ComboBox<Matiere> matiereCombo = new ComboBox<>();
            List<Matiere> matieres = matiereDAO.readAll();
            matiereCombo.getItems().addAll(matieres);
            // Sélectionner la matière actuelle
            for (Matiere m : matieres) {
                if (m.getId() == selected.getMatiereId()) {
                    matiereCombo.setValue(m);
                    break;
                }
            }
            matiereCombo.setPromptText("Sélectionner une matière");

            // Enseignant (ComboBox avec tous les enseignants)
            ComboBox<Enseignant> enseignantCombo = new ComboBox<>();
            List<Enseignant> enseignants = enseignantDAO.readAll();
            enseignantCombo.getItems().addAll(enseignants);
            // Sélectionner l'enseignant actuel
            for (Enseignant e : enseignants) {
                if (e.getId() == selected.getEnseignantId()) {
                    enseignantCombo.setValue(e);
                    break;
                }
            }
            enseignantCombo.setPromptText("Sélectionner un enseignant");

            // Filière
            TextField filiereField = new TextField(selected.getFiliere());
            filiereField.setPromptText("Filière");

            // Année
            Spinner<Integer> anneeSpinner = new Spinner<>(1, 5, selected.getAnnee());

            // Jour
            ComboBox<String> jourCombo = new ComboBox<>();
            jourCombo.getItems().addAll("LUNDI", "MARDI", "MERCREDI", "JEUDI", "VENDREDI", "SAMEDI");
            jourCombo.setValue(selected.getJour());

            // Heure début
            Spinner<Integer> heureDebut = new Spinner<>(8, 20, selected.getHeureDebut().getHour());
            Spinner<Integer> minuteDebut = new Spinner<>(0, 59, selected.getHeureDebut().getMinute());

            // Heure fin
            Spinner<Integer> heureFin = new Spinner<>(8, 20, selected.getHeureFin().getHour());
            Spinner<Integer> minuteFin = new Spinner<>(0, 59, selected.getHeureFin().getMinute());

            // Salle
            TextField salleField = new TextField(selected.getSalle() != null ? selected.getSalle() : "");
            salleField.setPromptText("Salle");

            // Type
            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("CM", "TD", "TP");
            typeCombo.setValue(selected.getTypeCours());

            // Semestre
            Spinner<Integer> semestreSpinner = new Spinner<>(1, 2, selected.getSemestre());

            // === LAYOUT ===
            int row = 0;
            grid.add(new Label("Matière :"), 0, row);
            grid.add(matiereCombo, 1, row++);

            grid.add(new Label("Enseignant :"), 0, row);
            grid.add(enseignantCombo, 1, row++);

            grid.add(new Label("Filière :"), 0, row);
            grid.add(filiereField, 1, row++);

            grid.add(new Label("Année :"), 0, row);
            grid.add(anneeSpinner, 1, row++);

            grid.add(new Label("Jour :"), 0, row);
            grid.add(jourCombo, 1, row++);

            grid.add(new Label("Heure début :"), 0, row);
            HBox heureDebutBox = new HBox(5, heureDebut, new Label("h"), minuteDebut);
            grid.add(heureDebutBox, 1, row++);

            grid.add(new Label("Heure fin :"), 0, row);
            HBox heureFinBox = new HBox(5, heureFin, new Label("h"), minuteFin);
            grid.add(heureFinBox, 1, row++);

            grid.add(new Label("Salle :"), 0, row);
            grid.add(salleField, 1, row++);

            grid.add(new Label("Type :"), 0, row);
            grid.add(typeCombo, 1, row++);

            grid.add(new Label("Semestre :"), 0, row);
            grid.add(semestreSpinner, 1, row++);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == modifierButton) {
                    Matiere matiere = matiereCombo.getValue();
                    Enseignant enseignant = enseignantCombo.getValue();

                    if (matiere == null || enseignant == null) {
                        afficherAlerte("Erreur", "Veuillez sélectionner une matière et un enseignant.");
                        return null;
                    }

                    try {
                        // Créer une nouvelle entrée avec les modifications
                        EmploiDuTemps newEdt = new EmploiDuTemps(
                                matiere.getId(),
                                enseignant.getId(),
                                filiereField.getText(),
                                anneeSpinner.getValue(),
                                jourCombo.getValue(),
                                LocalTime.of(heureDebut.getValue(), minuteDebut.getValue()),
                                LocalTime.of(heureFin.getValue(), minuteFin.getValue()),
                                salleField.getText().isEmpty() ? null : salleField.getText(),
                                typeCombo.getValue(),
                                semestreSpinner.getValue()
                        );

                        // Supprimer l'ancienne entrée
                        emploiDuTempsDAO.delete(selected.getId());
                        // Créer la nouvelle
                        emploiDuTempsDAO.create(newEdt);

                        return newEdt;
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Impossible de modifier : " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(edt -> {
                handleAfficherEmploiDuTemps();
                afficherAlerte("Succès", "✅ Cours modifié avec succès !");
            });

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimerEmploiDuTemps() {
        EmploiDuTemps selected = emploiDuTempsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucun cours sélectionné", "Veuillez sélectionner un cours à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le cours");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer le cours du " + selected.getJour() + " ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                emploiDuTempsDAO.delete(selected.getId());
                handleAfficherEmploiDuTemps();
                afficherAlerte("Succès", "✅ Cours supprimé avec succès !");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleExporterEmploiDuTemps() {
        if (emploiDuTempsData.isEmpty()) {
            afficherAlerte("Information", "Aucune donnée à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'emploi du temps");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        fileChooser.setInitialFileName("emploi_du_temps_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Jour;Horaire;Matière;Enseignant;Salle;Type\n");
                for (EmploiDuTemps e : emploiDuTempsData) {
                    writer.write(String.format("%s;%s;%s;%s;%s;%s\n",
                            e.getJour(),
                            e.getHeureFormatee(),
                            e.getNomMatiere(),
                            e.getNomEnseignantComplet(),
                            e.getSalle() != null ? e.getSalle() : "",
                            e.getTypeCours()
                    ));
                }
                afficherAlerte("Succès", "✅ Export CSV réussi : " + file.getName());
            } catch (IOException e) {
                afficherAlerte("Erreur", "Erreur lors de l'export : " + e.getMessage());
            }
        }
    }

    // ============================================================
// CRUD - NOTES (COMPLÉMENT)
// ============================================================

    @FXML
    private void handleAjouterNote() {
        // Sélectionner un étudiant
        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une note");
        dialog.setHeaderText("📝 Ajouter une note");

        ButtonType saveButton = new ButtonType("Ajouter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        try {
            // Étudiant
            ComboBox<Etudiant> etudiantCombo = new ComboBox<>();
            etudiantCombo.getItems().addAll(etudiantDAO.readAll());
            etudiantCombo.setPromptText("Sélectionner un étudiant");
            etudiantCombo.setPrefWidth(200);

            // Matière
            ComboBox<Matiere> matiereCombo = new ComboBox<>();
            matiereCombo.getItems().addAll(matiereDAO.readAll());
            matiereCombo.setPromptText("Sélectionner une matière");
            matiereCombo.setPrefWidth(200);

            // Enseignant
            ComboBox<Enseignant> enseignantCombo = new ComboBox<>();
            enseignantCombo.getItems().addAll(enseignantDAO.readAll());
            enseignantCombo.setPromptText("Sélectionner un enseignant");
            enseignantCombo.setPrefWidth(200);

            Spinner<Double> noteSpinner = new Spinner<>(0, 20, 10, 0.5);
            Spinner<Double> coeffSpinner = new Spinner<>(0.5, 5, 1, 0.5);
            DatePicker datePicker = new DatePicker(LocalDate.now());

            ComboBox<String> typeCombo = new ComboBox<>();
            typeCombo.getItems().addAll("Devoir", "Examen", "TP", "Projet", "Contrôle Continu");
            typeCombo.setValue("Devoir");

            Spinner<Integer> semestreSpinner = new Spinner<>(1, 6, 1);

            TextArea appreciationArea = new TextArea();
            appreciationArea.setPrefHeight(60);
            appreciationArea.setPromptText("Appréciation...");

            CheckBox valideeCheck = new CheckBox("Validée");

            int row = 0;
            grid.add(new Label("Étudiant :"), 0, row);
            grid.add(etudiantCombo, 1, row++);

            grid.add(new Label("Matière :"), 0, row);
            grid.add(matiereCombo, 1, row++);

            grid.add(new Label("Enseignant :"), 0, row);
            grid.add(enseignantCombo, 1, row++);

            grid.add(new Label("Note /20 :"), 0, row);
            grid.add(noteSpinner, 1, row++);

            grid.add(new Label("Coefficient :"), 0, row);
            grid.add(coeffSpinner, 1, row++);

            grid.add(new Label("Date :"), 0, row);
            grid.add(datePicker, 1, row++);

            grid.add(new Label("Type :"), 0, row);
            grid.add(typeCombo, 1, row++);

            grid.add(new Label("Semestre :"), 0, row);
            grid.add(semestreSpinner, 1, row++);

            grid.add(new Label("Appréciation :"), 0, row);
            grid.add(appreciationArea, 1, row++);

            grid.add(valideeCheck, 1, row++);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButton) {
                    Etudiant etudiant = etudiantCombo.getValue();
                    Matiere matiere = matiereCombo.getValue();
                    Enseignant enseignant = enseignantCombo.getValue();

                    if (etudiant == null || matiere == null || enseignant == null) {
                        afficherAlerte("Erreur", "Veuillez sélectionner un étudiant, une matière et un enseignant.");
                        return null;
                    }

                    try {
                        Note note = new Note(
                                etudiant.getId(),
                                matiere.getId(),
                                enseignant.getId(),
                                noteSpinner.getValue(),
                                datePicker.getValue(),
                                coeffSpinner.getValue(),
                                appreciationArea.getText(),
                                typeCombo.getValue(),
                                semestreSpinner.getValue()
                        );
                        note.setValidee(valideeCheck.isSelected());
                        noteDAO.create(note);
                        return note;
                    } catch (SQLException e) {
                        afficherAlerte("Erreur", "Impossible d'ajouter la note : " + e.getMessage());
                        return null;
                    }
                }
                return null;
            });

            dialog.showAndWait().ifPresent(note -> {
                chargerToutesLesDonnees();
                if (notesStatusLabel != null) {
                    notesStatusLabel.setText("✅ Note ajoutée avec succès !");
                }
                afficherAlerte("Succès", "✅ Note ajoutée avec succès !");
            });

        } catch (SQLException e) {
            afficherAlerte("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
        }
    }

    @FXML
    private void handleModifierNote() {
        Note selected = notesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucune note sélectionnée", "Veuillez sélectionner une note à modifier.");
            return;
        }

        Dialog<Note> dialog = new Dialog<>();
        dialog.setTitle("Modifier une note");
        dialog.setHeaderText("✏️ Modifier la note");

        ButtonType modifierButton = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(modifierButton, ButtonType.CANCEL);

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
            if (dialogButton == modifierButton) {
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
            chargerToutesLesDonnees();
            afficherAlerte("Succès", "✅ Note modifiée avec succès !");
        });
    }

    @FXML
    private void handleSupprimerNote() {
        Note selected = notesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            afficherAlerte("Aucune note sélectionnée", "Veuillez sélectionner une note à supprimer.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la note");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette note ?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                noteDAO.delete(selected.getId());
                chargerToutesLesDonnees();
                afficherAlerte("Succès", "✅ Note supprimée avec succès !");
            } catch (SQLException e) {
                afficherAlerte("Erreur", "Impossible de supprimer : " + e.getMessage());
            }
        }
    }
}