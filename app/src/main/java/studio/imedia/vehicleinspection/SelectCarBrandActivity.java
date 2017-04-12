package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class SelectCarBrandActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private ListView lvCarSeries;
    private ArrayAdapter<String> mAdapter;
    private List<String> seriesList;

    private static final String[] seriesArray = {"丰田", "法拉利", "马自达"};

    private String mIp;
    private String mPort;
    private int mId;
    private StringBuffer mUrl = new StringBuffer();


    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GBrandList mGBrandList = null;

    private static final String DIALOG_MSG = "加载中...";

    private Context mContext = SelectCarBrandActivity.this;

    private static final int MSG_OK = 0;
    private static final int MSG_FAIL = 1;
    private static final int CONNECT_FAIL = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGBrandList = (GBrandList) msg.obj;
                    setAdapter();
                    MyWidgetUtils.hideProgressDialog();
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取服务器数据失败", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_select_car_brand);

        MyWidgetUtils.showProgressDialog(mContext, null, DIALOG_MSG, false);
        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initEvent(); // 监听事件回调
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
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
        String title = getString(R.string.title_select_car_series);
        mTitle.setText(title);
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvCarSeries = (ListView) findViewById(R.id.lv_car_series);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        lvCarSeries.setOnItemClickListener(this);
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
        mUrl.append("/Car/getCarBrand.jsp");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(SelectCarBrandActivity.this, MainActivity.class);
        Bundle bundle = new Bundle();

        GBrand gBrand = mGBrandList.carbrand.get(position);
        bundle.putString(Constant.Key.CAR_BRAND_NAME, gBrand.name);
        bundle.putInt(Constant.Key.CAR_BRAND_ID, gBrand.id);
        Log.d("brand", gBrand.id+"");

        intent.putExtras(bundle);
        String series = bundle.getString(Constant.Key.CAR_BRAND_NAME);
        Log.d("carr", series);
        startActivity(intent);
        finish();
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Log.d("carr", url);

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                parseJsonByGson(jsonStr); // 解析json
            }
        });
    }

    /**
     * 通过gson解析json
     * @param json
     */
    private void parseJsonByGson(String json) {
        GBrandList gBrandList = mGson.fromJson(json, GBrandList.class);
        Message msg = new Message();
        msg.what = gBrandList.status;
        if (msg.what == MSG_OK)
            msg.obj = gBrandList;
        mHandler.sendMessage(msg);
    }

    static class GBrandList {
        List<GBrand> carbrand;
        int status;
    }

    static class GBrand {
        String name;
        int id;
    }

    /**
     * 配置适配器
     */
    private void setAdapter() {
        if (seriesList == null) {
            seriesList = new ArrayList<>();
        }
        for (GBrand gBrand : mGBrandList.carbrand) {
            seriesList.add(gBrand.name);
        }

        if (null == mAdapter) {
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, seriesList);
        }
        lvCarSeries.setAdapter(mAdapter);
    }
}
