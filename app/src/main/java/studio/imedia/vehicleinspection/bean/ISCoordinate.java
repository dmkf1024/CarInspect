package studio.imedia.vehicleinspection.bean;

/**
 * Created by Aaron on 2015/11/28 0028.
 */
public class ISCoordinate {
    private double latitude;
    private double longitude;
    private String name;

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }
}
