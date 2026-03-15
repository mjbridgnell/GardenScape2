package Plants;
import java.io.Serializable;

public class Plant implements Serializable {
    private static final long serialVersionUID = 1L;
    public String plantName;
    public long timestamp;

    public Plant(String plantName, long timestamp)
    {
        this.plantName = plantName;
        this.timestamp = timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getPlantName()
    {
        return plantName;
    }

    public long getTimestamp()
    {
        return timestamp;
    }
}
