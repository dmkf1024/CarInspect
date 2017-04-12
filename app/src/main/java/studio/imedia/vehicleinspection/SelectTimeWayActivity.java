package studio.imedia.vehicleinspection;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.MyCalendarUtils;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.views.MyDatePickerDialog;
import studio.imedia.vehicleinspection.views.MyTimePickerDialog;
import studio.imedia.vehicleinspection.views.SwitchView;

public class SelectTimeWayActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private Button btnCommit;
    private FrameLayout layoutChart;
    private LinearLayout noData;
    private TextView tvNoData;
    private TextView tvInspactStation;

    private RelativeLayout layoutSelectDate;
    private RelativeLayout layoutSelectTime;

    private TextView tvDate;
    private TextView tvTime;

    private SwitchView switchView;

    private Context mContext = SelectTimeWayActivity.this;

    private XYMultipleSeriesDataset mDataset;
    private XYSeries series;
    private XYMultipleSeriesRenderer mSeriesRenderer = null;
    private GraphicalView mGraphicalView;

    private LinkedList<Integer> mDataList = new LinkedList<>();

    private static final String TITLE = "title";
    private static final int AMOUNT_VALUE = 6;
    private static final int MAX_X = 7;
    private static final int MIN_X = 0;
    private static final int MAX_Y = 40;
    private static final int MIN_Y = 0;

    private static final int LABELS_Y = 5;

    private static final int[] valuesY = {0, 0, 0, 0, 0, 0};
    private static final String[] xLabelDate = {"24日", "25日", "26日", "27日", "28日", "29日"};
    private static final String[] xLabelTime = {"8:00~10:00", "10:00~11:30", "13:00~14:00", "14:00~15:00", "15:00~16:00", "16:00~17:30"};

    private ArrayList<Integer> valueY = new ArrayList<>();
    private ArrayList<String> textX = new ArrayList<>();

    private int mYear = 1991;
    private int mMonth = 1;
    private int mDay = 1;
    private int mHour = 0;
    private int mMinute = 0;
    private String dateSelected;

    private static final int TYPE_DATE = 0;
    private static final int TYPE_TIME = 1;

    private int mStationId = -1;
    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GInspectStation mGInspectStation = null;

    private static final int MSG_OK = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGInspectStation = (GInspectStation) msg.obj;
                    String stationName = mGInspectStation.name;
                    tvInspactStation.setText(stationName);
//                    initDate();
                    refreshGraph(dateSelected, TYPE_DATE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time_way);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initEvent(); // 初始化监听事件
        initGraphData(); // 初始化图表数据
        initGraph(); // 初始化柱状图
        getBundle(); // 获取上一界面传过来的值(mStationId)
        initUrl(); // 初始化url
        initDate(); // 初始化日期
        getDataFromUrl(mUrl); // 从接口url中获取数据
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_select_time_way));
    }

    /**
     * 关联控件
     */
    private void findView() {
        btnCommit = (Button) findViewById(R.id.btn_commit);
        layoutChart = (FrameLayout) findViewById(R.id.chart);
        noData = (LinearLayout) findViewById(R.id.no_data);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        layoutSelectDate = (RelativeLayout) findViewById(R.id.layout_select_date);
        layoutSelectTime = (RelativeLayout) findViewById(R.id.layout_select_time);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        switchView = (SwitchView) findViewById(R.id.switch_view);
        tvInspactStation = (TextView) findViewById(R.id.tv_inspection_station);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        btnCommit.setOnClickListener(this);
        layoutSelectDate.setOnClickListener(this);
        layoutSelectTime.setOnClickListener(this);
    }

    /**
     * 获取上一界面传来的数据
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mStationId = bundle.getInt(Constant.Key.STATION_ID);
        }
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);
        mUrl.append("http://");
        mUrl.append(ip);
        mUrl.append(":");
        mUrl.append(port);
        mUrl.append("/Car/inspectionStationInfo.jsp?cityId=2");
    }

    /**
     * 从url中获取数据
     */
    private void getDataFromUrl(StringBuffer urlSB) {
        String url = urlSB.toString();
        final Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                GInspectStationList gInspectStationList = mGson.fromJson(jsonStr, GInspectStationList.class);
                List<GInspectStation> gInspectStations = gInspectStationList.inspectStations;
                for (GInspectStation gInspectStation : gInspectStations) {
                    if (mStationId == gInspectStation.id) {
                        Message msg = new Message();
                        msg.what = MSG_OK;
                        msg.obj = gInspectStation;
                        mHandler.sendMessage(msg);
                    }
                }
            }
        });
    }

    private static class GInspectStationList {
        List<GInspectStation> inspectStations;
        int status;
    }

    private static class GInspectStation {
        String name;
        int id;
        List<GFlow> flow;
    }


    private static class GFlow {
        int dailyFlow;
        String date;
        int flowOne;
        int flowTwo;
        int flowThree;
        int flowFour;
        int flowFive;
        int flowSix;
        String timeOne;
        String timeTwo;
        String timeThree;
        String timeFour;
        String timeFive;
        String timeSix;
    }

    /**
     * 初始化图表数据
     */
    private void initGraphData() {
        if (null == mDataList) {
            mDataList = new LinkedList<>();
        }
        for (int i = 0; i < AMOUNT_VALUE; i++) {
            mDataList.add(valuesY[0]);
        }
    }

    /**
     * 初始化柱状图
     */
    private void initGraph() {
        // 初始化呼吸心率曲线
        mDataset = new XYMultipleSeriesDataset();
        series = new XYSeries(TITLE);

        // 绘制折线
        int i = 1;
        for (Integer data : mDataList) {
            series.add(i++, data);
        }

        mDataset.addSeries(series);
        mGraphicalView = initChart(mDataset);
    }

    private GraphicalView initChart(XYMultipleSeriesDataset dataset) {
        mSeriesRenderer = getRenderer();

        GraphicalView view = ChartFactory.getBarChartView(mContext,
                dataset, mSeriesRenderer, BarChart.Type.DEFAULT);
        view.setBackgroundColor(Color.WHITE);

        layoutChart.addView(view);
        return view;
    }

    /**
     * 获取renderer
     *
     * @return
     */
    private XYMultipleSeriesRenderer getRenderer() {
        XYMultipleSeriesRenderer multipleSeriesRenderer = new XYMultipleSeriesRenderer();
        multipleSeriesRenderer.setZoomEnabled(false);
        multipleSeriesRenderer.setZoomButtonsVisible(false);
        multipleSeriesRenderer.setBarSpacing(0.7);

        XYSeriesRenderer seriesRenderer = new XYSeriesRenderer();
        seriesRenderer.setColor(getResources().getColor(R.color.color_chart_bar_graph));
        seriesRenderer.setDisplayChartValues(true); // 显示数值
        seriesRenderer.setChartValuesTextSize(36); // 数值文本大小
        seriesRenderer.setChartValuesSpacing(10); // 数值与柱的距离

        multipleSeriesRenderer.addSeriesRenderer(seriesRenderer);
        setSettings(multipleSeriesRenderer);
        return multipleSeriesRenderer;
    }

    /**
     * 图标设置
     */
    private void setSettings(XYMultipleSeriesRenderer r) {
        r.setAxisTitleTextSize(20);
        r.setShowLegend(false);  // 是否显示图例文字
        r.setLabelsTextSize(20); // 设置轴标签文本大小
        r.setAxesColor(getResources().getColor(R.color.color_divider)); // 坐标轴颜色
        r.setLabelsColor(getResources().getColor(R.color.color_text_default));
        r.setDisplayValues(true); // 是否显示数值
        r.setZoomEnabled(false, false); // xy轴放大缩小设置
        r.setPanEnabled(false, false); // xy轴拖动设置
        r.setMarginsColor(Color.WHITE);
        r.setBackgroundColor(Color.WHITE);
        r.setApplyBackgroundColor(true);
        r.setFitLegend(true); // 设置是否调整合适的位置

        r.setXAxisMin(MIN_X); // 设置X轴最小值是MIN_X
        r.setXAxisMax(MAX_X); // 设置X轴最大值是MAX_X
        r.setXLabelsColor(getResources().getColor(R.color.color_text_default));
        r.setXLabels(0);
        // X轴刻度文字
        for (int i = MIN_X; i <= MAX_X; i++) {
            if (i > MIN_X && i < MAX_X) {
                r.addXTextLabel(i, xLabelDate[i - 1]);
            }
        }

        r.setYAxisMin(MIN_Y);
        r.setYAxisMax(MAX_Y);
        r.setYLabels(LABELS_Y);
        r.setYLabelsColor(0, getResources().getColor(R.color.color_divider));
        r.setYLabelsAlign(Paint.Align.CENTER.RIGHT);
        r.setGridColor(getResources().getColor(R.color.color_divider));
        r.setShowGrid(true);
    }

    @Override
    public void onClick(View v) {
        String dateContent = "";
        switch (v.getId()) {
            case R.id.layout_back:
                startActivity(new Intent(mContext, InspectionStationActivity.class));
                finish();
                break;
            case R.id.btn_commit:
                String date = mYear + "年" + tvDate.getText().toString();
                String time = tvTime.getText().toString();
                String station = tvInspactStation.getText().toString();
                String contentDate = tvDate.getText().toString().trim();
                String contentTime = tvTime.getText().toString().trim();
                if (TextUtils.isEmpty(contentDate) || TextUtils.isEmpty(contentTime)) {
                    Toast.makeText(mContext, "请先选择日期或时间", Toast.LENGTH_SHORT).show();
                } else {
                    if (SwitchView.STATE_SWITCH_ON == switchView.getState()) {
                        Intent intent = new Intent(mContext, SelectMasterWithPopupwidnowsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constant.Key.ORDER_DATE, date);
                        bundle.putString(Constant.Key.ORDER_TIME, time);
                        bundle.putString(Constant.Key.INSPECT_STATION, station);
                        bundle.putInt(Constant.Key.STATION_ID, mStationId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (SwitchView.STATE_SWITCH_OFF == switchView.getState()) {
                        Intent intent = new Intent(mContext, SubmitOrderActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putBoolean(Constant.Key.PROXY_STATE, false);
                        bundle.putString(Constant.Key.ORDER_DATE, date);
                        bundle.putString(Constant.Key.ORDER_TIME, time);
                        bundle.putString(Constant.Key.INSPECT_STATION, station);
                        bundle.putInt(Constant.Key.STATION_ID, mStationId);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.layout_select_date:
                dateContent = tvDate.getText().toString();
                if (dateContent.isEmpty()) {
                    getCurDate(); // 获取当前日期
                } else {
                    parseDateTimeFromTextView(tvDate, TYPE_DATE);
                }
                showDatePicker();
                break;
            case R.id.layout_select_time:
                String timeContent = tvTime.getText().toString();
                dateContent = tvDate.getText().toString();
                if (!dateContent.isEmpty()) {
                    if (timeContent.isEmpty()) {
                        getCurTime(); // 获取当前时间
                    } else {
                        parseDateTimeFromTextView(tvTime, TYPE_TIME);
                    }
                    showTimePicker();
                } else {
                    Toast.makeText(mContext, "请先选择日期", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initDate() {
        getCurDate(); // 获取当前日期
        tvDate.setText("");
        tvTime.setText("");
    }

    /**
     * 显示日期选择器弹出框
     */
    private void showDatePicker() {
        new MyDatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear++; // 月份从0开始计数，加上1
                String yearSelected = formatNum(year);
                String monthSelected = formatNum(monthOfYear);
                String daySelected = formatNum(dayOfMonth);
                tvDate.setText(monthSelected + "月" + daySelected + "日");
                dateSelected = yearSelected + "-" + monthSelected + "-" + daySelected;

                refreshGraph(dateSelected, TYPE_TIME); // 切换图标数据
                tvTime.setText(""); // tvTime内容清空
//                btnCommit.setEnabled(false);
            }
        }, mYear, (mMonth - 1), mDay).show(); // 月份从0开始计数，之前加1，现在减1
    }

    /**
     * 显示时间选择器弹出框
     */
    private void showTimePicker() {
        new MyTimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String hourSelected = formatNum(hourOfDay);
                String minuteSelected = formatNum(minute);
                tvTime.setText(hourOfDay + ":" + minuteSelected);

//                MyWidgetUtils.enableButtonByTextView(btnCommit, tvDate, tvTime); // 根据tvDate、tvTime判断是否使能提交按钮
            }
        }, mHour, mMinute, true).show();
    }

    /**
     * 获取当前日期
     */
    private void getCurDate() {
        Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH) + 1;
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        String year = formatNum(mYear);
        String month = formatNum(mMonth);
        String day = formatNum(mDay);
        dateSelected = year + "-" + month + "-" + day;
    }

    /**
     * 获取当前时间
     */
    private void getCurTime() {
        Calendar calendar = Calendar.getInstance();
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
    }

    /**
     * 规范数字，如果传入的数小于10，则返回两位字符串
     * 如传入7，则返回“07”
     *
     * @param num
     * @return
     */
    private String formatNum(int num) {
        if (num < 10)
            return "0" + num;
        else
            return "" + num;
    }

    /**
     * 从EditText中选择日期、时间
     *
     * @param textView
     * @param type
     */
    private void parseDateTimeFromTextView(TextView textView, int type) {
        String content = textView.getText().toString();
        if (!content.isEmpty()) {
            switch (type) {
                case TYPE_DATE:
                    mMonth = Integer.parseInt(content.substring(0, content.indexOf("月"))); // 减去多加的月份1
                    mDay = Integer.parseInt(content.substring(content.indexOf("月") + 1, content.indexOf("日")));
                    break;
                case TYPE_TIME:
                    mHour = Integer.parseInt(content.substring(0, content.indexOf(":")));
                    mMinute = Integer.parseInt(content.substring(content.indexOf(":") + 1));
                    break;
            }
        }
    }

    private void refreshGraph(String date, int type) {
        switch (type) {
            case TYPE_DATE:
                getDateFlow();
                break;
            case TYPE_TIME:
                getTimeFlowByDate(date);
                break;
        }
        refreshGraphData(valueY, textX);
    }

    private void getDateFlow() {
        List<GFlow> flows = mGInspectStation.flow;
        valueY.clear();
        textX.clear();
        // 获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // 获取未来七天的日期
        textX = MyCalendarUtils.getFutureWeeklyDate(year, month, day);
        int dailyFlowLength = flows.size();
        if (flows != null && dailyFlowLength > 0) {
            showChart(); // 显示图表
            int i = 0; // 获取的json数组下标
            for (String dayStr : textX) {
                int dayInt = Integer.parseInt(dayStr.substring(0, dayStr.indexOf("日")));
                if (i < dailyFlowLength) {
                    GFlow flow = flows.get(i);
                    String date = flow.date;
                    int dayFromJson = Integer.parseInt(date.substring(date.lastIndexOf("-") + 1));
                    if (dayFromJson == dayInt) {
                        i++;
                        valueY.add(flow.dailyFlow);
                    } else {
                        valueY.add(0);
                    }
                } else {
                    valueY.add(0);
                }
            }
//            for (int i = 0; i < dailyFlowLength && i < 6; i++) {
//                GFlow flow = flows.get(dailyFlowLength - i - 1);
//                valueY.add(flow.dailyFlow + 20);
//                String date = flow.date;
//                date = date.substring(date.lastIndexOf("-") + 1);
//            }
        } else {
            hideChart("未来一周没有车检车辆"); // 隐藏图表
        }
    }

    private void getTimeFlowByDate(String date) {
        List<GFlow> flows = mGInspectStation.flow;
        valueY.clear();
        textX.clear();
        if (null != flows && flows.size() > 0) {
            for (GFlow flow : flows) {
                if (date.equals(flow.date)) {
                    showChart(); // 显示图表
                    valueY.add(flow.flowOne);
                    valueY.add(flow.flowTwo);
                    valueY.add(flow.flowThree);
                    valueY.add(flow.flowFour);
                    valueY.add(flow.flowFive);
                    valueY.add(flow.flowSix);

                    textX.add(flow.timeOne);
                    textX.add(flow.timeTwo);
                    textX.add(flow.timeThree);
                    textX.add(flow.timeFour);
                    textX.add(flow.timeFive);
                    textX.add(flow.timeSix);
                    break;
                }
            }
            if (valueY.isEmpty())
                hideChart();
        } else {
            hideChart(); // 隐藏图表
        }
    }

    /**
     * 刷新图标数据
     *
     * @param valueY
     */
    private void refreshGraphData(ArrayList<Integer> valueY, ArrayList<String> textX) {
        if (null == mDataList) {
            mDataList = new LinkedList<>();
        }
        if (null == mDataset) {
            mDataset = new XYMultipleSeriesDataset();
        }
        if (null == series) {
            series = new XYSeries(TITLE);
        }

        mDataset.removeSeries(series);
        series.clear();
        mDataList.clear();
        for (Integer i : valueY) {
            mDataList.add(i);
        }

        int i = MIN_X + 1;
        for (Integer data : mDataList) {
            if (i < MAX_X) {
                series.add(i++, data);
            } else {
                break;
            }
        }
        mDataset.addSeries(series);

        refreshXTextLabel(textX); // 更新x轴刻度标签
        String aaa = mSeriesRenderer.getXTextLabel(2.0);

        mGraphicalView.repaint();
        layoutChart.postInvalidate();
    }

    /**
     * 修改x轴刻度标签
     *
     * @param labels
     */
    private void refreshXTextLabel(ArrayList<String> labels) {
        int i = MIN_X + 1;
        for (String str : labels) {
            if (i < MAX_X) {
                mSeriesRenderer.addXTextLabel(i, str);
                i++;
            } else {
                break;
            }
        }
    }

    /**
     * 显示图表
     */
    private void showChart() {
        layoutChart.setVisibility(View.VISIBLE);
        noData.setVisibility(View.INVISIBLE);
    }

    /**
     * 隐藏图表
     * @param str 显示提示信息
     */
    private void hideChart(String str) {
        hideChart();
        tvNoData.setText(str);
    }

    /**
     * 隐藏图表
     */
    private void hideChart() {
        String str = getResources().getString(R.string.inspection_station_available);
        tvNoData.setText(str);
        noData.setVisibility(View.VISIBLE);
        layoutChart.setVisibility(View.INVISIBLE);
    }
}
