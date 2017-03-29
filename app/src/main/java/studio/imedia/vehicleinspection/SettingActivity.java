package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private RelativeLayout layoutModifyPwd;
    private Button btnLogout;

    private Context mContext = SettingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initEvent(); // 初始化监听事件
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_setting));
    }

    /**
     * 关联控件
     */
    private void findView() {
        layoutModifyPwd = (RelativeLayout) findViewById(R.id.layout_modify_pwd);
        btnLogout = (Button) findViewById(R.id.btn_logout);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutModifyPwd.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_modify_pwd:
                startActivity(new Intent(mContext, ModifyPwdActivity.class));
                break;
            case R.id.btn_logout:
                MySharedPreferencesUtils.save(mContext, StaticValues.KEY_LOGIN_STATE, false);
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                break;
        }
    }
}
