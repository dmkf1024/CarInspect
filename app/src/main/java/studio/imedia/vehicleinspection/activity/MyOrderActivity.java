package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.fragments.OrderedFragment;
import studio.imedia.vehicleinspection.fragments.OrderingFragment;
import studio.imedia.vehicleinspection.gbean.GOrder;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class MyOrderActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.btn_ordering)
    Button btnOrdering;
    @BindView(R.id.btn_ordered)
    Button btnOrdered;

    private OrderedFragment fragmentOrdered;
    private OrderingFragment fragmentOrdering;

    private int textColorSelected;
    private int textColorNormal;

    private static final int ORDERING = 0;
    private static final int ORDERED = 1;

    private Context mContext = MyOrderActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GOrderList mGOrderList;
    private List<GOrder> mFinishedOrders = new ArrayList<>();
    private List<GOrder> mFinishingOrders = new ArrayList<>();

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_FAIL = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    mGOrderList = (GOrderList) msg.obj;
                    filterCoupon(mGOrderList); // 筛选
                    setSelected(ORDERING);
                    break;
                case MSG_FAIL:
                    Toast.makeText(mContext, "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
            WidgetUtils.hideProgressDialog();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initColor(); // 初始化颜色
        initEvent(); // 初始化监听事件
//        setSelected(ORDERING);
        initUrl(); // 初始化url
        getData(mUrl); // 获取数据
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle.setText(getString(R.string.title_my_order));
    }

    /**
     * 初始化颜色
     */
    private void initColor() {
        textColorNormal = getResources().getColor(R.color.colorPrimary);
        textColorSelected = Color.WHITE;
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        btnOrdered.setOnClickListener(this);
        btnOrdering.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ordering:
                // 切换fragment至orderingFragment
                setSelected(ORDERING);
                btnOrdered.setBackgroundResource(R.drawable.bg_ordered_normal);
                btnOrdered.setTextColor(textColorNormal);
                btnOrdering.setBackgroundResource(R.drawable.bg_ordering_selected);
                btnOrdering.setTextColor(textColorSelected);
                break;
            case R.id.btn_ordered:
                // 切换fragment至orderedFragment
                setSelected(ORDERED);
                btnOrdered.setBackgroundResource(R.drawable.bg_ordered_selected);
                btnOrdered.setTextColor(textColorSelected);
                btnOrdering.setBackgroundResource(R.drawable.bg_ordering_normal);
                btnOrdering.setTextColor(textColorNormal);
                break;
        }
    }

    private void setSelected(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragments(transaction); // 隐藏fragments
        switch (position) {
            case ORDERING:
                if (null == fragmentOrdering) {
                    fragmentOrdering = new OrderingFragment(mFinishingOrders);
                    transaction.add(R.id.container, fragmentOrdering);
                } else {
                    transaction.show(fragmentOrdering);
                }
                break;
            case ORDERED:
                if (null == fragmentOrdered) {
                    fragmentOrdered = new OrderedFragment(mFinishedOrders);
                    transaction.add(R.id.container, fragmentOrdered);
                } else {
                    transaction.show(fragmentOrdered);
                }
                break;
        }
        transaction.commit();
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
                .append("/Car/getOrderListById.jsp");
        Log.d("orders", mUrl.toString());
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        WidgetUtils.showProgressDialog(mContext, null, "订单获取中", true);
        String url = urlSB.toString();
        int userId = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);

        RequestBody formBody = new FormEncodingBuilder()
                .add("id", String.valueOf(userId))
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
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                GOrderList gOrderList = mGson.fromJson(jsonStr, GOrderList.class); // 通过gson解析json
                Message msg = new Message();
                int status = gOrderList.status;
                Log.d("orders", gOrderList.orders.size() + "--");
                if (status == 0) {
                    msg.what = MSG_OK;
                    msg.obj = gOrderList;
                } else
                    msg.what = MSG_FAIL;
                mHandler.sendMessage(msg);
            }
        });
    }

    private static class GOrderList {
        List<GOrder> orders;
        int status;
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fragmentOrdered != null) {
            transaction.hide(fragmentOrdered);
        }
        if (fragmentOrdering != null) {
            transaction.hide(fragmentOrdering);
        }
    }

    /**
     * 筛选优惠券
     */
    private void filterCoupon(GOrderList gOrderList) {
        List<GOrder> gOrders = gOrderList.orders;
        // 逆序排列
        Collections.reverse(gOrders);
        for (GOrder gOrder : gOrders) {
            Log.d("orders", gOrder.getId() + "");
            int orderStatus = gOrder.getOrderStatus();
            if (orderStatus < 2)
                mFinishingOrders.add(gOrder);
            else
                mFinishedOrders.add(gOrder);
        }
    }
}
