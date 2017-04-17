package studio.imedia.vehicleinspection.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eric on 15/12/9.
 */
public class CalendarUtils {

    /**
     * 获取未来七天的“日”，包括当天
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static ArrayList<String> getFutureWeeklyDate(int year, int month, int day) {
        ArrayList<String> week = new ArrayList<>();
        week.add(day + "日");
        for (int i = 0; i < 6; i++) {
            day = nextDay(year, month, day);
            String dayStr = day + "日";
            week.add(dayStr);
        }
        return week;
    }

    /**
     * 下一天
     * @param year
     * @param month
     * @param day
     * @return
     */
    private static int nextDay(int year, int month, int day) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day < 31)
                    day++;
                else
                    day = 1;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day < 30)
                    day++;
                else
                    day = 1;
                break;
            case 2:
                if (isLeapYear(year)) {
                    if (day < 29)
                        day++;
                    else
                        day = 1;
                } else {
                    if (day < 28)
                        day++;
                    else
                        day = 1;
                }
                break;
        }
        return day;
    }

    /**
     * 判断是否是闰年
     * @return
     */
    private static boolean isLeapYear(int year) {
        if (year % 100 == 0) {
            if (year % 400 == 0)
                return true;
            return false;
        } else {
            if (year % 4 == 0)
                return true;
            return false;
        }
    }
}
