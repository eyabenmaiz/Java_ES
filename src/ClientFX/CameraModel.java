package ClientFX;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class CameraModel {
	 // On déclare des "propriétés" au lieu de simples champs
    private final IntegerProperty id     = new SimpleIntegerProperty();
    private final StringProperty  status = new SimpleStringProperty();
    private final StringProperty  lastUpdate = new SimpleStringProperty();

    // Constructeur
    public CameraModel(int id, String status, String lastUpdate) {
        this.id.set(id);
        this.status.set(status);
        this.lastUpdate.set(lastUpdate);
    }
 // Pour chaque propriété, on doit exposer 3 choses :

    // 1. La propriété elle-même (utilisée par le TableView pour le binding)
    public IntegerProperty idProperty()         { return id; }
    public StringProperty  statusProperty()     { return status; }
    public StringProperty  lastUpdateProperty() { return lastUpdate; }

    // 2. Le getter classique
    public int    getId()         { return id.get(); }
    public String getStatus()     { return status.get(); }
    public String getLastUpdate() { return lastUpdate.get(); }

    // 3. Le setter (qui met à jour la propriété)
    public void setStatus(String s)     { status.set(s); }
    public void setLastUpdate(String s) { lastUpdate.set(s); }
}

