package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPwd;
    @BindView(R.id.btn_register)
    Button btnRegister;

    private String mPhone;
    private String mPassword;

    private final Context mContext = RegisterActivity.this;

    private final OkHttpClient mClient = new OkHttpClient();
    private StringBuffer mUrl = new StringBuffer();

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_OK:
                    Toast.makeText(mContext, "新用户注册成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constant.Key.FROM, Constant.ACTIVITY_REGISTER);
                    bundle.putString(Constant.Key.PHONE, mPhone);
                    bundle.putString(Constant.Key.PASSWORD, mPassword);
                    intent.putExtras(bundle);
                    Log.d("reg", intent.toString());
                    startActivity(intent);
                    finish();
                    break;
                case MSG_FAIL:
                    Toast.makeText(mContext, "该号码已被注册，请登录", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initEvent(); // 监听事件回调
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getString(R.string.title_register);
        mTitle.setText(title);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                register(); // 注册
                break;
        }
    }

    /**
     * 注册
     */
    private void register() {
        if (WidgetUtils.isEtContentEqual(etPassword, etConfirmPwd)) {
            final String phone = etPhone.getText().toString();
            final String password = etPassword.getText().toString();
            initUrl(); // 初始化url
            RequestBody formBody = new FormEncodingBuilder()
                    .add("phone", phone)
                    .add("password", password)
                    .build();

            Request request = new Request.Builder()
                    .url(mUrl.toString())
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
                        if (status == 0) {
                            mPhone = phone;
                            mPassword = password;
                            mHandler.sendEmptyMessage(MSG_OK);
                        } else
                            mHandler.sendEmptyMessage(MSG_FAIL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(mContext, "请确认两次密码输入一致", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/register.jsp"); // TODO 添加注册接口
    }
}
