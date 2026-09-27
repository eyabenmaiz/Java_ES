package ClientFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

// Application = classe de base de tout programme JavaFX
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charge le fichier FXML qui décrit l'interface
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("main-view.fxml")
        );

        // Crée la scène avec une taille initiale
        Scene scene = new Scene(loader.load(), 700, 450);

        primaryStage.setTitle("Vidéosurveillance - Client JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show(); // affiche la fenêtre
    }

    public static void main(String[] args) {
    	System.setProperty("http.keepAlive", "false");
        launch(args); // démarre JavaFX, qui appelle start() automatiquement
    }
}