package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.bean.Flow;
import studio.imedia.vehicleinspection.bean.InspectionStation;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MyCalendarUtils;

/**
 * Created by eric on 15/10/11.
 */
public class MyInspectionStationAdapter extends BaseAdapter {

    private Context context;
    private List<InspectionStation> inspectionStationList;
    private LayoutInflater inflater;

    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_NORMAL = 1;

    private int mCurYear;
    private int mCurMonth;
    private int mCurDay;

    public MyInspectionStationAdapter(Context context, List<InspectionStation> inspectionStationList) {
        this.context = context;
        this.inspectionStationList = inspectionStationList;
        inflater = LayoutInflater.from(context);
        Log.d("flow", inspectionStationList.size() + " inside length");

        Calendar calendar = Calendar.getInstance();
        mCurYear = calendar.get(Calendar.YEAR);
        mCurMonth = calendar.get(Calendar.MONTH) + 1;
        mCurDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public int getCount() {
        return inspectionStationList.size() + 1;
    }

    @Override
    public Object getItem(int position) {
        return inspectionStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_SEARCH;
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder01 holder01 = null;
        ViewHolder02 holder02 = null;
        int type = getItemViewType(position);
        if (null == convertView) {
            switch (type) {
                case TYPE_SEARCH:
                    holder01 = new ViewHolder01();
                    convertView = inflater.inflate(R.layout.item_search, null);
                    holder01.etSearch = (EditText) convertView.findViewById(R.id.et_search);
                    convertView.setTag(holder01);
                    break;
                case TYPE_NORMAL:
                    holder02 = new ViewHolder02();
                    convertView = inflater.inflate(R.layout.item_inspection_station, null);
                    holder02.imgStation = (ImageView) convertView.findViewById(R.id.img_station);
                    holder02.tvStationName = (TextView) convertView.findViewById(R.id.tv_station_name);
                    holder02.tvPeriodSummer = (TextView) convertView.findViewById(R.id.tv_period_summer);
                    holder02.tvPeriodWinter = (TextView) convertView.findViewById(R.id.tv_period_winter);
                    holder02.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
                    holder02.tvDistance = (TextView) convertView.findViewById(R.id.tv_distance);

                    for (int i = 0; i < StaticValues.LENGTH_TIMELY_FLOW; i++) {
                        int dateResId = context.getResources().getIdentifier("tv_day" + (i + 1),
                                "id", context.getPackageName());
                        holder02.tvFlowDates[i] = (TextView) convertView.findViewById(dateResId);

                        int valueResId = context.getResources().getIdentifier("tv_day" + (i + 1) + "_value",
                                "id", context.getPackageName());
                        holder02.tvFlowValues[i] = (TextView) convertView.findViewById(valueResId);
                    }
//                    holder02.tvDay1 = (TextView) convertView.findViewById(R.id.tv_day1);
//                    holder02.tvDay1Value = (TextView) convertView.findViewById(R.id.tv_day1_value);
//                    holder02.tvDay2 = (TextView) convertView.findViewById(R.id.tv_day2);
//                    holder02.tvDay2Value = (TextView) convertView.findViewById(R.id.tv_day2_value);
//                    holder02.tvDay3 = (TextView) convertView.findViewById(R.id.tv_day3);
//                    holder02.tvDay3Value = (TextView) convertView.findViewById(R.id.tv_day3_value);
//                    holder02.tvDay4 = (TextView) convertView.findViewById(R.id.tv_day4);
//                    holder02.tvDay4Value = (TextView) convertView.findViewById(R.id.tv_day4_value);
//                    holder02.tvDay5 = (TextView) convertView.findViewById(R.id.tv_day5);
//                    holder02.tvDay5Value = (TextView) convertView.findViewById(R.id.tv_day5_value);

                    convertView.setTag(holder02);
                    break;
            }
        } else {
            switch (type) {
                case TYPE_SEARCH:
                    holder01 = (ViewHolder01) convertView.getTag();
                    break;
                case TYPE_NORMAL:
                    holder02 = (ViewHolder02) convertView.getTag();
                    break;
            }
        }

        switch (type) {
            case TYPE_SEARCH:
                break;
            case TYPE_NORMAL:
                InspectionStation inspectionStation = inspectionStationList.get(position - 1);
                holder02.imgStation.setImageBitmap(inspectionStation.getStationPic());
                holder02.tvStationName.setText(inspectionStation.getStationName());

                String startTimeMorningSummer = inspectionStation.getStartTimeMorningSummer();
                String endTimeMorningSummer = inspectionStation.getEndTimeMorningSummer();
                String startTimeAfternoonSummer = inspectionStation.getStartTimeAfternoonSummer();
                String endTimeAfternoonSummer = inspectionStation.getEndTimeAfternoonSummer();

                String startTimeMorningWinter = inspectionStation.getStartTimeMorningWinter();
                String endTimeMorningWinter = inspectionStation.getEndTimeMorningWinter();
                String startTimeAfternoonWinter = inspectionStation.getStartTimeAfternoonWinter();
                String endTimeAfternoonWinter = inspectionStation.getEndTimeMorningWinter();

//                startTimeMorningSummer = formatTime(startTimeMorningSummer);
//                endTimeMorningSummer = formatTime(endTimeMorningSummer);
//                startTimeAfternoonSummer = formatTime(startTimeAfternoonSummer);
//                endTimeAfternoonSummer = formatTime(endTimeAfternoonSummer);
//
//                startTimeMorningWinter = formatTime(startTimeMorningWinter);
//                endTimeMorningWinter = formatTime(endTimeMorningWinter);
//                startTimeAfternoonWinter = formatTime(startTimeAfternoonWinter);
//                endTimeAfternoonWinter = formatTime(endTimeAfternoonWinter);

                holder02.tvPeriodSummer.setText(startTimeMorningSummer + "~" + endTimeMorningSummer + "  " +
                        startTimeAfternoonSummer + "~" + endTimeAfternoonSummer + "(夏)");
                holder02.tvPeriodWinter.setText(startTimeMorningWinter + "~" + endTimeMorningWinter + "  " +
                        startTimeAfternoonWinter + "~" + endTimeAfternoonWinter + "(冬)");

                holder02.ratingBar.setNumStars((int) inspectionStation.getStarNum());
                holder02.tvDistance.setText(inspectionStation.getDistance() + "km");

                List<Flow> dailyFlows = inspectionStationList.get(position - 1).getDailyFlows();
                ArrayList<String> dates = MyCalendarUtils.getFutureWeeklyDate(mCurYear, mCurMonth, mCurDay);
                if (null != dailyFlows) {
                    int dailyFlowLength = dailyFlows.size();
                    int i = 0; // 日期下标
                    int j = 0; // json数组下标
                    for (String dayStr : dates) {
                        if (i >= 5)
                            break;
                        holder02.tvFlowDates[i].setText(dayStr);
                        int dayLocal = Integer.parseInt(dayStr.substring(0, dayStr.indexOf("日")));
                        if (j < dailyFlowLength) { // 如果json数组下标在长度范围内
                            String dateStrFromJson = dailyFlows.get(j).getDate();
                            int dayFromJson = Integer.parseInt(dateStrFromJson.substring(
                                    dateStrFromJson.lastIndexOf("-") + 1));
                            Log.d("datly", "dayFromJson: " + dayFromJson + "--- dayLocal:" + dayLocal + " times: " + i);
                            if (dayFromJson == dayLocal) { // 如果json数组日期对应
                                holder02.tvFlowValues[i].setText(dailyFlows.get(j).getValue()+""); // 赋值
                                j++;
                            } else { // 如果json数组中没有对应日期的值
                                holder02.tvFlowValues[i].setText("0"); // 赋值为0
                            }
                        } else { // 如果json数组读完了
                            holder02.tvFlowValues[i].setText("0"); // 其他赋值为0
                        }
                        i++;
                    }

//                    for (int i = 0; i < dailyFlows.size() && i < StaticValues.LENGTH_TIMELY_FLOW; i++) {
//                        String date = dailyFlows.get(i).getDate();
//                        date = date.substring(date.lastIndexOf("-") + 1);
//                        int value = dailyFlows.get(i).getValue();
//                        Log.d("adp", dailyFlows.size() + " -- " + (StaticValues.LENGTH_TIMELY_FLOW - 1 - i) + "");
//                        holder02.tvFlowDates[StaticValues.LENGTH_TIMELY_FLOW - 1 - i].setText(date + "日");
//                        holder02.tvFlowValues[StaticValues.LENGTH_TIMELY_FLOW - 1 - i].setText(value + "");
//                    }
                }
//                holder02.tvDay1.setText(inspectionStation.getDay1() + "日");
//                holder02.tvDay2.setText(inspectionStation.getDay2() + "日");
//                holder02.tvDay3.setText(inspectionStation.getDay3() + "日");
//                holder02.tvDay4.setText(inspectionStation.getDay4() + "日");
//                holder02.tvDay5.setText(inspectionStation.getDay5() + "日");
//
//                holder02.tvDay1Value.setText(inspectionStation.getDay1Value() + "");
//                holder02.tvDay2Value.setText(inspectionStation.getDay2Value() + "");
//                holder02.tvDay3Value.setText(inspectionStation.getDay3Value() + "");
//                holder02.tvDay4Value.setText(inspectionStation.getDay4Value() + "");
//                holder02.tvDay5Value.setText(inspectionStation.getDay5Value() + "");
                break;
        }


        return convertView;
    }

    private String formatTime(String time) {
        if (time != null && !time.isEmpty()) {
            return time.substring(0, time.lastIndexOf(":"));
        } else {
            Log.d("car", "empty");
            return "";
        }
    }

    private class ViewHolder01 {
        private EditText etSearch;
    }

    private class ViewHolder02 {
        private ImageView imgStation;
        private TextView tvStationName;

        private TextView tvPeriodSummer;
        private TextView tvPeriodWinter;

        private RatingBar ratingBar;
        private TextView tvDistance;

//        private TextView tvDay1;
//        private TextView tvDay1Value;
//        private TextView tvDay2;
//        private TextView tvDay2Value;
//        private TextView tvDay3;
//        private TextView tvDay3Value;
//        private TextView tvDay4;
//        private TextView tvDay4Value;
//        private TextView tvDay5;
//        private TextView tvDay5Value;

        private TextView[] tvFlowDates = new TextView[StaticValues.LENGTH_TIMELY_FLOW];
        private TextView[] tvFlowValues = new TextView[StaticValues.LENGTH_TIMELY_FLOW];
    }
}
