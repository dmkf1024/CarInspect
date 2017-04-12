package studio.imedia.vehicleinspection;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;


public class CityActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;
    private ListView lvCity;
    private DrawerLayout mDrawerLayout;

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
                    MyWidgetUtils.hideProgressDialog();
                    setAdapter(); // 设置适配器
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);

        MyWidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
        initEvent(); // 监听事件回调
//        initDrawerlayout(); // 初始化抽屉式侧滑
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
        String title = getString(R.string.title_city);
        mTitle.setText(title);
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvCity = (ListView) findViewById(R.id.city_list);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.container);
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
