package studio.imedia.vehicleinspection;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.adapters.MyCouponAdapter;
import studio.imedia.vehicleinspection.bean.Coupon;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class MyCouponActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.lv_coupon)
    ListView lvCoupon;
    @BindView(R.id.tv_no_coupon)
    TextView tvNoCoupon;

    private List<Coupon> mCouponList;

    private MyCouponAdapter mAdapter;

    private Context mContext = MyCouponActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GCouponList mGCouponList;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    Log.d("coupon", "msg_ok");
                    mGCouponList = (GCouponList) msg.obj;
                    WidgetUtils.hideProgressDialog();
                    setAdapter(); // 设置适配器
                    break;
                case MSG_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        ButterKnife.bind(this);

        WidgetUtils.showProgressDialog(mContext, null, "数据加载中...", true);
        initToolbar(); // 初始化toolbar
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText(getString(R.string.title_my_coupon));
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getCouponList.jsp");
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        int id = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);
        RequestBody formBody = new FormEncodingBuilder()
                .add("id", String.valueOf(id))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("coupon", "onResponse");
                    WidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                GCouponList gCouponList = mGson.fromJson(jsonStr, GCouponList.class);
                int status = gCouponList.status;
                Message msg = new Message();
                if (status == 0) {
                    msg.what = MSG_OK;
                    msg.obj = gCouponList;
                } else
                    msg.what = MSG_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }

    private static class GCouponList {
        List<GCoupon> coupons;
        int status;
    }

    private static class GCoupon {
        String cityId;
        String countyId;
        String deadline;
        int discount;
        int id;
        String name;
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        // 传递数据到bean
        if (mCouponList == null)
            mCouponList = new ArrayList<>();
        List<GCoupon> list = mGCouponList.coupons;
        if (list != null && list.size() > 0) {
            showList();
            for (GCoupon gCoupon : list) {
                Coupon coupon = new Coupon();
                String timeStamp = gCoupon.deadline;
                int year = Integer.parseInt(timeStamp.substring(0, timeStamp.indexOf("-")));
                int month = Integer.parseInt(timeStamp.substring(timeStamp.indexOf("-") + 1, timeStamp.lastIndexOf("-")));
                int day = Integer.parseInt(timeStamp.substring(timeStamp.lastIndexOf("-") + 1, timeStamp.indexOf(" ")));
                coupon.setCity(gCoupon.cityId);
                coupon.setCounty(gCoupon.countyId);
                coupon.setYear(year);
                coupon.setMonth(month);
                coupon.setDay(day);
                coupon.setPrice(gCoupon.discount);
                coupon.setCouponName(gCoupon.name);
                mCouponList.add(coupon);
            }

            // 将bean传到adapter里面
            if (mAdapter == null)
                mAdapter = new MyCouponAdapter(mContext, mCouponList);
            lvCoupon.setAdapter(mAdapter);
        } else {
            hideList();
        }
    }

    /**
     * 显示列表
     */
    private void showList() {
        lvCoupon.setVisibility(View.VISIBLE);
        tvNoCoupon.setVisibility(View.GONE);
    }

    /**
     * 隐藏列表
     */
    private void hideList() {
        lvCoupon.setVisibility(View.GONE);
        tvNoCoupon.setVisibility(View.VISIBLE);
    }
}
