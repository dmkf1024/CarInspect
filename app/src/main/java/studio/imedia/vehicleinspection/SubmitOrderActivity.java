package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import studio.imedia.vehicleinspection.adapters.MyOrderItemAdapter;
import studio.imedia.vehicleinspection.gbean.GOrderItem;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;
import studio.imedia.vehicleinspection.views.MyListView;
import studio.imedia.vehicleinspection.views.SwitchView;

public class SubmitOrderActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private TextView tvMaster;
    private TextView tvStation;
    private TextView tvDateTime;

    private RelativeLayout layoutProxyCost;
    private TextView tvCostSum;
    private TextView tvCostSumBottom;

    private SwitchView switchCoupon;

    private RelativeLayout layoutAlipay;
    private RelativeLayout layoutWechatPay;
    private ImageView imgCheckAlipay;
    private ImageView imgCheckWechatPay;
    private MyListView lvOrderItem;

    private Button btnConfirm;

    private boolean isProxy;
    private boolean isUseCoupon;
    private int mOrderId;
    private int mUserId;
    private int mMasterId;
    private int mStationId;
    private int mCouponId = 1;
    private String mMsgLeft;
    private int mPayType = 1;
    private String mOrderTime;

    private MyOrderItemAdapter mAdapter;

    private StringBuffer mSubmitUrl = new StringBuffer();
    private StringBuffer mItemsUrl = new StringBuffer();
    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GItemList mGItemList;

    private Context mContext = SubmitOrderActivity.this;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int MSG_GET_ITEM_OK = 0x03;
    private static final int MSG_GET_ITEM_FAIL = 0x04;
    private static final int CONNECT_FAIL = 0X05;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    MyWidgetUtils.hideProgressDialog();
                    // 跳转支付界面
                    mOrderId = (int) msg.obj;
                    Toast.makeText(mContext, "订单提交成功！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(mContext, PayActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constant.Key.ORDER_ID, mOrderId);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    startActivity(new Intent(mContext, PayActivity.class));
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "订单提交失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_GET_ITEM_OK:
                    MyWidgetUtils.hideProgressDialog();
                    mGItemList = (GItemList) msg.obj;
                    setAdapter(mGItemList); // 显示订单项目
                    initPriceSum(mGItemList);
                    break;
                case MSG_GET_ITEM_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "获取订单项目失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_order);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initUrl();
        getOrderItems(mItemsUrl); // 获取订单项目
        initEvent(); // 初始化监听事件
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBundle(); // 获取传值数据
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
        mTitle.setText(getString(R.string.title_sumbit_order));
    }

    /**
     * 关联控件
     */
    private void findView() {
        tvMaster = (TextView) findViewById(R.id.tv_master);
        tvStation = (TextView) findViewById(R.id.tv_station);
        tvDateTime = (TextView) findViewById(R.id.tv_date_time);
        layoutProxyCost = (RelativeLayout) findViewById(R.id.layout_proxy_cost);
        tvCostSum = (TextView) findViewById(R.id.tv_cost_sum);
        tvCostSumBottom = (TextView) findViewById(R.id.tv_cost_sum_bottom);
        switchCoupon = (SwitchView) findViewById(R.id.switch_coupon);

        layoutAlipay = (RelativeLayout) findViewById(R.id.layout_alipay);
        layoutWechatPay = (RelativeLayout) findViewById(R.id.layout_wechat_pay);
        imgCheckAlipay = (ImageView) findViewById(R.id.img_check_alipay);
        imgCheckWechatPay = (ImageView) findViewById(R.id.img_check_wechat_pay);
        btnConfirm = (Button) findViewById(R.id.btn_confirm);

        lvOrderItem = (MyListView) findViewById(R.id.lv_order_items);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

        StringBuffer baseUrl = new StringBuffer();
        baseUrl.append("http://");
        baseUrl.append(ip);
        baseUrl.append(":");
        baseUrl.append(port);

        mSubmitUrl.append(baseUrl);
        mSubmitUrl.append("/Car/saveOrder.jsp");

        mItemsUrl.append(baseUrl);
        mItemsUrl.append("/Car/getInspectItems.jsp");
    }

    /**
     * 获得订单项目
     */
    private void getOrderItems(StringBuffer urlSB) {
        MyWidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        String url = urlSB.toString();
        int userId = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);
        RequestBody formBody = new FormEncodingBuilder()
                .add("id", String.valueOf(userId))
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        Log.d(Constant.Tag.NET, "url is " + url + "; id " + userId);

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
               mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    MyWidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                GItemList gItemList = mGson.fromJson(jsonStr, GItemList.class);
                int status = gItemList.status;
                if (status == 0) {
                    Message msg = new Message();
                    msg.what = MSG_GET_ITEM_OK;
                    msg.obj = gItemList;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(MSG_GET_ITEM_FAIL);
                }
            }
        });
    }

    private static class GItemList {
        List<GOrderItem> items;
        int status;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutAlipay.setOnClickListener(this);
        layoutWechatPay.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        switchCoupon.setOnStateChangedListener(new SwitchView.OnStateChangedListener() {
            @Override
            public void toggleToOn(View view) {
                startActivity(new Intent(mContext, SelectCouponActivity.class));
            }

            @Override
            public void toggleToOff(View view) {

            }
        });
    }

    /**
     * 获取传值数据，并初始化view
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            String from = bundle.getString(Constant.Key.FROM);
            if (from == null || !Constant.ACTIVITY_SELECT_COUPON.equals(from)) { // 从上一界面跳转过来
                // 从上一层Activity返回的bundle
                isProxy = bundle.getBoolean(Constant.Key.PROXY_STATE);
                String station = bundle.getString(Constant.Key.INSPECT_STATION);
                String date = bundle.getString(Constant.Key.ORDER_DATE);
                String time = bundle.getString(Constant.Key.ORDER_TIME);

                // 将日期规范成：2015-11-25格式
                String formatDate = date.substring(0, date.indexOf("年")) + "-" +
                        date.substring(date.indexOf("年") + 1, date.indexOf("月")) + "-" +
                        date.substring(date.indexOf("月") + 1, date.indexOf("日"));
                // 将时间规范成：07:45:00或7:45:00格式
                String formatTime = time + ":00";
                // 时间戳格式：2015-11-25 12:34:00
                mOrderTime = formatDate + " " + formatTime;
                mStationId = bundle.getInt(Constant.Key.STATION_ID);

                if (isProxy) {
                    mMasterId = bundle.getInt(Constant.Key.MASTER_ID);
                    mMsgLeft = bundle.getString(Constant.Key.ORDER_MSG_LEFT);
                    String master = bundle.getString(Constant.Key.MASTER_NAME);
                    tvMaster.setText("代检人： " + master);
                    tvCostSum.setText("￥132.00");
                    tvCostSumBottom.setText("￥132.00");
                } else {
                    tvMaster.setVisibility(View.GONE);
                    layoutProxyCost.setVisibility(View.GONE);
                    tvCostSum.setText("￥80.00");
                    tvCostSumBottom.setText("￥80.00");
                }
                tvStation.setText("检查站： " + station);
                tvDateTime.setText("车检时间： " + date + " " + time);
            } else { // 从选择优惠券界面跳转过来
                isUseCoupon = bundle.getBoolean(Constant.Key.IS_USE_COUPON);
                if (isUseCoupon) {
                    mCouponId = bundle.getInt(Constant.Key.COUPON_ID);
                    switchCoupon.toggleSwitch(true);
                } else {
                    switchCoupon.toggleSwitch(false);
                    switchCoupon.setEnabled(true);
                }
            }
        } else { // 从选择优惠券界面直接回退，未返回值
            isUseCoupon = false;
            switchCoupon.toggleSwitch(false);
            switchCoupon.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_alipay:
                selectAlipay(); // 选择支付宝支付
                break;
            case R.id.layout_wechat_pay:
                selectWechatPay(); // 选择微信支付
                break;
            case R.id.btn_confirm:
                MyWidgetUtils.showProgressDialog(mContext, null, "订单提交中...", true);
                submitOrder(mSubmitUrl); // 提交订单
                break;
        }
    }

    /**
     * 选择支付宝支付
     */
    private void selectAlipay() {
        mPayType = 1;
        imgCheckAlipay.setVisibility(View.VISIBLE);
        imgCheckWechatPay.setVisibility(View.GONE);
    }

    /**
     * 选择微信支付
     */
    private void selectWechatPay() {
        mPayType = 2;
        imgCheckWechatPay.setVisibility(View.VISIBLE);
        imgCheckAlipay.setVisibility(View.GONE);
    }

    /**
     * 提交订单
     */
    private void submitOrder(StringBuffer urlSB) {
        String url = urlSB.toString();
        mUserId = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);
        String content = "id:" + mUserId + ", masterId:" + mMasterId + ", inspectStationsId:" +
                mStationId + ", couponId:" + mCouponId + ", message:" + mMsgLeft + ", payTYpe:" +
                mPayType + ", orderTIme:" + mOrderTime;

        Log.d("aaa", content);
        FormEncodingBuilder builder = new FormEncodingBuilder();
        Log.d("aaa", "builder");
        builder.add("id", String.valueOf(mUserId))
                .add("inspectStationId", String.valueOf(mStationId))
                .add("payType", String.valueOf(mPayType))
                .add("orderTime", mOrderTime);
        Log.d("aaa", "builder.add");
        if (isProxy) {
            builder.add("masterId", String.valueOf(mMasterId))
                    .add("message", mMsgLeft);
        }
        if (isUseCoupon) {
            builder.add("couponId", String.valueOf(mCouponId));
        }
        RequestBody formBody = builder.build();
        Log.d("aaa", "formBody");
        Log.d("aaa", "the url is " + url);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Log.d("aaa", "init request");
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_FAIL);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String resMsg = response.message();
                String result = response.body().string();
                Log.d("aaa", "the json is " + result);
                try {
                    int status = new JSONObject(result).getInt("status");
                    Log.d("aaa", "the status is " + status);
                    if (status == 0) {
                        JSONObject jsonObject = new JSONObject(result);
                        int orderId = jsonObject.getInt("orderId");
                        Message msg = new Message();
                        msg.obj = orderId;
                        msg.what = MSG_OK;
                        mHandler.sendMessage(msg);
                    } else
                        mHandler.sendEmptyMessage(MSG_FAIL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示订单项目
     */
    private void setAdapter(GItemList list) {
        List<GOrderItem> orderItemList = list.items;
        if (mAdapter == null)
            mAdapter = new MyOrderItemAdapter(mContext, orderItemList);
        lvOrderItem.setAdapter(mAdapter);
    }

    /**
     * 初始化价格总计
     */
    private void initPriceSum(GItemList list) {
        List<GOrderItem> itemList = list.items;
        float sum = 0;
        for (GOrderItem item : itemList) {
            sum += item.getPrice();
        }
        if (isProxy)
            sum += 52; // 加上代检费
        DecimalFormat df = new DecimalFormat(".00");
        String sumStr = df.format(sum);
        tvCostSum.setText("￥" + sumStr);
        tvCostSumBottom.setText("￥" + sumStr);
    }
}
