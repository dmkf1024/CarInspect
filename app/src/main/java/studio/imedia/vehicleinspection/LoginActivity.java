package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import studio.imedia.vehicleinspection.gbean.GCar;
import studio.imedia.vehicleinspection.gbean.GUser;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private Button btnLogin;
    private EditText etPhoneNum;
    private EditText etPassword;
    private Button btnRegister;

    private Context mContext = LoginActivity.this;

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();

    private static final int MSG_LOGIN_SUCCESS = 0;
    private static final int MSG_LOGIN_FAIL = 1;

    private String mUrl;
    private String mIp;
    private String mPort;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGIN_SUCCESS:
                    Log.d("reg", "handler success");
                    String phoneNum = etPhoneNum.getText().toString();
                    rememberAccount(phoneNum); // 记住账号
                    Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(mContext, MainActivity.class));
                    finish();
                    break;
                case MSG_LOGIN_FAIL:
                    Log.d("reg", "handler fail");
                    Toast.makeText(mContext, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initToolbar(); // 初始化toolbar
        findView(); // 关联控件
        initAccount(); // 初始化上次登录账号
        initWidget(); // 初始化控件状态
        initUrl(); // 初始化url
        initEvent(); // 初始化监听事件
    }

    @Override
    protected void onResume() {
        super.onResume();
        getBundle(); // 获取传值
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /**
     * 获取传值
     */
    private void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String from = bundle.getString(Constant.Key.FROM);
            if (Constant.ACTIVITY_REGISTER.equals(from)) {
                String phone = bundle.getString(Constant.Key.PHONE);
                String password = bundle.getString(Constant.Key.PASSWORD);
                etPhoneNum.setText(phone);
                etPassword.setText(password);
            }
        }
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
        mTitle.setText(getString(R.string.title_car_inspection));
    }

    /**
     * 关联控件
     */
    private void findView() {
        btnLogin = (Button) findViewById(R.id.btn_login);
        etPhoneNum = (EditText) findViewById(R.id.et_phone_num);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    /**
     * 初始化控件状态
     */
    private void initWidget() {
        MyWidgetUtils.enableButtonByEditText(btnLogin, etPhoneNum, etPassword); // 初始化登录按钮使能状态
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        mIp = (String) SPUtil.get(mContext, Constant.Key.URL_IP,
                Constant.Type.STRING);
        mPort = (String) SPUtil.get(mContext, Constant.Key.URL_PORT,
                Constant.Type.STRING);
        mUrl = "http://" + mIp + ":" + mPort + "/Car/getInfo.jsp";
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                Log.d("reg", "onClick");
                login(mUrl); // 登录
                break;
            case R.id.btn_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
    }

    /**
     * 登录
     * @param url
     */
    private void login(String url) {
        Log.d("reg", "login");
        String phone = etPhoneNum.getText().toString();
        String password = etPassword.getText().toString();

        String urlGet = url + "?phone=" + phone + "&password=" + password;
        // 登录验证
        final Request request = new Request.Builder()
                .url(urlGet)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.d("reg", "onFailure");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.d("reg", "not success");
                    throw new IOException("Unexpected code " + response);
                }

                String jsonStr = response.body().string();
                Log.d("reg", "json " + jsonStr);
                getStatus(jsonStr); // 解析json
            }
        });
    }

    /**
     * 解析json数据
     * @param json
     */
    private void getStatus(String json) {
        Log.d("reg", "getStatus");
        try {
            JSONObject result = new JSONObject(json);
            int status = result.getInt("status");
            Message msg = new Message();
            if (Constant.Status.OK == status) {
                Log.d("reg", "status ok");
                GInfo gInfo = mGson.fromJson(json, GInfo.class); // GSON解析json数据
                GUser gUser = gInfo.user;
                GCar gCar = gInfo.car;
                saveDataIntoPreferences(gUser, gCar); // 将用户数据存入sharedPreferences里

                msg.what = MSG_LOGIN_SUCCESS;
                mHandler.sendMessage(msg);
            } else if (Constant.Status.FAIL == status) {
                Log.d("reg", "status fail");
                msg.what = MSG_LOGIN_FAIL;
                mHandler.sendMessage(msg);
            }
        } catch (JSONException e) {
            Log.d("reg", "exception");
            e.printStackTrace();
        }
    }



    static class GInfo {
        GCar car;
        GUser user;
    }

    /**
     * 将用户数据存入sharedPreferences里
     */
    private void saveDataIntoPreferences(GUser gUser, GCar gCar) {
        // 获取用户信息
        int id = gUser.getId();
        String name = gUser.getName();
        int score = gUser.getScore();
        String avatar = gUser.getAvatar();
        int gender = gUser.getGender();
        String signature = gUser.getSignature();
        int provinceId = gUser.getProvince();
        String provinceName = gUser.getProvinceName();
        int cityId = gUser.getCity();
        String cityName = gUser.getCityName();
        int countyId = gUser.getCounty();
        String countyName = gUser.getCountyName();
        String detailedAddress = gUser.getDetailedAddress();

        // 获取车辆信息
        String carBrand = gCar.getCarBrand();
        String carFrameNum = gCar.getCarFrameNum();
        String carPlateNum = gCar.getCarPlateNum();
        String carType = gCar.getCarType();
        String engineNum = gCar.getEngineNum();
        String licensePic = gCar.getLicensesPic();
        String registerTime = gCar.getRegisterTime();

        // 将用户信息存至preferences
        SPUtil.save(mContext, Constant.Key.USER_ID, id);
        SPUtil.save(mContext, Constant.Key.USER_NAME, name);
        SPUtil.save(mContext, Constant.Key.USER_AVATAR, avatar);
        SPUtil.save(mContext, Constant.Key.USER_GENDER, gender);
        SPUtil.save(mContext, Constant.Key.USER_SCORE, score);
        SPUtil.save(mContext, Constant.Key.USER_SIGNATURE, signature);
        SPUtil.save(mContext, Constant.Key.USER_PROVINCE_ID, provinceId);
        SPUtil.save(mContext, Constant.Key.USER_PROVINCE_NAME, provinceName);
        SPUtil.save(mContext, Constant.Key.USER_CITY_ID, cityId);
        SPUtil.save(mContext, Constant.Key.USER_CITY_NAME, cityName);
        SPUtil.save(mContext, Constant.Key.USER_COUNTY_ID, countyId);
        SPUtil.save(mContext, Constant.Key.USER_COUNTY_NAME, countyName);
        SPUtil.save(mContext, Constant.Key.USER_DETAILED_ADDRESS, detailedAddress);

        // 将车辆信息存至preferences
        SPUtil.save(mContext, Constant.Key.CAR_BRAND_NAME, carBrand);
        SPUtil.save(mContext, Constant.Key.CAR_FRAME_NUM, carFrameNum);
        SPUtil.save(mContext, Constant.Key.CAR_PLATE_NUM, carPlateNum);
        SPUtil.save(mContext, Constant.Key.CAR_TYPE_NAME, carType);
        SPUtil.save(mContext, Constant.Key.CAR_ENGINE_NUM, engineNum);
        SPUtil.save(mContext, Constant.Key.CAR_LICENSE_PIC, licensePic);
        SPUtil.save(mContext, Constant.Key.CAR_REGISTER_TIME, registerTime);

        // 保存登录状态
        SPUtil.save(mContext, Constant.Key.LOGIN_STATE, true);
    }

    /**
     * 记住账号
     */
    private void rememberAccount(String phoneNum) {
        SPUtil.save(mContext, Constant.Key.LOGIN_PHONE_NUM, phoneNum);
    }

    /**
     * 初始化上次登录的账号
     */
    private void initAccount() {
        String phoneNum = (String) SPUtil.get(mContext, Constant.Key.LOGIN_PHONE_NUM,
                Constant.Type.STRING);
        if (phoneNum == null || phoneNum.equals(""))
            return;
        etPhoneNum.setText(phoneNum);
    }
}
