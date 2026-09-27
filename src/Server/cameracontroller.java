package Server;

import java.io.IOException;

public class cameracontroller {

    private final cameraRepository repo;

    public cameracontroller(cameraRepository repo) {
        this.repo = repo;
    }

    // Point d'entrée : toutes les requêtes passent ici
    public void handle(HttpRequest req, HttpResponse res) throws IOException {

        String method = req.getMethod(); // "GET" ou "POST"
        String path   = req.getPath();   // ex: "/api/cameras/1/record"

        // On teste chaque route possible :

        // GET /api/cameras → liste toutes les caméras
        if ("GET".equals(method) && "/api/cameras".equals(path)) {
            handleGetAll(res);
            return;
        }

        // POST /api/cameras/all/record → commande globale
        if ("POST".equals(method) && "/api/cameras/all/record".equals(path)) {
            handleRecordAll(req, res);
            return;
        }

        // GET /api/cameras/0  (ou /1, /2)
        // .matches() utilise une expression régulière : \d+ = un ou plusieurs chiffres
        if ("GET".equals(method) && path.matches("/api/cameras/\\d+")) {
            int id = extractId(path); // extrait le "0" depuis "/api/cameras/0"
            handleGetOne(id, res);
            return;
        }

        // POST /api/cameras/0/record
        if ("POST".equals(method) && path.matches("/api/cameras/\\d+/record")) {
            int id = extractId(path);
            handleRecord(id, req, res);
            return;
        }

        // Aucune route ne correspond
        res.sendError(HttpResponse.NOT_FOUND, "Route inconnue : " + path);
    }
    private void handleGetAll(HttpResponse res) throws IOException {
        System.out.println("[DEBUG] handleGetAll appelé");
        try {
            camera[] cams = repo.getAllCameras();
            System.out.println("[DEBUG] " + cams.length + " caméras lues");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < cams.length; i++) {
                sb.append(cams[i].toJson());
                if (i < cams.length - 1) sb.append(",");
            }
            sb.append("]");
            System.out.println("[DEBUG] JSON : " + sb.toString());
            res.sendJson(HttpResponse.OK, sb.toString());
            System.out.println("[DEBUG] Réponse envoyée");
        } catch (Exception e) {
            System.err.println("[DEBUG] ERREUR : " + e.getMessage());
            e.printStackTrace();
            res.sendError(HttpResponse.INTERNAL_ERROR, e.getMessage());
        }
    }

 private void handleRecordAll(HttpRequest req, HttpResponse res) {
		// TODO Auto-generated method stub
		
	}

 private void handleGetOne(int id, HttpResponse res) {
		// TODO Auto-generated method stub
		
	}

 private void handleRecord(int id, HttpRequest req, HttpResponse res) {
		// TODO Auto-generated method stub
		
	}

 // Extrait l'id depuis le chemin
    // "/api/cameras/2/record".split("/") = ["", "api", "cameras", "2", "record"]
    //                                          0     1       2       3      4
    // donc segments[3] = "2"
    private int extractId(String path) {
        String[] segments = path.split("/");
        return Integer.parseInt(segments[3]);
    }

    // Convertit "START" → "RECORDING" et "STOP" → "STOPPED"
    private String resolveStatus(String action) {
        if ("START".equalsIgnoreCase(action)) return cameraRepository.status_RECORDING;
        if ("STOP".equalsIgnoreCase(action))  return cameraRepository.status_STOPPED;
        return null; // action inconnue
    }
}