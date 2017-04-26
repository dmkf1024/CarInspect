package studio.imedia.vehicleinspection.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.activity.SelectTimeWayActivity;
import studio.imedia.vehicleinspection.adapters.MyInspectionStationAdapter;
import studio.imedia.vehicleinspection.bean.Flow;
import studio.imedia.vehicleinspection.bean.InspectionStation;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class InspectionStationListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView lvInspectionStation;
    private List<InspectionStation> inspectionStationList;
    private List<InspectionStation> stationFromOkHttpList;
    private MyInspectionStationAdapter myInspectionStationAdapter;

    private Bitmap bitmap; // 车检站图片
    private Bundle bundle;
    private String mUrl;
    private int[] index;
    private double[] distances;

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    WidgetUtils.hideProgressDialog();
                    putIntoAdapter();
                    break;
            }
        }
    };
    private Object lngLat;

    public InspectionStationListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inspection_station_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        WidgetUtils.showProgressDialog(getActivity(), null, "数据加载中...", true);
        init();
        findView(); // 关联控件
        initUrl(); // 初始化url
        initEvent(); // 初始化监听事件
    }

    @Override
    public void onPause() {
        super.onPause();
        WidgetUtils.hideProgressDialog();
    }

    private void init() {
        bundle = getArguments();
        if (bundle != null) {
            index = bundle.getIntArray("index");
            distances = bundle.getDoubleArray("distance");
//            for (int i = 0; i < index.length; i++) {
//                Log.d("msg", "传过来的车检站id: " + index[i] +": "+distances[i]);
//                //                Log.d("msg", "Activity传过来的值" + StaticValues.DISTANCE[i] + "  " + bundle.getDouble(StaticValues.DISTANCE[i]));
//            }
        }
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvInspectionStation = (ListView) getActivity().findViewById(R.id.lv_station);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(getActivity(), Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(getActivity(), Constant.Key.URL_PORT,
                Constant.Type.STRING);
        mUrl = "http://" + ip + ":" + port + "/Car/inspectionStationInfo.jsp?cityId=2";
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            initUrl();
            getDataFromUrl(mUrl);
        } else {
            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initUrl();
        getDataFromUrl(mUrl);
    }

    /**
     * 从接口获取数据
     *
     * @param url
     */
    private void getDataFromUrl(String url) {
        if (stationFromOkHttpList == null) {
            stationFromOkHttpList = new ArrayList<>();
        } else {
            stationFromOkHttpList.clear();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                WidgetUtils.hideProgressDialog();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    WidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String result = response.body().string();
                parseJson(result);
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 解析json格式数据
     *
     * @param json json格式的字符串
     */
    private void parseJson(String json) {
        try {
            JSONArray jStations = new JSONObject(json).getJSONArray("inspectStations");
//            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_station);
            int jStationLength = jStations.length();
            for (int i = 0; i < jStationLength; i++) {
//                JSONObject jStation = jStations.getJSONObject(index[i]);
                JSONObject jStation = jStations.getJSONObject(i);
                InspectionStation station = new InspectionStation();

                // - 营业时间
                // 夏季上午开始时间
                String startTimeMorSummer = jStation.getString("startTimeMorSummer");
                station.setStartTimeMorningSummer(startTimeMorSummer);
                // 夏季上午结束时间
                String endTimeMorSummer = jStation.getString("endTimeMorSummer");
                station.setEndTimeMorningSummer(endTimeMorSummer);
                // 夏季下午开始时间
                String startTimeAftSummer = jStation.getString("startTimeAftSummer");
                station.setStartTimeAfternoonSummer(startTimeAftSummer);
                // 夏季下午结束时间
                String endTimeAftSummer = jStation.getString("startTimeAftWinter");
                station.setEndTimeAfternoonSummer(endTimeAftSummer);
                // 冬季上午开始时间
                String startTimeMorWinter = jStation.getString("startTimeMorWinter");
                station.setStartTimeMorningWinter(startTimeMorWinter);
                // 冬季上午结束时间
                String endTimeMorWinter = jStation.getString("endTimeMorWinter");
                station.setEndTimeMorningWinter(endTimeMorWinter);
                // 冬季下午开始时间
                String startTimeAftWinter = jStation.getString("startTimeAftWinter");
                station.setStartTimeAfternoonWinter(startTimeAftWinter);
                // 冬季下午结束时间
                String endTimeAftWinter = jStation.getString("endTimeAftWinter");
                station.setEndTimeAfternoonWinter(endTimeAftWinter);

                // 车检站距离定位处直线距离
                double distance = distances[index[i]];
                station.setDistance(distance);

                // 车检站图片
//                station.setStationPic(bitmap);
                station.setStationPic(null);

                // 车检站星级评价
                int stars = jStation.getInt("ratingStars");
                station.setStarNum(stars);

                // 车检站名字
                String name = jStation.getString("name");
                station.setStationName(name);

                // 车检站id
                int id = jStation.getInt("id");
                station.setId(id);

                // 车检站最近几天的日流量
                JSONArray jFlows = jStation.getJSONArray("flow");
                if (jFlows.length() != 0) {
                    List<Flow> dailyFlows = new ArrayList<>();
                    int jLength = jFlows.length();
                    for (int j = 0; j < jLength && j < 6; j++) {
                        JSONObject jFlow = jFlows.getJSONObject(j); // TODO
                        String date = jFlow.getString("date");
                        int flowValue = jFlow.getInt("dailyFlow");
                        Flow flow = new Flow();
                        flow.setDate(date);
                        flow.setValue(flowValue);
                        dailyFlows.add(flow);
                    }
                    station.setDailyFlows(dailyFlows);
                }
                stationFromOkHttpList.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJsonByGson(String json) {
        GInspectStations gInspectStations = mGson.fromJson(json, GInspectStations.class);
        for (GInspectStation gInspectStation : gInspectStations.inspectStations) {
            for (GFlow gFlow : gInspectStation.flow) {
                Log.d("ggson", gFlow.toString());
            }
        }
    }

    private static class GInspectStations {
        int status;
        List<GInspectStation> inspectStations;

        @Override
        public String toString() {
            return "GInspectStations [status=" + status + ", inspectStations=" + inspectStations + "]";
        }
    }

    private static class GInspectStation {
        int id;
        String name;
        String stationPic;
        int ratingStars;
        String startTimeMorSummer;
        String endTimeMorSummer;
        String startTimeAftSummer;
        String endTimeAftSummer;
        String startTimeMorWinter;
        String endTimeMorWinter;
        String startTimeAftWinter;
        String endTimeAftWinter;
        double lng;
        double lat;
        List<GFlow> flow;

        @Override
        public String toString() {
            return "GInspectStation [id=" + id + ", name=" + name + ", staticPic=" + stationPic +
                    ", ratingStars=" + ratingStars + ", startTimeMorSummer=" + startTimeMorSummer +
                    ", endTimeMorSummer=" + endTimeMorSummer + ", startTimeAftSummer=" + startTimeAftSummer +
                    ", endTimeAftSummer=" + endTimeAftSummer + ", startTimeMorWinter=" + startTimeMorWinter +
                    ", endTimeMorWinter=" + endTimeMorWinter + ", startTimeAftWinter=" + startTimeAftWinter +
                    ", endTimeAftWinter=" + endTimeAftWinter + ", lng=" + lng + ", lat=" + lat +
                    ", flow=" + flow + "]";
        }
    }

    private static class GFlow {
        String date;
        String dailyFlow;

        @Override
        public String toString() {
            return "GFlow [date=" + date + ", dailyFlow=" + dailyFlow + "]";
        }
    }


    /**
     * 初始化监听事件
     */
    private void initEvent() {
        lvInspectionStation.setOnItemClickListener(this);
    }

    /**
     * 将数据放入adapter中
     */
    private void putIntoAdapter() {
        if (null == myInspectionStationAdapter) {
            myInspectionStationAdapter = new MyInspectionStationAdapter(getActivity(), stationFromOkHttpList);
            Log.d("flow", "outside is " + stationFromOkHttpList.size());
        }
        lvInspectionStation.setAdapter(myInspectionStationAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long viewId) {
        switch (parent.getId()) {
            case R.id.lv_station:
                switch (position) {
                    case 0:
                        break;
                    default:
                        Intent intent = new Intent(getActivity(), SelectTimeWayActivity.class);
                        Bundle bundle = new Bundle();
                        int id = stationFromOkHttpList.get(position - 1).getId();
                        bundle.putInt(Constant.Key.STATION_ID, id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                }
                break;
        }
    }
}
