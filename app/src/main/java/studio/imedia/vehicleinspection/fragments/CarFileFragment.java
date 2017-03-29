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

import studio.imedia.vehicleinspection.CarFileDetailActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyCarFileAdapter;
import studio.imedia.vehicleinspection.bean.CarFile;
import studio.imedia.vehicleinspection.gbean.GOrder;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarFileFragment extends Fragment implements AdapterView.OnItemClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private TextView tvNoLogin;

    private StringBuffer mUrl = new StringBuffer();

    private boolean isLogin;

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private Gist mGist;

    private ListView lvCarFiles;
    private List<CarFile> mCarFileList;
    private MyCarFileAdapter mAdapter;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    MyWidgetUtils.hideProgressDialog();
                    mGist = (Gist) msg.obj;
                    setAdapter(mGist); // 设置适配器
                    break;
                case MSG_FAIL:
                    MyWidgetUtils.hideProgressDialog();
                    Toast.makeText(getActivity(), "数据获取失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public CarFileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_file, container, false);
        initToolbar(view); // 初始化toolbar
        return view;
    }

    /**
     * 初始化toolbar
     * @param view
     */
    private void initToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_car_file));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView(); // 关联控件
        lvCarFiles.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        isLogin = (boolean) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_LOGIN_STATE,
                StaticValues.TYPE_BOOLEAN);
        if (isLogin) {
            MyWidgetUtils.showProgressDialog(getActivity(), null, "加载中...", true);
            MyWidgetUtils.showList(lvCarFiles, tvNoLogin);
            initUrl(); // 初始化url
            getData(mUrl); // 获取数据
        } else {
            MyWidgetUtils.hideList(lvCarFiles, tvNoLogin);
        }
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvCarFiles = (ListView) getActivity().findViewById(R.id.lv_car_file);
        tvNoLogin = (TextView) getActivity().findViewById(R.id.tv_no_login);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_URL_IP, StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_URL_PORT, StaticValues.TYPE_STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getOrderListById.jsp");
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        int userId = (int) MySharedPreferencesUtils.get(getActivity(), StaticValues.KEY_USER_ID, StaticValues.TYPE_INTEGER);
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
                MyWidgetUtils.hideProgressDialog();
                Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    MyWidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                Gist gist = mGson.fromJson(jsonStr, Gist.class);
                int status = gist.status;
                if (status == 0) {
                    Message msg = new Message();
                    msg.obj = gist;
                    msg.what = MSG_OK;
                    mHandler.sendMessage(msg);
                } else
                    mHandler.sendEmptyMessage(MSG_FAIL);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getActivity(), CarFileDetailActivity.class);
        Bundle bundle = new Bundle();

        GOrder gOrder = mGist.orders.get(position);
        int orderId = gOrder.getId();
        String date = ((TextView) view.findViewById(R.id.tv_date)).getText().toString();
        String station = gOrder.getName();
        int isPass = gOrder.getIsPass();

        bundle.putInt("order_id", orderId);
        bundle.putString("car_file_date", date);
        bundle.putString("car_file_station", station);
        bundle.putInt("car_file_is_pass", isPass);

        intent.putExtras(bundle);
        startActivity(intent);
    }

    private static class Gist {
        List<GOrder> orders;
        int status;
    }

    /**
     * 设置适配器
     */
    private void setAdapter(Gist gist) {
        List<GOrder> orderList = gist.orders;
        if (mCarFileList == null) {
            mCarFileList = new ArrayList<>();
        } else {
            mCarFileList.clear(); // 清空list
        }

        for (GOrder gOrder : orderList) {
            CarFile carFile = new CarFile();
            String orderTime = gOrder.getOrderTime();
            String year = orderTime.substring(0, orderTime.indexOf("-"));
            String month = orderTime.substring(orderTime.indexOf("-") + 1, orderTime.lastIndexOf("-"));
            String day = orderTime.substring(orderTime.lastIndexOf("-") + 1, orderTime.indexOf(" "));
            String date = year + "年" + month + "月" + day + "日";
            carFile.setDate(date);
            String stationName = gOrder.getName();
            carFile.setStation(stationName);
            int isPass = gOrder.getIsPass();
            if (isPass == 0)
                carFile.setState(false);
            else
                carFile.setState(true);
            mCarFileList.add(carFile);
        }

        if (mAdapter == null)
            mAdapter = new MyCarFileAdapter(getActivity(), mCarFileList);
        lvCarFiles.setAdapter(mAdapter);
    }

}


