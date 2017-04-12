package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class PayActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;

    private int mOrderId;

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

        initToolbar(); // 初始化toolbar
        getOrderId(); // 获取订单ID
        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyWidgetUtils.showProgressDialog(mContext, null, "支付中...", true);
                String url = "http://best8023.com:8080/Car/doPay.jsp";
                final OkHttpClient client = new OkHttpClient();
                RequestBody formBody = new FormEncodingBuilder()
                        .add("orderId", String.valueOf(mOrderId))
                        .build();

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
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyWidgetUtils.hideProgressDialog();
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
