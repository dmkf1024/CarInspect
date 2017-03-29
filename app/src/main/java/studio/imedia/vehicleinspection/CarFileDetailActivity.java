package studio.imedia.vehicleinspection;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import studio.imedia.vehicleinspection.bean.CarFile;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;

public class CarFileDetailActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;

    private TextView tvStation;
    private TextView tvDate;
    private TextView tvStatus;

    private TextView tvField1;
    private TextView tvField2;
    private TextView tvField3;
    private TextView tvField4;
    private TextView tvField5;
    private TextView tvField6;

    private int mOrderId;
    private String mDate;
    private String mStation;
    private int mPassState; // 0-未通过， 1-通过

    private final Context mContext = CarFileDetailActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GArchives mGArchives;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGArchives = (GArchives) msg.obj;
                    initView(mGArchives); // 初始化视图
                    break;
                case MSG_FAIL:
                    Toast.makeText(mContext, "数据获取失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_file_detail);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        getBundle(); // 获取上一界面传值
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

        mTitle = (TextView) findViewById(R.id.title);
        String title = getString(R.string.title_car_file_detail);
        mTitle.setText(title);
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvStatus = (TextView) findViewById(R.id.tv_status);
        tvStation = (TextView) findViewById(R.id.tv_station);

        tvField1 = (TextView) findViewById(R.id.tv_field1);
        tvField2 = (TextView) findViewById(R.id.tv_field2);
        tvField3 = (TextView) findViewById(R.id.tv_field3);
        tvField4 = (TextView) findViewById(R.id.tv_field4);
        tvField5 = (TextView) findViewById(R.id.tv_field5);
        tvField6 = (TextView) findViewById(R.id.tv_field6);
    }

    /**
     * 获取上一界面传值
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mOrderId = bundle.getInt("order_id");
            mDate = bundle.getString("car_file_date");
            mStation = bundle.getString("car_file_station");
            mPassState = bundle.getInt("car_file_is_pass");
        }
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_IP, StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_PORT, StaticValues.TYPE_STRING);

        mUrl.append("http:")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getArchivesInfoByOrderId.jsp");
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        RequestBody formBody = new FormEncodingBuilder()
                .add("orderId", String.valueOf(mOrderId))
                .build();

        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                GArchives gArchives = mGson.fromJson(jsonStr, GArchives.class);
                int status = gArchives.status;
                Log.d("files", "---" + String.valueOf(status));
                if (status == 0) {
                    Message msg = new Message();
                    msg.obj = gArchives;
                    msg.what = MSG_OK;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(MSG_FAIL);
                }
            }
        });
    }

    private static class GArchives {
        Archives archives;
        int status;
    }

    private static class Archives {
        int id;
        int orderId;
        String field1;
        String field2;
        String field3;
        String field4;
        String field5;
        String field6;
    }

    /**
     * 初始化视图
     */
    private void initView(GArchives gArchives) {
        Archives archives = gArchives.archives;
        tvDate.setText(mDate);
        tvStation.setText(mStation);
        if (mPassState == 0)
            tvStatus.setText("状态：未通过");
        else
            tvStatus.setText("状态：通过");
        tvField1.setText(archives.field1+"");
        tvField2.setText(archives.field2+"");
        tvField3.setText(archives.field3+"");
        tvField4.setText(archives.field4+"");
        tvField5.setText(archives.field5+"");
        tvField6.setText(archives.field6+"");
    }
}
