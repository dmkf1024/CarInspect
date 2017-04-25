package studio.imedia.vehicleinspection.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import butterknife.OnItemClick;
import studio.imedia.vehicleinspection.MainActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CountyFragment extends Fragment {

    @BindView(R.id.county_list)
    ListView lvCounty;
    private DrawerLayout mDrawerLayout;

    private int mCityId = 2;
    private String mIp;
    private String mPort;
    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GCountyList mGCountyList = null;
    private List<String> mCountyList;
    private ArrayAdapter mAdapter;

    private boolean isLoaded = false;

    private static final int MSG_OK = 0;
    private static final int MSG_FAIL = 1;
    private static final int CONNECT_FAIL = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    WidgetUtils.hideProgressDialog();
                    mGCountyList = (GCountyList) msg.obj;
                    setAdapter(); // 设置适配器
                    break;
                case MSG_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(getActivity(), "获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public CountyFragment() {
        // Required empty public constructor
        Log.d("city", "empty constructor");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCityId = (int) SPUtil.get(getActivity(), Constant.Key.USER_CITY_ID,
                Constant.Type.INTEGER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_county, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnItemClick(R.id.county_list)
    void onItemClick(View v, int position) {
        // 从preferences中取出城市名称、城市id
        String cityName = (String) SPUtil.get(getActivity(),
                Constant.Key.USER_CITY_NAME_TMP, Constant.Type.STRING);
        int cityId = (int) SPUtil.get(getActivity(),
                Constant.Key.USER_CITY_ID_TMP, Constant.Type.INTEGER);

        // 获取区ID，并存入preferences
        int countyId = mGCountyList.county.get(position).id;
//                MySharedPreferencesUtils.save(getActivity(), StaticValues.KEY_USER_COUNTY_ID, countyId);
        // 获取市区名称，并存入preferences
        String countyName = mCountyList.get(position);
//                MySharedPreferencesUtils.save(getActivity(), StaticValues.KEY_USER_COUNTY_NAME, countyName);
        // 拼接成城市名+市区名，跳转传值给CarInfoFragment
        String cityCounty = cityName + countyName;

        Intent intent = new Intent(getActivity(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.Key.USER_CITY_ID, cityId);
        bundle.putInt(Constant.Key.USER_COUNTY_ID, countyId);
        bundle.putString(Constant.Key.USER_CITY_COUNTY, cityCounty);
        intent.putExtras(bundle);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.container);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (isLoaded == false) {
                    isLoaded = true;
                    WidgetUtils.showProgressDialog(getActivity(), null, "加载中...", true);
                    initUrl(); // 初始化url
                    getData(mUrl); // 获取数据
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                reset();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        mIp = (String) SPUtil.get(getActivity(), Constant.Key.URL_IP,
                Constant.Type.STRING);
        mPort = (String) SPUtil.get(getActivity(), Constant.Key.URL_PORT,
                Constant.Type.STRING);

        mUrl.append("http://");
        mUrl.append(mIp);
        mUrl.append(":");
        mUrl.append(mPort);
        mUrl.append("/Car/getCounty.jsp");
    }

    /**
     * 获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();
        mCityId = (int) SPUtil.get(getActivity(), Constant.Key.USER_CITY_ID,
                Constant.Type.INTEGER);
        Log.d("city", "the cityid got is " + mCityId);
        // TODO 做cityId的异常处理
        RequestBody formBody = new FormEncodingBuilder()
                .add("locationid", String.valueOf(mCityId))
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
                // TODO 解析json
                parseJsonByGson(jsonStr); // 解析json
            }
        });
    }

    private void parseJsonByGson(String jsonStr) {
        GCountyList gCountyList = mGson.fromJson(jsonStr, GCountyList.class);
        Message msg = new Message();
        msg.what = gCountyList.status;
        if (msg.what == MSG_OK)
            msg.obj = gCountyList;
        mHandler.sendMessage(msg);
    }

    static class GCountyList {
        List<GCounty> county;
        int status;
    }

    static class GCounty {
        String name;
        int pid;
        int id;
    }

    /**
     * 设置适配器
     */
    private void setAdapter() {
        if (mCountyList == null)
            mCountyList = new ArrayList<>();
        List<GCounty> countyList = mGCountyList.county;
        for (GCounty gCounty : countyList)
            mCountyList.add(gCounty.name);


        if (mAdapter == null)
            mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, mCountyList);
        lvCounty.setAdapter(mAdapter);
    }

    /**
     * 重置
     */
    private void reset() {
        if (mCountyList != null && lvCounty != null) {
            mCountyList.clear();
            lvCounty.postInvalidate();
        }
        isLoaded = false;
    }
}
