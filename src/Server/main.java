package Server;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws IOException {

        // 1. Crée le repository et initialise les fichiers /opt/cams/
        cameraRepository repo = new cameraRepository();
        repo.init();

        // 2. Crée le contrôleur en lui donnant le repo
        cameracontroller controller = new cameracontroller(repo);

        // 3. Crée et lance le serveur HTTP
        HttpServer server = new HttpServer(7777, controller);
        server.start(); // bloquant : le programme reste ici
    }
}