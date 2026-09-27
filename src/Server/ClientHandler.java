package Server;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket           socket;
    private final cameracontroller controller;

    public ClientHandler(Socket socket, cameracontroller controller) {
        this.socket     = socket;
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            socket.setSoTimeout(5000);
            HttpRequest req = HttpRequest.parse(socket.getInputStream());
            if (req == null) {
                System.out.println("[HANDLER] Requête nulle, ignorée.");
                socket.close();
                return;
            }
            System.out.println("[HANDLER] Méthode : " + req.getMethod());
            System.out.println("[HANDLER] Chemin  : " + req.getPath());
            System.out.println("[HANDLER] Body    : " + req.getBody());

            HttpResponse res = new HttpResponse(socket.getOutputStream());

            if (req.getPath().startsWith("/api/cameras")) {
                controller.handle(req, res);
            } else {
                res.sendError(HttpResponse.NOT_FOUND, "Chemin inconnu.");
            }

            socket.getOutputStream().flush();

        } catch (java.net.SocketTimeoutException e) {
            System.out.println("[HANDLER] Timeout - connexion vide.");
        } catch (IOException e) {
            System.err.println("[HANDLER] Erreur : " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}