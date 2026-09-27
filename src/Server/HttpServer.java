package Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class HttpServer {
	private int port;
	private cameracontroller controller;
	private ExecutorService pool = Executors.newFixedThreadPool(10);
    public HttpServer(int port, cameracontroller controller) {
    	this.port=port;
    	this.controller=controller;
		// TODO Auto-generated constructor stub
	}

	public void start() {
        // ServerSocket écoute sur le port (comme un serveur en attente d'appels)
		System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Serveur Vidéosurveillance démarré   ║");
        System.out.println("║  Écoute sur le port : " + port + "   ║");
        System.out.println("╚══════════════════════════════════════╝");
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) { // boucle infinie : toujours prêt à recevoir
                Socket clientSocket = serverSocket.accept(); // BLOQUANT : attend une connexion
                // Dès qu'un client se connecte, on crée un thread pour lui
                pool.submit(new ClientHandler(clientSocket, controller));
                // et on repart immédiatement attendre le prochain client
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
