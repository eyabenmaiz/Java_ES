package Server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class camera {
	private int id;
	private String status; // recording or stopped
	private LocalDateTime lastupdate; // date et heure de la dernière modification
	public camera (int id, String status) {
		this.id=id;
		this.status=status;
		this.lastupdate= LocalDateTime.now();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
		this.lastupdate = lastupdate;
		 // Quand on change le statut, on met aussi à jour lastUpdate
	}
	
	public String toJson() {
		String ts= lastupdate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		return String.format("{\"cameraId\":%d,\"status\":\"%s\",\"lastUpdate\":\"%s\"}",id, status, ts);
	}

}
