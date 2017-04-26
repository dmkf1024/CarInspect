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

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.activity.CarInspectionLibraryActivity;
import studio.imedia.vehicleinspection.activity.CarInsuranceActivity;
import studio.imedia.vehicleinspection.activity.IllegalQueryActivity;
import studio.imedia.vehicleinspection.activity.InspectionStationActivity;
import studio.imedia.vehicleinspection.activity.LoginActivity;
import studio.imedia.vehicleinspection.activity.OrderInfoActivity;
import studio.imedia.vehicleinspection.activity.RepairStationListActivity;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.slider)
    SliderLayout mSliderLayout;
    @BindView(R.id.tv_user_count)
    TextView tvUserCount;
    @BindView(R.id.tv_limit)
    TextView tvLimit;
    @BindView(R.id.tv_check_order)
    TextView tvCheckOrder;
    @BindView(R.id.layout_inspection_one_key)
    RelativeLayout layoutInspectionOneKey;
    @BindView(R.id.illegal_query)
    LinearLayout illegalQuery;
    @BindView(R.id.car_insurance)
    LinearLayout carInsurance;
    @BindView(R.id.repair_station_nearby)
    LinearLayout repairStation;
    @BindView(R.id.validate_car)
    LinearLayout validateCar;
    @BindView(R.id.custom_indicator)
    PagerIndicator indicator;

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
        ButterKnife.bind(this, view);
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

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvent(); // 注册监听事件
        initSlider(); // 初始化广告区
        getData(mUrl); // 获取界面数据：用户量、限行
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
                if (isLogin) {
                    Toast.makeText(getActivity(), "此功能暂未开放", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(getActivity(), RepairStationListActivity.class));
                } else {
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
        textSliderView.image(R.drawable.img_engine);
        textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
        textSliderView.description("NGK火花塞 赛车直选");
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "广告 敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        TextSliderView textSliderView2 = new TextSliderView(this.getActivity());
        textSliderView2.image(R.drawable.img_car_model);
        textSliderView2.description("一汽-大众家用汽车");
        textSliderView2.setScaleType(BaseSliderView.ScaleType.CenterCrop);
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "广告 敬请期待", Toast.LENGTH_SHORT).show();
            }
        });

        TextSliderView textSliderView3 = new TextSliderView(this.getActivity());
        textSliderView3.image(R.drawable.img_wolun);
        textSliderView3.description("汽车配件，来京东");
        textSliderView3.setScaleType(BaseSliderView.ScaleType.CenterCrop);
        textSliderView.setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Toast.makeText(getActivity(), "广告 敬请期待", Toast.LENGTH_SHORT).show();
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
