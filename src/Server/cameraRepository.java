package Server;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


public class cameraRepository {
	// le dossier ou sont stockés les fichiers caméras
	private static final String CAMS_DIR = "C:/opt/cams/";
	public static final int CAM_COUNT = 3; // cam0,cam1,cam2
	
	public static final String status_RECORDING = "Recording";
	public static final String status_STOPPED = "Stopped";
	
	public void init() throws IOException {
		Path dir = Paths.get(CAMS_DIR); // crée un objet path pour "/opt/cams/"
		
		if(!Files.exists(dir)) {
			Files.createDirectories(dir);
		}
		for (int i=0; i<CAM_COUNT; i++) {
			Path camFile = dir.resolve("cam"+ i);
			
			if(!Files.exists(camFile))  {
				Files.write(camFile, status_STOPPED.getBytes(StandardCharsets.UTF_8));
				}
		}	
		}
	public camera getcamera(int id) throws IOException {
		validateId(id);
		Path path = Paths.get(CAMS_DIR + "cam" + id);
			
		String status = new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim();
		return new camera(id, status);
	}
	public camera setstatus(int id, String status) throws IOException {
		validateId(id);
		validateStatus(status);
		
		Path path = Paths.get(CAMS_DIR + "cam" + id);
		
		Files.write(path, status.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		return new camera(id, status);
	}
	private void validateId(int id) {
		if(id<0 || id>= CAM_COUNT) {
			throw new IllegalArgumentException("Id caméra invalide:" + id);
		
		}
	}
	private void validateStatus(String status) {
		if(!status_RECORDING.equals(status) && !status_STOPPED.equals(status)) {
			throw new IllegalArgumentException("Statut invalide:" + status);
		}
	}
	public camera[] getAllCameras() throws IOException {
        camera[] cameras = new camera[CAM_COUNT];
        for (int i = 0; i < CAM_COUNT; i++) {
            cameras[i] = getcamera(i);
        }
        return cameras;
	}
}
