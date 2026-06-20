package unischool.controllers;

import unischool.dao.UtilisateurDAO;
import unischool.models.Utilisateur;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheck;
    @FXML private Button loginButton;
    @FXML private Label messageLabel;
    @FXML private ProgressBar loadingProgress;

    private UtilisateurDAO utilisateurDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        utilisateurDAO = new UtilisateurDAO();

        emailField.setTooltip(new Tooltip("Entrez votre adresse email"));
        passwordField.setTooltip(new Tooltip("Entrez votre mot de passe"));

        emailField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> handleLogin());

        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showMessage("⚠️ Veuillez remplir tous les champs !", Alert.AlertType.WARNING);
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            showMessage("⚠️ Veuillez entrer une adresse email valide !", Alert.AlertType.WARNING);
            return;
        }

        loadingProgress.setVisible(true);
        loadingProgress.setProgress(0.5);
        loginButton.setDisable(true);

        new Thread(() -> {
            try {
                Utilisateur utilisateur = utilisateurDAO.authenticate(email, password);

                Platform.runLater(() -> {
                    loadingProgress.setProgress(1.0);

                    if (utilisateur != null) {
                        try {
                            utilisateurDAO.updateDerniereConnexion(utilisateur.getId());
                        } catch (SQLException e) {
                            System.err.println("❌ Erreur mise à jour dernière connexion : " + e.getMessage());
                        }

                        redirectToDashboard(utilisateur);

                    } else {
                        showMessage("❌ Email ou mot de passe incorrect !", Alert.AlertType.ERROR);
                        loadingProgress.setVisible(false);
                        loginButton.setDisable(false);
                    }
                });

            } catch (SQLException e) {
                Platform.runLater(() -> {
                    showMessage("❌ Erreur de connexion à la base de données : " + e.getMessage(), Alert.AlertType.ERROR);
                    loadingProgress.setVisible(false);
                    loginButton.setDisable(false);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void redirectToDashboard(Utilisateur utilisateur) {
        try {
            Stage loginStage = (Stage) loginButton.getScene().getWindow();

            String fxmlFile;
            String title;

            switch (utilisateur.getRole()) {
                case "ADMIN":
                    fxmlFile = "/views/main_admin.fxml";
                    title = "UniSchool - Administration";
                    break;
                case "ENSEIGNANT":
                    fxmlFile = "/views/main_enseignant.fxml";
                    title = "UniSchool - Enseignant";
                    break;
                case "ETUDIANT":
                    fxmlFile = "/views/main_etudiant.fxml";
                    title = "UniSchool - Étudiant";
                    break;
                default:
                    showMessage("❌ Rôle inconnu !", Alert.AlertType.ERROR);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Passer l'utilisateur au contrôleur
            if (utilisateur.getRole().equals("ADMIN")) {
                AdminController controller = loader.getController();
                controller.setUtilisateur(utilisateur);
            } else if (utilisateur.getRole().equals("ENSEIGNANT")) {
                EnseignantController controller = loader.getController();
                controller.setUtilisateur(utilisateur);
            } else if (utilisateur.getRole().equals("ETUDIANT")) {
                EtudiantController controller = loader.getController();
                controller.setUtilisateur(utilisateur);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/style.css").toExternalForm()
            );

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle(title);
            dashboardStage.setScene(scene);
            dashboardStage.setMaximized(true);
            dashboardStage.initStyle(StageStyle.DECORATED);

            loginStage.close();
            dashboardStage.show();

        } catch (Exception e) {
            showMessage("❌ Erreur lors du chargement de l'interface : " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void showMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("UniSchool");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}