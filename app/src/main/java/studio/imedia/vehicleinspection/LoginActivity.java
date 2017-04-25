package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import studio.imedia.vehicleinspection.net.RetrofitUtils;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.retrofitbean.CarBean;
import studio.imedia.vehicleinspection.retrofitbean.response.LoginResponse;
import studio.imedia.vehicleinspection.retrofitbean.UserBean;
import studio.imedia.vehicleinspection.utils.WidgetUtils;
import studio.imedia.vehicleinspection.utils.proxy.SPProxy;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.et_phone_num)
    EditText etPhoneNum;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_login)
    Button btnLogin;

    private Context mContext;

    public CarBean mCarBean;
    public UserBean mUserBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        // 初始化视图
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        // 初始化Toolbar
        initToolbar();
        // 初始化上次登录账号
        initAccount();
        // 初始化登录按钮使能状态
        WidgetUtils.enableButtonByEditText(btnLogin, etPhoneNum, etPassword);
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tvTitle.setText(TITLE_CAR_INSPECTION);
    }

    /**
     * 初始化上次登录的账号
     */
    private void initAccount() {
        String phoneNum = SPProxy.getLoginPhone(mContext);
        if (phoneNum == null || phoneNum.equals(""))
            return;
        etPhoneNum.setText(phoneNum);
    }

    @Override
    protected Context initContext() {
        mContext = LoginActivity.this;
        return mContext;
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
        if (intent == null) {
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }

        String from = bundle.getString(Constant.Key.FROM);
        if (TextUtils.equals(Constant.Activity.REGISTER, from)) {
            String phone = bundle.getString(Constant.Key.PHONE);
            String password = bundle.getString(Constant.Key.PASSWORD);
            etPhoneNum.setText(phone);
            etPassword.setText(password);
        }
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {

        final String phone = etPhoneNum.getText().toString();
        String password = etPassword.getText().toString();

        RetrofitUtils.getInstance()
                .baseUrl(Constant.Net.BASE_URL_DEFAULT)
                .login(phone, password, new Subscriber<LoginResponse>() {
                    @Override
                    public void onCompleted() {
                        unsubscribe();
                    }

                    @Override
                    public void onError(Throwable e) {
                        unsubscribe();
                        Toast.makeText(mContext, CONNECT_FAILED, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(LoginResponse response) {
                        int status = response.getStatus();
                        if (status == Constant.Status.OK) {
                            mCarBean = response.getCar();
                            mUserBean = response.getUser();

                            String phoneNum = etPhoneNum.getText().toString();
                            SPProxy.saveLoginPhone(mContext, phoneNum);
                            Toast.makeText(mContext, LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putString(Constant.Key.FROM, Constant.Activity.LOGIN);
                            bundle.putParcelable(Constant.Key.PARCELABLE_CAR_BEAN, mCarBean);
                            bundle.putParcelable(Constant.Key.PARCELABLE_USER_BEAN, mUserBean);
                            activityJump(MainActivity.class);
                            finish();
                        } else {
                            Toast.makeText(mContext, LOGIN_FAILED, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
