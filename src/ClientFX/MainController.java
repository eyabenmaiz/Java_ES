package ClientFX;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.util.List;

public class MainController {

    @FXML private TableView<CameraModel>           tableView;
    @FXML private TableColumn<CameraModel, Number> colId;
    @FXML private TableColumn<CameraModel, String> colStatus;
    @FXML private TableColumn<CameraModel, String> colLastUpdate;
    @FXML private TableColumn<CameraModel, Void>   colAction;
    @FXML private Label  lblStatus;
    @FXML private Button btnRefresh;
    @FXML private Button btnStartAll;
    @FXML private Button btnStopAll;

    private final ObservableList<CameraModel> cameraList =
            FXCollections.observableArrayList();

    private final ApiClient apiClient = new ApiClient();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(
            cellData -> cellData.getValue().idProperty());
        colStatus.setCellValueFactory(
            cellData -> cellData.getValue().statusProperty());
        colLastUpdate.setCellValueFactory(
            cellData -> cellData.getValue().lastUpdateProperty());

        // Empêche Rafraîchir de se déclencher avec Enter ou focus
        btnRefresh.setDefaultButton(false);
        btnRefresh.setFocusTraversable(false);
        btnStartAll.setFocusTraversable(false);
        btnStopAll.setFocusTraversable(false);

        setupActionColumn();
        tableView.setItems(cameraList);
        loadCameras();
    }

    private void setupActionColumn() {
        colAction.setCellFactory(col -> new ActionCell());
    }

    // Classe nommée au lieu d'anonyme → pas de problème $1
    class ActionCell extends TableCell<CameraModel, Void> {

        private final Button btnStart = new Button("Start");
        private final Button btnStop  = new Button("Stop");
        private final HBox   box      = new HBox(8, btnStart, btnStop);

        public ActionCell() {
            btnStart.setStyle(
                "-fx-background-color: #4CAF50; -fx-text-fill: white;");
            btnStop.setStyle(
                "-fx-background-color: #f44336; -fx-text-fill: white;");
            btnStart.setFocusTraversable(false);
            btnStop.setFocusTraversable(false);

            btnStart.setOnAction(e -> {
                e.consume();
                CameraModel cam = getTableView().getItems().get(getIndex());
                // Appelle la méthode du contrôleur parent
                MainController.this.executeAction("START", cam.getId());
            });

            btnStop.setOnAction(e -> {
                e.consume();
                CameraModel cam = getTableView().getItems().get(getIndex());
                MainController.this.executeAction("STOP", cam.getId());
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : box);
        }
    }

    // Action individuelle Start/Stop
    private void executeAction(String action, int cameraId) {
        Platform.runLater(() -> {
            btnRefresh.setDisable(true);
            lblStatus.setText("En cours...");
            lblStatus.setStyle("-fx-text-fill: blue;");
        });

        new Thread(() -> {
            try {
                // Envoie la commande - AFFICHE l'erreur si ça échoue
                if ("START".equals(action)) {
                    apiClient.startCamera(cameraId);
                } else {
                    apiClient.stopCamera(cameraId);
                }
                System.out.println("[OK] Action " + action + " envoyée pour cam" + cameraId);
            } catch (Exception e) {
                // Affiche l'erreur dans la console ET dans l'interface
                System.out.println("[ERREUR ACTION] " + e.getMessage());
            }

            try { Thread.sleep(400); } catch (InterruptedException ignored) {}

            try {
                List<CameraModel> cameras = apiClient.getAllCameras();
                Platform.runLater(() -> {
                    cameraList.setAll(cameras);
                    lblStatus.setText("");
                    btnRefresh.setDisable(false);
                });
            } catch (Exception e) {
                System.out.println("[ERREUR REFRESH] " + e.getMessage());
                Platform.runLater(() -> {
                    showError(e.getMessage());
                    btnRefresh.setDisable(false);
                });
            }
        }).start();
    }
    @FXML
    private void onRefresh() {
        loadCameras();
    }

    @FXML
    private void onStartAll() {
        new Thread(() -> {
            try { apiClient.commandAll("START"); }
            catch (Exception ignored) {}
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            try {
                List<CameraModel> cameras = apiClient.getAllCameras();
                Platform.runLater(() -> {
                    cameraList.setAll(cameras);
                    lblStatus.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    @FXML
    private void onStopAll() {
        new Thread(() -> {
            try { apiClient.commandAll("STOP"); }
            catch (Exception ignored) {}
            try { Thread.sleep(400); } catch (InterruptedException ignored) {}
            try {
                List<CameraModel> cameras = apiClient.getAllCameras();
                Platform.runLater(() -> {
                    cameraList.setAll(cameras);
                    lblStatus.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    private void loadCameras() {
        new Thread(() -> {
            try {
                List<CameraModel> cameras = apiClient.getAllCameras();
                Platform.runLater(() -> {
                    cameraList.setAll(cameras);
                    lblStatus.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError(e.getMessage()));
            }
        }).start();
    }

    private void showError(String msg) {
        lblStatus.setText("Erreur : " + msg);
        lblStatus.setStyle("-fx-text-fill: red;");
    }
}