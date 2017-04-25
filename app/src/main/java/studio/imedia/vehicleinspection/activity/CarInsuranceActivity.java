package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;

public class CarInsuranceActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;

    private Context mContext = CarInsuranceActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_insurance);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText(getString(R.string.title_car_insurance));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
