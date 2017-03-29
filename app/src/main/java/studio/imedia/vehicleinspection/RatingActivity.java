package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class RatingActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private EditText etLeaveMsg;
    private Button btnConfirm;

    private Context mContext = RatingActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initWidget(); // 初始化控件状态
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
        String title = getString(R.string.title_rating);
        mTitle.setText(title);
    }

    /**
     * 关联控件
     */
    private void findView() {
        btnConfirm = (Button) findViewById(R.id.btn_confirm);
        etLeaveMsg = (EditText) findViewById(R.id.et_leave_msg);
    }

    /**
     * 初始化控件状态
     */
    private void initWidget() {
        MyWidgetUtils.enableButtonByEditText(btnConfirm, etLeaveMsg); // 初始化确认按钮使能状态
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
