package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.et_suggestion)
    EditText etSuggestion;
    @BindView(R.id.btn_send)
    Button btnSend;

    private Context mContext = FeedBackActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    Toast.makeText(mContext, "提交反馈成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    break;
                case MSG_FAIL:
                    Toast.makeText(mContext, "提交反馈失败，请稍候再试", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_feed_back);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initEvent(); // 初始化监听事件
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText(getString(R.string.title_feed_back));
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        WidgetUtils.enableButtonByEditText(btnSend, etSuggestion);
        btnSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                initUrl(); // 初始化url
                sendFeedback(mUrl); // 发送返回信息
                break;
        }
    }

    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/addSuggestion.jsp");
    }

    /**
     * 发送反馈信息
     *
     * @param urlSB
     */
    private void sendFeedback(StringBuffer urlSB) {
        String url = urlSB.toString();
        int userId = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);
        String feedback = etSuggestion.getText().toString().trim();

        RequestBody formBody = new FormEncodingBuilder()
                .add("id", String.valueOf(userId))
                .add("suggest", feedback)
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
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                try {
                    int status = new JSONObject(jsonStr).getInt("status");
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
}
