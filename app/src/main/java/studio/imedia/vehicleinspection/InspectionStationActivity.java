package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.services.route.DriveRouteResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.fragments.InspectionStationListFragment;
import studio.imedia.vehicleinspection.fragments.InspectionStationMapFragment;
import studio.imedia.vehicleinspection.service.LocationService;

public class InspectionStationActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView mSwitch;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.container)
    FrameLayout container;

    private InspectionStationListFragment fragmentStationList;
    private InspectionStationMapFragment fragmentStationMap;

    private DriveRouteResult driveRouteResult;
    private Intent service;
    private Bundle bundle;
    private IntentFilter intentFilter;
    private Receiver receiver;
    private int i = -1;

    private static final int STATION_LIST = 0;
    private static final int STATION_MAP = 1;

    private int curFragment = STATION_LIST;

    private Context mContext = InspectionStationActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspection_station);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        //setSelect方法在init()方法中实现。获取定位坐标后再运行setSelect()方法
//        setSelect(STATION_LIST); // 设置选择为列表显示
        init();
    }

    private void init() {
        if (null == bundle)
            bundle = new Bundle();
        if (null == intentFilter)
            intentFilter = new IntentFilter();
        if (null == receiver)
            receiver = new Receiver();
        abc();
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getResources().getString(R.string.title_select_car_inspection_station);
        mTitle.setText(title);

        mSwitch.setVisibility(View.VISIBLE);
        mSwitch.setImageResource(R.drawable.icon_map_show);
        mSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back:
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
                break;
            case R.id.right_icon:
                switch (curFragment) {
                    case STATION_LIST:
                        curFragment = STATION_MAP;
                        setSelect(STATION_MAP);
                        mSwitch.setImageResource(R.drawable.icon_list_show);
                        break;
                    case STATION_MAP:
                        curFragment = STATION_LIST;
                        setSelect(STATION_LIST);
                        mSwitch.setImageResource(R.drawable.icon_map_show);
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("system", "执行了onDestroy");
        if (service != null)
            stopService(service);
        if (receiver != null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    /**
     * 设置选择
     *
     * @param position
     */
    private void setSelect(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragments(transaction); // 隐藏fragments
        switch (position) {
            case STATION_LIST:
                if (null == fragmentStationList) {
                    fragmentStationList = new InspectionStationListFragment();
                    if (null != bundle)
                        fragmentStationList.setArguments(bundle);    //向Fragment传输数据
                    transaction.add(R.id.container, fragmentStationList);
                } else {
                    transaction.show(fragmentStationList);
                }
                break;
            case STATION_MAP:
                if (null == fragmentStationMap) {
                    fragmentStationMap = new InspectionStationMapFragment();
                    transaction.add(R.id.container, fragmentStationMap);
                } else {
                    transaction.show(fragmentStationMap);
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
        if (null != fragmentStationList) {
            transaction.hide(fragmentStationList);
        }
        if (null != fragmentStationMap) {
            transaction.hide(fragmentStationMap);
        }
    }

    protected void abc() {

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        if (null == service) {
            service = new Intent(this, LocationService.class);
            startService(service);
        }
        intentFilter.addAction("获取定位经纬度值");
        localBroadcastManager.registerReceiver(receiver, intentFilter);
        receiver.setOnReceivveLatLongListner(new Receiver.LatLongListner() {
            @Override
            public void OnReceived(double latitude, double longitude, int[] index, double[] distance) {
                //latitude为定位纬度  longitude为定位经度   index为将StaticValues.VI重新排序后的新序列下标

                bundle.putIntArray("index", index);
                bundle.putDoubleArray("distance", distance);
                setSelect(STATION_LIST); // 设置选择为列表显示
                Log.d("msg", "获取到的定位经纬度为：" + latitude + " " + longitude + " " + index[0] + " " + distance[index[0]]);
            }
        });


    }

}

