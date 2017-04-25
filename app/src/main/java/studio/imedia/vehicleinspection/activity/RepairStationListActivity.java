package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyRepairingStationAdapter;
import studio.imedia.vehicleinspection.bean.RepairingStation;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class RepairStationListActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.layout_to_search)
    RelativeLayout layoutToSearch;
    @BindView(R.id.layout_spinner_search)
    LinearLayout layoutSpinnerSearch;
    @BindView(R.id.layout_input_back)
    LinearLayout layoutInputBack;
    @BindView(R.id.layout_search)
    LinearLayout layoutSearch;
    @BindView(R.id.et_input)
    EditText etInput;
    @BindView(R.id.layout_input_search)
    RelativeLayout layoutInputSearch;
    @BindView(R.id.img_advertisement)
    ImageView imgAdvertisement;
    @BindView(R.id.lv_repairing_station)
    ListView lvRepairingStation;
    @BindView(R.id.tv_no_station)
    TextView tvNoStation;

    private List<RepairingStation> mStationList;
    private MyRepairingStationAdapter mAdapter;

    private Context mContext = RepairStationListActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GRepairStationList mGRepairStationList;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    WidgetUtils.hideProgressDialog();
                    mGRepairStationList = (GRepairStationList) msg.obj;
                    setAdapter(); // 设置适配器
                    break;
                case MSG_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_station_list);
        ButterKnife.bind(this);

        WidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        initToolbar(); // 初始化toolbar
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
        initEvent(); // 初始化监听事件
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText(getString(R.string.title_repairing_station));
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getRepairStationByCityId.jsp");
    }

    /**
     * 获取车检站数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        Log.d("status", "the usrl is " + url);
        int cityId = (int) SPUtil.get(mContext, Constant.Key.USER_CITY_ID, Constant.Type.INTEGER);
        Log.d("status", "the city id is " + cityId);
        RequestBody formBody = new FormEncodingBuilder()
                .add("cityId", String.valueOf(cityId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    WidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                GRepairStationList gRepairStationList = mGson.fromJson(jsonStr, GRepairStationList.class);
                int status = gRepairStationList.status;
                Log.d("status", status + "--");
                if (status == 0) {
                    Message msg = new Message();
                    msg.obj = gRepairStationList;
                    msg.what = MSG_OK;
                    mHandler.sendMessage(msg);
                } else
                    mHandler.sendEmptyMessage(MSG_FAIL);
            }
        });
    }

    private static class GRepairStationList {
        List<GRepairStation> repairStations;
        int status;
    }

    private static class GRepairStation {
        int id;
        String name;
        String county;
        String detailedAddress;
        String stationPic;
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutToSearch.setOnClickListener(this);
        layoutInputBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_to_search:
                showInputSearch(); // 显示输入搜索
                break;
            case R.id.layout_input_back:
                hideInputSearch(); // 隐藏输入搜索
                break;
        }
    }

    /**
     * 显示输入搜索
     */
    private void showInputSearch() {
        layoutInputSearch.setVisibility(View.VISIBLE);
        layoutSpinnerSearch.setVisibility(View.GONE);
    }

    /**
     * 隐藏输入搜索
     */
    private void hideInputSearch() {
        layoutInputSearch.setVisibility(View.GONE);
        layoutSpinnerSearch.setVisibility(View.VISIBLE);
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (mStationList == null)
            mStationList = new ArrayList<>();

        // 传递数据到指定list
        List<GRepairStation> gStationList = mGRepairStationList.repairStations;
        if (gStationList != null && gStationList.size() > 0) {
            showList(); // 显示列表
            for (GRepairStation gRepairStation : gStationList) {
                RepairingStation station = new RepairingStation();
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_station);
                station.setStationPic(bitmap);
                String stationName = gRepairStation.name;
                station.setStationName(stationName);
                String address = gRepairStation.county + gRepairStation.detailedAddress;
                station.setStationAddress(address);
                mStationList.add(station);
            }
        } else if (gStationList != null && gStationList.size() <= 0)
            hideList(); // 隐藏列表

        if (mAdapter == null)
            mAdapter = new MyRepairingStationAdapter(mContext, mStationList);
        lvRepairingStation.setAdapter(mAdapter);
    }

    /**
     * 隐藏列表
     */
    private void hideList() {
        tvNoStation.setVisibility(View.VISIBLE);
        lvRepairingStation.setVisibility(View.GONE);
    }

    /**
     * 显示列表
     */
    private void showList() {
        tvNoStation.setVisibility(View.GONE);
        lvRepairingStation.setVisibility(View.VISIBLE);
    }
}
