package studio.imedia.vehicleinspection.bean;

/**
 * Created by eric on 15/10/9.
 */
public class CarFile {

    private String date;
    private String station;
    private boolean state;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
