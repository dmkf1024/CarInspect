package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.layout_modify_pwd)
    RelativeLayout layoutModifyPwd;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    private Context mContext = SettingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initEvent(); // 初始化监听事件
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText(getString(R.string.title_setting));
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
                SPUtil.save(mContext, Constant.Key.LOGIN_STATE, false);
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                break;
        }
    }
}
