package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;


public class CityActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.city_list)
    ListView lvCity;
    @BindView(R.id.container)
    DrawerLayout mDrawerLayout;

    private List<String> mCityList;
    private ArrayAdapter mAdapter;

    private String mIp;
    private String mPort;
    private StringBuffer mUrl = new StringBuffer();
    private int mLocationId = 1;

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GCityList mGCityList = null;

    private Context mContext = CityActivity.this;

    private static final int MSG_OK = 0;
    private static final int MSG_FAIL = 1;
    private static final int CONNECT_FAIL = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGCityList = (GCityList) msg.obj;
                    WidgetUtils.hideProgressDialog();
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
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);

        WidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        initToolbar(); // 初始化toolbar
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
        initEvent(); // 监听事件回调
//        initDrawerlayout(); // 初始化抽屉式侧滑
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getString(R.string.title_city);
        mTitle.setText(title);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        mIp = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        mPort = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);

        mUrl.append("http://");
        mUrl.append(mIp);
        mUrl.append(":");
        mUrl.append(mPort);
        mUrl.append("/Car/getCity.jsp");
    }

    /**
     * 获取数据 POST方式
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        RequestBody formBody = new FormEncodingBuilder()
                .add("locationid", String.valueOf(mLocationId))
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
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                // 解析json
                parseJsonByGson(jsonStr); // 解析json
            }
        });
    }

    /**
     * 通过gson解析json
     *
     * @param json
     */
    private void parseJsonByGson(String json) {
        GCityList gCityList = mGson.fromJson(json, GCityList.class);
        Message msg = new Message();
        msg.what = gCityList.status;
        if (msg.what == MSG_OK)
            msg.obj = gCityList;
        mHandler.sendMessage(msg);
    }

    static class GCityList {
        List<GCity> city;
        int status;
    }

    static class GCity {
        String name;
        int pid;
        int id;
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (mCityList == null) {
            mCityList = new ArrayList<>();
        }
        List<GCity> gCityList = mGCityList.city;
        for (GCity gCity : gCityList) {
            mCityList.add(gCity.name);
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, mCityList);
        }
        lvCity.setAdapter(mAdapter);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        lvCity.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GCity gCity = mGCityList.city.get(position);
        int cityId = gCity.id;
        String cityName = gCity.name;
        SPUtil.save(mContext, Constant.Key.USER_CITY_ID_TMP, cityId);
        SPUtil.save(mContext, Constant.Key.USER_CITY_NAME_TMP, cityName);
        mDrawerLayout.openDrawer(Gravity.RIGHT);
    }
}
