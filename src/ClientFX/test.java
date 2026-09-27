package ClientFX;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class test extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX fonctionne !");
        Scene scene = new Scene(new StackPane(label), 400, 300);
        stage.setScene(scene);
        stage.setTitle("Test JavaFX");
        stage.show();
    }

    public static void main (String[] args) {
        launch(args);
    }
}
