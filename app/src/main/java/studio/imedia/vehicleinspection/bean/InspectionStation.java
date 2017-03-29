package studio.imedia.vehicleinspection.bean;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

/**
 * Created by eric on 15/10/11.
 */
public class InspectionStation {

    private int id;

    private Bitmap stationPic;                  // 车检站图片
    private String stationName;                 // 车检站名字

    private String startTimeMorningSummer;      // 车检站夏季上午开始时间
    private String endTimeMorningSummer;        // 车检站夏季上午结束时间
    private String startTimeAfternoonSummer;    // 车检站夏季下午开始时间
    private String endTimeAfternoonSummer;      // 车检站夏季下午结束时间
    private String startTimeMorningWinter;      // 车检站冬季上午开始时间
    private String endTimeMorningWinter;        // 车检站冬季上午结束时间
    private String startTimeAfternoonWinter;    // 车检站冬季下午开始时间
    private String endTimeAfternoonWinter;      // 车检站冬季下午结束时间

    private double starNum;                     // 星评数量
    private double distance;                    // 距离

    private double latitude;                    //纬度
    private double longitude;                   //经度

    private int day1;                           // 第一天日期
    private int day1Value;                      // 第一天数值
    private int day2;                           // 第二天日期
    private int day2Value;                      // 第二天数值
    private int day3;                           // 第三天日期
    private int day3Value;                      // 第三天数值
    private int day4;                           // 第四天日期
    private int day4Value;                      // 第四天数值
    private int day5;                           // 第五天日期
    private int day5Value;                      // 第五天数值

    private List<Flow> dailyFlows;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStartTimeMorningSummer() {
        return startTimeMorningSummer;
    }

    public void setStartTimeMorningSummer(String startTimeMorningSummer) {
        this.startTimeMorningSummer = startTimeMorningSummer;
    }

    public String getEndTimeMorningSummer() {
        return endTimeMorningSummer;
    }

    public void setEndTimeMorningSummer(String endTimeMorningSummer) {
        this.endTimeMorningSummer = endTimeMorningSummer;
    }

    public String getStartTimeAfternoonSummer() {
        return startTimeAfternoonSummer;
    }

    public void setStartTimeAfternoonSummer(String startTimeAfternoonSummer) {
        this.startTimeAfternoonSummer = startTimeAfternoonSummer;
    }

    public String getEndTimeAfternoonSummer() {
        return endTimeAfternoonSummer;
    }

    public void setEndTimeAfternoonSummer(String endTimeAfternoonSummer) {
        this.endTimeAfternoonSummer = endTimeAfternoonSummer;
    }

    public String getStartTimeMorningWinter() {
        return startTimeMorningWinter;
    }

    public void setStartTimeMorningWinter(String startTimeMorningWinter) {
        this.startTimeMorningWinter = startTimeMorningWinter;
    }

    public String getEndTimeMorningWinter() {
        return endTimeMorningWinter;
    }

    public void setEndTimeMorningWinter(String endTimeMorningWinter) {
        this.endTimeMorningWinter = endTimeMorningWinter;
    }

    public String getStartTimeAfternoonWinter() {
        return startTimeAfternoonWinter;
    }

    public void setStartTimeAfternoonWinter(String startTimeAfternoonWinter) {
        this.startTimeAfternoonWinter = startTimeAfternoonWinter;
    }

    public String getEndTimeAfternoonWinter() {
        return endTimeAfternoonWinter;
    }

    public void setEndTimeAfternoonWinter(String endTimeAfternoonWinter) {
        this.endTimeAfternoonWinter = endTimeAfternoonWinter;
    }

    public double getStarNum() {
        return starNum;
    }

    public void setStarNum(double starNum) {
        this.starNum = starNum;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDay1() {
        return day1;
    }

    public void setDay1(int day1) {
        this.day1 = day1;
    }

    public int getDay1Value() {
        return day1Value;
    }

    public void setDay1Value(int day1Value) {
        this.day1Value = day1Value;
    }

    public int getDay2() {
        return day2;
    }

    public void setDay2(int day2) {
        this.day2 = day2;
    }

    public int getDay2Value() {
        return day2Value;
    }

    public void setDay2Value(int day2Value) {
        this.day2Value = day2Value;
    }

    public int getDay3() {
        return day3;
    }

    public void setDay3(int day3) {
        this.day3 = day3;
    }

    public int getDay3Value() {
        return day3Value;
    }

    public void setDay3Value(int day3Value) {
        this.day3Value = day3Value;
    }

    public int getDay4() {
        return day4;
    }

    public void setDay4(int day4) {
        this.day4 = day4;
    }

    public int getDay4Value() {
        return day4Value;
    }

    public void setDay4Value(int day4Value) {
        this.day4Value = day4Value;
    }

    public int getDay5() {
        return day5;
    }

    public void setDay5(int day5) {
        this.day5 = day5;
    }

    public int getDay5Value() {
        return day5Value;
    }

    public void setDay5Value(int day5Value) {
        this.day5Value = day5Value;
    }

    public List<Flow> getDailyFlows() {
        return dailyFlows;
    }

    public void setDailyFlows(List<Flow> dailyFlows) {
        this.dailyFlows = dailyFlows;
    }
}