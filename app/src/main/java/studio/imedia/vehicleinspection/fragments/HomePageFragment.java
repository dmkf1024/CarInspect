package studio.imedia.vehicleinspection.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import studio.imedia.vehicleinspection.activity.CarInspectionLibraryActivity;
import studio.imedia.vehicleinspection.activity.CarInsuranceActivity;
import studio.imedia.vehicleinspection.activity.IllegalQueryActivity;
import studio.imedia.vehicleinspection.activity.InspectionStationActivity;
import studio.imedia.vehicleinspection.activity.LoginActivity;
import studio.imedia.vehicleinspection.activity.OrderInfoActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.activity.RepairStationListActivity;
import studio.imedia.vehicleinspection.debugs.URLSettingActivity;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView mRightIcon;
    private TextView mTitle;

    private SliderLayout mSliderLayout;
    private PagerIndicator indicator;

    private TextView tvUserCount;
    private TextView tvLimit;

    private LinearLayout illegalQuery;
    private LinearLayout carInsurance;
    private LinearLayout repairStation;
    private LinearLayout validateCar;
    private RelativeLayout layoutInspectionOneKey; // 一键车检
    private TextView tvCheckOrder; // 查看车检订单

    private StringBuffer mUrl = new StringBuffer();
    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private Gist mGist;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGist = (Gist) msg.obj;
                    initView(); // 初始化视图: 用户数、限行
                    break;
                case MSG_FAIL:
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public HomePageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        findView(view); // 关联控件
        initToolbar(view); // 初始化toolbar
        initUrl(); // 初始化url
        return view;
    }

    private void initToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_car_inspection));

        mRightIcon = (ImageView) mToolbar.findViewById(R.id.right_icon);
        mRightIcon.setVisibility(View.VISIBLE);
        mRightIcon.setImageResource(R.drawable.icon_qr_code);
        mRightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), URLSettingActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvent(); // 注册监听事件
        initSlider(); // 初始化广告区
        getData(mUrl); // 获取界面数据：用户量、限行
    }

    /**
     * 关联控件
     */
    private void findView(View view) {
        illegalQuery = (LinearLayout) view.findViewById(R.id.illegal_query);
        carInsurance = (LinearLayout) view.findViewById(R.id.car_insurance);
        repairStation = (LinearLayout) view.findViewById(R.id.repair_station_nearby);
        validateCar = (LinearLayout) view.findViewById(R.id.validate_car);

        tvUserCount = (TextView) view.findViewById(R.id.tv_user_count);
        tvLimit = (TextView) view.findViewById(R.id.tv_limit);

        layoutInspectionOneKey = (RelativeLayout) view.findViewById(R.id.layout_inspection_one_key);
        tvCheckOrder = (TextView) view.findViewById(R.id.tv_check_order);
        mSliderLayout = (SliderLayout) view.findViewById(R.id.slider);
        indicator = (PagerIndicator) view.findViewById(R.id.custom_indicator);
    }

    /**
     * 注册监听事件
     */
    private void initEvent() {
        illegalQuery.setOnClickListener(this);
        carInsurance.setOnClickListener(this);
        repairStation.setOnClickListener(this);
        validateCar.setOnClickListener(this);

        layoutInspectionOneKey.setOnClickListener(this);
        tvCheckOrder.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isLogin = (boolean) SPUtil.get(getActivity(), Constant.Key.LOGIN_STATE, Constant.Type.BOOLEAN);
        switch (v.getId()) {
            case R.id.illegal_query:
                startActivity(new Intent(getActivity(), IllegalQueryActivity.class));
                break;
            case R.id.car_insurance:
                startActivity(new Intent(getActivity(), CarInsuranceActivity.class));
                break;
            case R.id.repair_station_nearby:
                if (isLogin)
                    startActivity(new Intent(getActivity(), RepairStationListActivity.class));
                else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.validate_car:
                startActivity(new Intent(getActivity(), CarInspectionLibraryActivity.class));
                break;
            case R.id.layout_inspection_one_key:
                if (isLogin)
                    startActivity(new Intent(getActivity(), InspectionStationActivity.class));
                else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            case R.id.tv_check_order:
                startActivity(new Intent(getActivity(), OrderInfoActivity.class));
                break;
        }
    }

    private void initSlider() {
        TextSliderView textSliderView = new TextSliderView(this.getActivity());
        textSliderView.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t2416/102/20949846/13425/a3027ebc/55e6d1b9Ne6fd6d8f.jpg");
        textSliderView.description("新品推荐");
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "新品推荐", Toast.LENGTH_SHORT).show();
            }
        });

        TextSliderView textSliderView2 = new TextSliderView(this.getActivity());
        textSliderView2.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t1507/64/486775407/55927/d72d78cb/558d2fbaNb3c2f349.jpg");
        textSliderView2.description("时尚男装");
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "时尚男装", Toast.LENGTH_SHORT).show();
            }
        });

        TextSliderView textSliderView3 = new TextSliderView(this.getActivity());
        textSliderView3.image("http://m.360buyimg.com/mobilecms/s300x98_jfs/t1363/77/1381395719/60705/ce91ad5c/55dd271aN49efd216.jpg");
        textSliderView3.description("家电秒杀");
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "家电秒杀", Toast.LENGTH_SHORT).show();
            }
        });

        mSliderLayout.addSlider(textSliderView);
        mSliderLayout.addSlider(textSliderView2);
        mSliderLayout.addSlider(textSliderView3);

//        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom); // 使用默认的indicator
        mSliderLayout.setCustomIndicator(indicator); // 使用自定义indicator
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.RotateUp);
        mSliderLayout.setDuration(3000);

        mSliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSliderLayout.stopAutoCycle();
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(getActivity(), Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(getActivity(), Constant.Key.URL_PORT, Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getUserCount.jsp");
    }

    /**
     * 获取数据：用户数、今日限行
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        final Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response);
                }

                String jsonStr = response.body().string();
                Gist gist = mGson.fromJson(jsonStr, Gist.class);
                int status = gist.status;
                if (status == 0) {
                    Message msg = new Message();
                    msg.what = MSG_OK;
                    msg.obj = gist;
                    mHandler.sendMessage(msg);
                } else
                    mHandler.sendEmptyMessage(MSG_FAIL);
            }
        });
    }

    private static class Gist {
        int count;
        String limit;
        int status;
    }

    /**
     * 初始化视图
     */
    private void initView() {
        int userCount = mGist.count;
        String limit = mGist.limit;
        tvUserCount.setText("用户数：" + userCount + "人");
        tvLimit.setText("今日限行" + limit);
    }
}
