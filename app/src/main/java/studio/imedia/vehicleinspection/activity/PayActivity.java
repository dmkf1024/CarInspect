package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class PayActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;

    private int mOrderId;
    private StringBuilder mUrl = new StringBuilder();

    private final Context mContext = PayActivity.this;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, MainActivity.class));
                    break;
                case MSG_FAIL:
                    Toast.makeText(mContext, "支付失败，请稍候再试", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(PayActivity.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initUrl(); // 初始化通信接口url
        getOrderId(); // 获取订单ID
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
                .append("/Car/doPay.jsp");
    }

    @OnClick(R.id.btn_pay)
    void onClick(View v) {
        WidgetUtils.showProgressDialog(mContext, null, "支付中...", true);
        final OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormEncodingBuilder()
                .add("orderId", String.valueOf(mOrderId))
                .build();

        String url = mUrl.toString();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    int status = new JSONObject(response.body().string()).getInt("status");
                    if (status == 0)
                        mHandler.sendEmptyMessage(MSG_OK);
                    else
                        mHandler.sendEmptyMessage(MSG_FAIL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        WidgetUtils.hideProgressDialog();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText("支付");
    }

    /**
     * 获取订单ID
     */
    private void getOrderId() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mOrderId = bundle.getInt(Constant.Key.ORDER_ID);
    }
}
