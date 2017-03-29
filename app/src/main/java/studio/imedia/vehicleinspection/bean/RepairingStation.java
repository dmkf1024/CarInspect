package studio.imedia.vehicleinspection.bean;

import android.graphics.Bitmap;

/**
 * Created by eric on 15/10/16.
 */
public class RepairingStation {
    private Bitmap stationPic;
    private String stationName;
    private String stationAddress;
    private double stationDistance;

    public Bitmap getStationPic() {
        return stationPic;
    }

    public void setStationPic(Bitmap stationPic) {
        this.stationPic = stationPic;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationAddress() {
        return stationAddress;
    }

    public void setStationAddress(String stationAddress) {
        this.stationAddress = stationAddress;
    }

    public double getStationDistance() {
        return stationDistance;
    }

    public void setStationDistance(double stationDistance) {
        this.stationDistance = stationDistance;
    }
}
