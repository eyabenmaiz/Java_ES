Système Distribué de Vidéosurveillance — API REST Java \& Client JavaFX



Mini-projet réalisé dans le cadre du cours Programmation des Entrées Sorties Java — ENET'Com, 2ème année GII, année universitaire 2025-2026.



📋 Description



Système client-serveur permettant la gestion à distance de caméras réseau simulées :



Serveur : API REST HTTP/JSON développée en Java pur (sans framework), basée sur ServerSocket.

Client : Application graphique JavaFX consommant l'API REST via HttpURLConnection.



Les états des caméras (RECORDING / STOPPED) sont simulés par des fichiers système, manipulés via les mécanismes d'Entrées/Sorties Java (java.io / java.nio).



🏗️ Architecture

src/

├── Server/          → Serveur REST (HTTP, gestion des caméras, fichiers)

│   ├── main.java

│   ├── HttpServer.java

│   ├── HttpRequest.java

│   ├── HttpResponse.java

│   ├── ClientHandler.java

│   ├── camera.java

│   ├── cameracontroller.java

│   └── cameraRepository.java

│

└── ClientFX/        → Client graphique JavaFX

&#x20;   ├── MainApp.java

&#x20;   ├── MainController.java

&#x20;   ├── CameraModel.java

&#x20;   ├── ApiClient.java

&#x20;   └── main-view.fxml

🎥 Caméras simulées



Trois caméras réseau (cam0, cam1, cam2), représentées par des fichiers :



/opt/cams/cam0

/opt/cams/cam1

/opt/cams/cam2



Contenu possible : RECORDING ou STOPPED.



🔌 API REST

Méthode	Endpoint	Description

POST	/api/cameras/{id}/record	Démarrer/arrêter l'enregistrement d'une caméra ({"action":"START"} ou {"action":"STOP"})

POST	/api/cameras/all/record	Appliquer une action à toutes les caméras

GET	/api/cameras/{id}	Consulter l'état d'une caméra

GET	/api/cameras	Lister toutes les caméras

🚀 Lancement du projet

Prérequis

JDK 17 ou supérieur

JavaFX SDK (téléchargé séparément, non fourni dans le dépôt) — télécharger ici

Eclipse IDE (projet fourni avec .classpath / .project)

1\. Importer le projet dans Eclipse



File → Import → Existing Projects into Workspace, puis sélectionner le dossier du dépôt cloné.



2\. Ajouter la bibliothèque JavaFX au projet



Si ce n'est pas déjà configuré : Project → Properties → Java Build Path → Libraries → Add External JARs, puis ajouter les .jar du dossier lib/ du SDK JavaFX téléchargé.



3\. Démarrer le serveur

Clic droit sur Server/main.java → Run As → Java Application

Le serveur démarre et écoute sur le port configuré (ex. 7777)



⚠️ Si une erreur BindException: Address already in use apparaît, un serveur tourne déjà en arrière-plan : terminez toutes les instances actives dans l'onglet Console avant de relancer.



4\. Démarrer le client JavaFX



Le client nécessite des VM arguments pointant vers le SDK JavaFX pour fonctionner (JavaFX n'est plus inclus au JDK depuis Java 11).



Clic droit sur ClientFX/MainApp.java → Run As → Run Configurations...

Onglet Arguments → dans VM arguments, ajouter (en remplaçant par le chemin local du SDK) :

\--module-path "C:\\chemin\\vers\\javafx-sdk-XX\\lib" --add-modules javafx.controls,javafx.fxml

Apply, puis Run



Sans cette étape, l'erreur suivante apparaît :



java.lang.module.FindException: Module javafx.controls not found

5\. Vérifier la connexion client-serveur



Dans ApiClient.java, vérifier que le port utilisé pour contacter le serveur correspond bien au port sur lequel le serveur écoute réellement.



👥 Auteurs



Eya Ben Maiz - Projet réalisé avec Oumayma Bouaoija — 2ème année GII, ENET'Com.

