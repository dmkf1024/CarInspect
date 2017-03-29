package studio.imedia.vehicleinspection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class ModifyPwdActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private EditText etOrgPwd;
    private EditText etNewPwd;
    private EditText etConfirmNewPwd;
    private Button btnConfirm;

    StringBuffer mUrl = new StringBuffer();

    private Context mContext = ModifyPwdActivity.this;

    private final OkHttpClient mClient = new OkHttpClient();

    private static final int MSG_MODIFY_SUCCESS = 0x01;
    private static final int MSG_MODIFY_FAIL = 0x02;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MODIFY_SUCCESS:
                    Toast.makeText(mContext, "修改密码成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, SettingActivity.class));
                    finish();
                    break;
                case MSG_MODIFY_FAIL:
                    Toast.makeText(mContext, "请输入正确的原密码", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initEvent(); // 初始化监听事件
    }

    /**
     * 关联控件
     */
    private void findView() {
        etOrgPwd = (EditText) findViewById(R.id.et_org_pwd);
        etNewPwd = (EditText) findViewById(R.id.et_new_pwd);
        etConfirmNewPwd = (EditText) findViewById(R.id.et_confirm_new_pwd);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_IP, StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_PORT, StaticValues.TYPE_STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/updateUserInfo.jsp");
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_modify_pwd));
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        MyWidgetUtils.enableButtonByEditText(btnConfirm, etOrgPwd, etNewPwd, etConfirmNewPwd);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                initUrl(); // 初始化url
                modifyPwd(mUrl); // 修改密码
                break;
        }
    }

    /**
     * 修改密码
     * @param urlSB
     */
    private void modifyPwd(StringBuffer urlSB) {
        String url = urlSB.toString();
        int id = (int) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_USER_ID, StaticValues.TYPE_INTEGER);
        String orgPwd = etOrgPwd.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String confirmNewPwd = etConfirmNewPwd.getText().toString();

        if (newPwd.equals(confirmNewPwd)) {
            RequestBody formBody = new FormEncodingBuilder()
                    .add("id", String.valueOf(id))
                    .add("password", orgPwd)
                    .add("newPassword", newPwd)
                    .build();

            Request request = new Request.Builder()
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
                    if (!response.isSuccessful())
                        throw new IOException("Unexpected code " + response);

                    String jsonStr = response.body().string();
                    try {
                        int status = new JSONObject(jsonStr).getInt("status");
                        if (status == 0)
                            mHandler.sendEmptyMessage(MSG_MODIFY_SUCCESS);
                         else
                            mHandler.sendEmptyMessage(MSG_MODIFY_FAIL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Toast.makeText(mContext, "新密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
        }
    }
}
