package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

import studio.imedia.vehicleinspection.adapters.MyCouponAdapter;
import studio.imedia.vehicleinspection.bean.Coupon;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class SelectCouponActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;
    private ListView lvCoupon;
    private TextView tvNoData;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GCouponList mGCouponList = null;

    private List<Coupon> mCouponList;
    private MyCouponAdapter mAdapter;

    private Context mContext = SelectCouponActivity.this;

    private static final String TAG = "mycoupon";

    private static final int MSG_HAS_COUPON = 0x01;
    private static final int MSG_NO_COUPON = 0x02;
    private static final int MSG_FAIL = 0x03;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HAS_COUPON:
                    Log.d("coupon", "msg_ok");
                    MyWidgetUtils.hideProgressDialog();
                    MyWidgetUtils.showList(lvCoupon, tvNoData); // 显示列表
                    mGCouponList = (GCouponList) msg.obj;
                    setAdapter();
                    break;
                case MSG_NO_COUPON:
                    MyWidgetUtils.hideProgressDialog();
                    MyWidgetUtils.hideList(lvCoupon, tvNoData);
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取数据异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_coupon);

        MyWidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        initToolbar(); // 初始化toolbar
        initUrl(); // 初始化url
        findView(); // 关联控件
        getData(mUrl); // 获取数据
        initEvent(); // 回调事件监听
    }

    /**
     * 初始化
     */
    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        String title = getString(R.string.title_select_coupon);
        mTitle.setText(title);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_IP,
                StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_PORT,
                StaticValues.TYPE_STRING);

        mUrl.append("http://");
        mUrl.append(ip);
        mUrl.append(":");
        mUrl.append(port);
        mUrl.append("/Car/getCouponList.jsp");
        Log.d("mUrl", mUrl.toString());
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvCoupon = (ListView) findViewById(R.id.coupon_list);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        lvCoupon.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mContext, SubmitOrderActivity.class);
                Bundle bundle = new Bundle();
                int couponId = mGCouponList.coupons.get(position).id;
                bundle.putString(StaticValues.KEY_FROM, StaticValues.ACTIVITY_SELECT_COUPON);
                bundle.putInt(StaticValues.KEY_COUPON_ID, couponId);
                bundle.putBoolean(StaticValues.KEY_IS_USE_COUPON, true);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        Log.d(TAG, "the url is " + url);
        int userId = (int) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_USER_ID, StaticValues.TYPE_INTEGER);
        Log.d(TAG, "user id is " + userId);
        // 换成动态id
        RequestBody formBody = new FormEncodingBuilder()
                .add("id", String.valueOf(userId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Log.d(TAG, "this");
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                MyWidgetUtils.hideProgressDialog();
                Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                // 解析json
                parseJsonByGson(jsonStr); // 解析json
            }
        });
    }

    /**
     * 通过gson解析json
     * @param jsonStr
     */
    private void parseJsonByGson(String jsonStr) {
        Log.d("mUrl", jsonStr);
        GCouponList gCouponList = mGson.fromJson(jsonStr, GCouponList.class);
        Message msg = new Message();
        int status = gCouponList.status;
        Log.d("mUrl", status+"");
        if (status == 0) {
            List<GCoupon> gCoupons = gCouponList.coupons;
            if (gCoupons != null && gCoupons.size() > 0) {
                msg.what = MSG_HAS_COUPON;
                msg.obj = gCouponList;
            } else {
                msg.what = MSG_NO_COUPON;
            }
        } else {
            msg.what = MSG_FAIL;
        }
        mHandler.sendMessage(msg);
    }

    private static class GCouponList {
        List<GCoupon> coupons;
        int status;
    }

    private static class GCoupon {
        String name;
        int discount;
        int id;
        String cityId;
        String deadline;
        String countyId;
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (mCouponList == null)
            mCouponList = new ArrayList<>();
        List<GCoupon> gCouponList = mGCouponList.coupons;
        for (GCoupon gCoupon : gCouponList) {
            Coupon coupon = new Coupon();

            coupon.setCouponName(gCoupon.name);

            coupon.setPrice(gCoupon.discount);

            String deadline = gCoupon.deadline;
            int year = Integer.parseInt(deadline.substring(0, deadline.indexOf("-")));
            int month = Integer.parseInt(deadline.substring(deadline.indexOf("-") + 1, deadline.lastIndexOf("-")));
            int day = Integer.parseInt(deadline.substring(deadline.lastIndexOf("-") + 1, deadline.indexOf(" ")));
            coupon.setYear(year);
            coupon.setMonth(month);
            coupon.setDay(day);

            String cityName = gCoupon.cityId;
            coupon.setCity(cityName);
            mCouponList.add(coupon);
        }

        if (mAdapter == null)
            mAdapter = new MyCouponAdapter(mContext, mCouponList);
        lvCoupon.setAdapter(mAdapter);
    }
}
