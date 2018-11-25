package si.fri.rso.teamlj.rents.dtos;


public class MapEntity {

    private Integer id;

    private float latitude;

    private float longitude;

    private String locationString;

    private String locationName;

    private int numberOfAvailableBikes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getNumberOfAvailableBikes() {
        return numberOfAvailableBikes;
    }

    public void setNumberOfAvailableBikes(int numberOfAvailableBikes) {
        this.numberOfAvailableBikes = numberOfAvailableBikes;
    }
}
