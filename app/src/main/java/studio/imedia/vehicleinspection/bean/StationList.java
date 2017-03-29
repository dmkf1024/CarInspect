package studio.imedia.vehicleinspection.bean;

import java.util.List;

import studio.imedia.vehicleinspection.bean.ISCoordinate;

/**
 * Created by Aaron on 2015/11/28 0028.
 */
public class StationList {

    public static List<ISCoordinate> isCoordinateList;
    public static int numVI;
    public static StationList stationList = new StationList();

    public static StationList getInstance() {
        if (null == stationList) {
            synchronized (StationList.class) {
                if (null == stationList)
                    stationList = new StationList();
            }
        }
        return stationList;
    }

    static {
        numVI = 3;
        for (int i = 0; i < numVI; i++) {
            ISCoordinate isCoordinate = new ISCoordinate();
            isCoordinate.setLatitude(30.11);
            isCoordinate.setLatitude(120.33);
            isCoordinate.setName("杭州市第二十二车检站");
            isCoordinateList.add(isCoordinate);
        }
    }

    public StationList() {
    }
}
