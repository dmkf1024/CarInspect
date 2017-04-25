package studio.imedia.vehicleinspection.debugs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import studio.imedia.vehicleinspection.activity.MainActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

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
        SPUtil.save(mContext, Constant.Key.URL_IP, mIp);

        if (!portUpdate.isEmpty()) {
            mPort = portUpdate;
        }
        Log.d("save", "inside " + mPort);
        SPUtil.save(mContext, Constant.Key.URL_PORT, mPort);
    }

    private void initUrl() {
        Log.d("url", "enter initUrl");
        mIp = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        Log.d("url", mIp);
        mPort = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);
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

    private void initToolbar() {
        mToolbar.setTitle("");
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
