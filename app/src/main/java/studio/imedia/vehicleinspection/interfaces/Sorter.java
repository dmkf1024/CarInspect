package studio.imedia.vehicleinspection.interfaces;

import java.util.List;

import studio.imedia.vehicleinspection.bean.GDLocation;

/**
 * Created by eric on 15/11/12.
 */
public interface Sorter {

    /**
     *
     * @param myLocation        我当前位置的高德坐标(x, y)
     * @param stationLocations  范围内车检站的坐标(x, y)
     * @return 按路线距离从小到大排序好的车检站坐标列表的下标
     */
    public int[] sortByLocation (GDLocation myLocation, List<GDLocation> stationLocations);
}
