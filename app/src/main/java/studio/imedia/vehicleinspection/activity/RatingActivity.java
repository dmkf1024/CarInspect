package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class RatingActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.et_leave_msg)
    EditText etLeaveMsg;

    private Context mContext = RatingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initWidget(); // 初始化控件状态
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getString(R.string.title_rating);
        mTitle.setText(title);
    }


    /**
     * 初始化控件状态
     */
    private void initWidget() {
        WidgetUtils.enableButtonByEditText(btnConfirm, etLeaveMsg); // 初始化确认按钮使能状态
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                startActivity(new Intent(mContext, MyOrderActivity.class));
                finish();
                break;
        }
    }
}
