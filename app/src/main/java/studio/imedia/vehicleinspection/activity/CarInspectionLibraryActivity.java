package studio.imedia.vehicleinspection.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyCarPartAdapter;
import studio.imedia.vehicleinspection.gbean.GPart;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;

public class CarInspectionLibraryActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.lv_car_inspection)
    ListView lvCarInspection;

    private Context mContext = CarInspectionLibraryActivity.this;

    private StringBuffer mUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private Gist mGist;

    private MyCarPartAdapter mAdapter;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private static final int CONNECT_OUT = 0x03;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    WidgetUtils.hideProgressDialog();
                    mGist = (Gist) msg.obj;
                    setAdapter(mGist); // 设置适配器
                    break;
                case MSG_FAIL:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "数据获取失败", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_OUT:
                    WidgetUtils.hideProgressDialog();
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_inspection_library);
        ButterKnife.bind(this);

        WidgetUtils.showProgressDialog(mContext, null, "加载中...", true);
        initToolbar(); // 初始化toolbar
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

        mTitle.setText(getString(R.string.title_car_inspection_library));
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);

        mUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port)
                .append("/Car/getPartList.jsp");
    }

    /**
     * 从接口中获取数据
     */
    private void getData(StringBuffer urlSB) {
        String url = urlSB.toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                mHandler.sendEmptyMessage(CONNECT_OUT);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    WidgetUtils.hideProgressDialog();
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                try {
                    int status = new JSONObject(jsonStr).getInt("status");
                    if (status == 0) {
                        Gist gist = mGson.fromJson(jsonStr, Gist.class);
                        Message msg = new Message();
                        msg.what = MSG_OK;
                        msg.obj = gist;
                        mHandler.sendMessage(msg);
                    } else {
                        mHandler.sendEmptyMessage(MSG_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class Gist {
        List<GPart> parts;
        int status;
    }

    private void setAdapter(Gist gist) {
        List<GPart> partList = gist.parts;
        if (mAdapter == null)
            mAdapter = new MyCarPartAdapter(mContext, partList);
        lvCarInspection.setAdapter(mAdapter);
    }
}
