package unischool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger le fichier FXML avec le bon chemin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            // Charger le CSS
            String css = getClass().getResource("/css/style.css").toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }

            primaryStage.setTitle("UniSchool - Connexion");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

            System.out.println("✅ Application lancée avec succès !");

        } catch (Exception e) {
            System.err.println("❌ Erreur au lancement : " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}