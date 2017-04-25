package studio.imedia.vehicleinspection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.fragments.CarFileFragment;
import studio.imedia.vehicleinspection.fragments.CarInfoFragment;
import studio.imedia.vehicleinspection.fragments.HomePageFragment;
import studio.imedia.vehicleinspection.fragments.OwnerInfoFragment;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_home_page)
    ImageView imgHomePage;
    @BindView(R.id.tv_home_page)
    TextView tvHomePage;
    @BindView(R.id.tab_home_page)
    LinearLayout tabHomePage;
    @BindView(R.id.img_car_info)
    ImageView imgCarInfo;
    @BindView(R.id.tv_car_info)
    TextView tvCarInfo;
    @BindView(R.id.tab_car_info)
    LinearLayout tabCarInfo;
    @BindView(R.id.img_owner_info)
    ImageView imgOwnerInfo;
    @BindView(R.id.tv_owner_info)
    TextView tvOwnerInfo;
    @BindView(R.id.tab_owner_info)
    LinearLayout tabOwnerInfo;
    @BindView(R.id.img_car_file)
    ImageView imgCarFile;
    @BindView(R.id.tv_car_file)
    TextView tvCarFile;
    @BindView(R.id.tab_car_file)
    LinearLayout tabCarFile;
    @BindView(R.id.container)
    FrameLayout container;

    private Fragment fragmentHomePage;
    private Fragment fragmentCarInfo;
    private Fragment fragmentOwnerInfo;
    private Fragment fragmentCarFile;

    private int colorNormal;
    private int colorSelected;

    private static final int HOME_PAGE = 0;     // fragment 首页标记
    private static final int CAR_INFO = 1;      // fragment 爱车信息标记
    private static final int OWNER_INFO = 2;    // fragment 车主信息标记
    private static final int CAR_FILE = 3;      // fragment 爱车档案标记

    private long mExitTime = 0;                 // 两次回退建的点击时间间隔
    private static final String BACK_TO_DESKTOP = "再按一次返回键回到桌面";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initColor(); // 初始化颜色
        initEvent(); // 初始化事件监听
        initTab(); // 初始化tab栏
        Log.d("file", Environment.getExternalStorageDirectory() + "/imooc_recorder_audios");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getBundle();
    }

    /**
     * 获取传值
     */
//    private void getBundle() {
//        Bundle bundle = MainActivity.this.getIntent().getExtras();
//        if (null != bundle) {
//            Log.d("car", "bundle not null---");
//            int carSeriesId = bundle.getInt(StaticValues.KEY_CAR_SERIES_ID);
//            String carSeries = bundle.getString(StaticValues.KEY_CAR_SERIES);
//            Log.d("car", carSeries + "--id=" + carSeriesId);
//        } else {
//            Log.d("car", "bundle null");
//        }
//    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 初始化颜色
     */
    private void initColor() {
        colorNormal = getResources().getColor(R.color.color_tab_text_normal);
        colorSelected = getResources().getColor(R.color.color_tab_text_selected);

    }

    /**
     * 初始化事件监听
     */
    private void initEvent() {
        tabHomePage.setOnClickListener(this);
        tabCarInfo.setOnClickListener(this);
        tabOwnerInfo.setOnClickListener(this);
        tabCarFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_home_page:
                resetTab();
                tvHomePage.setTextColor(colorSelected);
                imgHomePage.setImageResource(R.drawable.icon_home_selected);
                setSelect(HOME_PAGE);
                break;
            case R.id.tab_car_info:
                resetTab();
                tvCarInfo.setTextColor(colorSelected);
                imgCarInfo.setImageResource(R.drawable.icon_car_info_selected);
                setSelect(CAR_INFO);
                break;
            case R.id.tab_owner_info:
                resetTab();
                tvOwnerInfo.setTextColor(colorSelected);
                imgOwnerInfo.setImageResource(R.drawable.icon_owner_selected);
                setSelect(OWNER_INFO);
                break;
            case R.id.tab_car_file:
                resetTab();
                tvCarFile.setTextColor(colorSelected);
                imgCarFile.setImageResource(R.drawable.icon_car_file_selected);
                setSelect(CAR_FILE);
                break;
        }
    }

    /**
     * 设置选中的tab项
     *
     * @param position
     */
    private void setSelect(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragments(transaction); // 隐藏fragments
        switch (position) {
            case HOME_PAGE:
                if (null == fragmentHomePage) {
                    fragmentHomePage = new HomePageFragment();
                    transaction.add(R.id.container, fragmentHomePage);
                } else {
                    transaction.show(fragmentHomePage);
                }
                break;
            case CAR_INFO:
                if (null == fragmentCarInfo) {
                    fragmentCarInfo = new CarInfoFragment();
                    transaction.add(R.id.container, fragmentCarInfo);
                } else {
                    transaction.show(fragmentCarInfo);
                }
                break;
            case OWNER_INFO:
                if (null == fragmentOwnerInfo) {
                    fragmentOwnerInfo = new OwnerInfoFragment();
                    transaction.add(R.id.container, fragmentOwnerInfo);
                } else {
                    transaction.show(fragmentOwnerInfo);
                }
                break;
            case CAR_FILE:
                if (null == fragmentCarFile) {
                    fragmentCarFile = new CarFileFragment();
                    transaction.add(R.id.container, fragmentCarFile);
                } else {
                    transaction.show(fragmentCarFile);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 隐藏fragments
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (null != fragmentHomePage) {
            transaction.hide(fragmentHomePage);
        }
        if (null != fragmentCarInfo) {
            transaction.hide(fragmentCarInfo);
        }
        if (null != fragmentOwnerInfo) {
            transaction.hide(fragmentOwnerInfo);
        }
        if (null != fragmentCarFile) {
            transaction.hide(fragmentCarFile);
        }
    }

    /**
     * 重置tab栏
     */
    private void resetTab() {
        tvHomePage.setTextColor(colorNormal);
        tvCarInfo.setTextColor(colorNormal);
        tvOwnerInfo.setTextColor(colorNormal);
        tvCarFile.setTextColor(colorNormal);

        // 替换成normal的图标
        imgHomePage.setImageResource(R.drawable.icon_home_normal);
        imgCarInfo.setImageResource(R.drawable.icon_car_info_normal);
        imgOwnerInfo.setImageResource(R.drawable.icon_owner_normal);
        imgCarFile.setImageResource(R.drawable.icon_car_file_normal);
    }

    /**
     * 初始化tab栏
     */
    private void initTab() {
        resetTab();
        setSelect(HOME_PAGE);
        tvHomePage.setTextColor(colorSelected);
        // 切换成选中的图标
        imgHomePage.setImageResource(R.drawable.icon_home_selected);
    }

    /**
     * 返回键按键事件监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToDesktop();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 返回桌面
     */
    public void backToDesktop() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Toast.makeText(this, BACK_TO_DESKTOP, Toast.LENGTH_LONG).show();
            mExitTime = System.currentTimeMillis();
        } else {
            Intent mIntent = new Intent(Intent.ACTION_MAIN);
            mIntent.addCategory(Intent.CATEGORY_HOME);
            startActivity(mIntent);
        }
    }
}
