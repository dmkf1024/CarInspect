package studio.imedia.vehicleinspection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

public class CarFileDetailActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.tv_station)
    TextView tvStation;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.tv_field1)
    TextView tvField1;
    @BindView(R.id.tv_field2)
    TextView tvField2;
    @BindView(R.id.tv_field3)
    TextView tvField3;
    @BindView(R.id.tv_field4)
    TextView tvField4;
    @BindView(R.id.tv_field5)
    TextView tvField5;
    @BindView(R.id.tv_field6)
    TextView tvField6;

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
    private static final int CONNECT_FAIL = 0x03;
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
                case CONNECT_FAIL:
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_file_detail);
        ButterKnife.bind(this);

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
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getString(R.string.title_car_file_detail);
        tvTitle.setText(title);
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
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

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
                mHandler.sendEmptyMessage(CONNECT_FAIL);
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
        tvField1.setText(archives.field1 + "");
        tvField2.setText(archives.field2 + "");
        tvField3.setText(archives.field3 + "");
        tvField4.setText(archives.field4 + "");
        tvField5.setText(archives.field5 + "");
        tvField6.setText(archives.field6 + "");
    }
}
