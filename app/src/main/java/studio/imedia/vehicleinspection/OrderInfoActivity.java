package studio.imedia.vehicleinspection;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class OrderInfoActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;

    private Context mContext = OrderInfoActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        initToolbar(); // 初始化toolbar
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_order_info));
    }
}
