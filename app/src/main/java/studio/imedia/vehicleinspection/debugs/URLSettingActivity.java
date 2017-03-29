package studio.imedia.vehicleinspection.debugs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import studio.imedia.vehicleinspection.MainActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;

public class URLSettingActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private String mIp;
    private String mPort;

    private TextView tvIp;
    private TextView tvPort;
    private EditText etIp;
    private EditText etPort;

    private Context mContext = URLSettingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlsetting);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initUrl(); // 初始化显示url
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_url_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if (isActive()) {
                    saveIp();
                    Toast.makeText(mContext, "接口URL修改成功，IP:" + mIp + ";端口号:" + mPort, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, MainActivity.class));
                } else {
                    Toast.makeText(mContext, "请输入要修改的IP或端口号", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveIp() {
        String ipUpdate = etIp.getText().toString().trim();
        String portUpdate = etPort.getText().toString().trim();
        if (!ipUpdate.isEmpty()) {
            mIp = ipUpdate;
        }
        Log.d("save", "inside " + mIp);
        MySharedPreferencesUtils.save(mContext, StaticValues.KEY_URL_IP, mIp);

        if (!portUpdate.isEmpty()) {
            mPort = portUpdate;
        }
        Log.d("save", "inside " + mPort);
        MySharedPreferencesUtils.save(mContext, StaticValues.KEY_URL_PORT, mPort);
    }

    private void initUrl() {
        Log.d("url", "enter initUrl");
        mIp = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_IP,
                StaticValues.TYPE_STRING);
        Log.d("url", mIp);
        mPort = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_PORT,
                StaticValues.TYPE_STRING);
        Log.d("url", mPort);

        if (mIp.isEmpty()) {
            Log.d("url", "ip is empty");
            mIp = getResources().getString(R.string.default_ip);
        } else {
            Log.d("url", "ip not empty --- " + mIp);
        }
        if (mPort.isEmpty()) {
            Log.d("url", "port is empty");
            mPort = getResources().getString(R.string.default_port);
        } else {
            Log.d("url", "port not empty --- " + mPort);
        }

        tvIp.setText(mIp);
        tvPort.setText(mPort);

    }

    /**
     * 关联控件
     */
    private void findView() {
        tvIp = (TextView) findViewById(R.id.tv_cur_ip);
        tvPort = (TextView) findViewById(R.id.tv_cur_port);
        etIp = (EditText) findViewById(R.id.et_ip);
        etPort = (EditText) findViewById(R.id.et_port);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public boolean isActive() {
        String ipContent = etIp.getText().toString().trim();
        String portContent = etPort.getText().toString().trim();
        if (ipContent.isEmpty() && portContent.isEmpty()) {
            return false;
        }
        return true;
    }
}
