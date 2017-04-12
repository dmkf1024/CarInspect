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
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
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

public class SelectCarTypeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;
    private ListView lvCarModel;
    private TextView tvNoCarType;
    private ArrayAdapter<String> mAdapter;

    private List<String> carModelList;
    private String mIp;
    private String mPort;
    private int mCarBrandId;
    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private Gson mGson = new Gson();

    private static final String DIALOG_MSG = "加载中...";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private GTypeList mGTypeList = null;

    private Context mContext = SelectCarTypeActivity.this;

    private static final int MSG_OK = 0;
    private static final int MSG_FAIL = 1;
    private static final int CONNECT_OUT = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    MyWidgetUtils.hideProgressDialog();
                    mGTypeList = (GTypeList) msg.obj;
                    setAdapter();
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    hideList();
                    break;
                case CONNECT_OUT:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_car_type);

        MyWidgetUtils.showProgressDialog(mContext, null, DIALOG_MSG, false);
        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        showList();
        getBundle(); // 获取传递的数据
        initUrl(); // 初始化url
        getData(mUrl); // 初始化数据
        initEvent(); // 监听事件回调
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
        String title = getString(R.string.title_select_car_model);
        mTitle.setText(title);

    }

    /**
     * 关联控件
     */
    private void findView() {
        lvCarModel = (ListView) findViewById(R.id.lv_car_type);
        tvNoCarType = (TextView) findViewById(R.id.tv_no_car_type);
    }

    /**
     * 获取传递过来的数据
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mCarBrandId = bundle.getInt(Constant.Key.CAR_BRAND_ID);
        }
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
        mUrl.append("/Car/getCarType.jsp");
        Log.d("carr", mUrl.toString());
    }

    /**
     * 初始化数据
     * POST方式
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        RequestBody formBody = new FormEncodingBuilder()
                .add("carBrandId", String.valueOf(mCarBrandId))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_OUT);
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
     * @param jsonStr
     */
    private void parseJsonByGson(String jsonStr) {
        GTypeList gTypeList = mGson.fromJson(jsonStr, GTypeList.class);
        Message msg = new Message();
        msg.what = gTypeList.status;
        if (msg.what == MSG_OK)
            msg.obj = gTypeList;
        mHandler.sendMessage(msg);
    }

    static class GTypeList {
        List<GCarType> cartype;
        int status;
    }

    static class GCarType {
        String name;
        int pid;
        int id;
        int type;
    }

    /**
     * 配置适配器
     */
    private void setAdapter() {
        if (carModelList == null) {
            carModelList = new ArrayList<>();
        }
        for (GCarType gCarType : mGTypeList.cartype) {
            carModelList.add(gCarType.name);
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, carModelList);
        }
        lvCarModel.setAdapter(mAdapter);
    }

    /**
     * 显示列表
     */
    private void showList() {
        lvCarModel.setVisibility(View.VISIBLE);
        tvNoCarType.setVisibility(View.GONE);
    }

    /**
     * 隐藏列表
     */
    private void hideList() {
        lvCarModel.setVisibility(View.GONE);
        tvNoCarType.setVisibility(View.VISIBLE);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        lvCarModel.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("types", "" + position);
        GCarType gCarType = mGTypeList.cartype.get(position);
//        MySharedPreferencesUtils.save(mContext, StaticValues.KEY_USER_CAR_BRAND_ID, mCarBrandId);
//        MySharedPreferencesUtils.save(mContext, StaticValues.KEY_USER_CAR_TYPE_ID, gCarType.id);

        Intent intent = new Intent(mContext, MainActivity.class);
        Bundle bundle = new Bundle();
        String typeName = gCarType.name;
        int typeId = gCarType.id;
        bundle.putString(Constant.Key.CAR_TYPE_NAME, typeName);
        bundle.putInt(Constant.Key.CAR_TYPE_ID, typeId);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
