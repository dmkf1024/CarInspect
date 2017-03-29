package studio.imedia.vehicleinspection.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import studio.imedia.vehicleinspection.CityActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.SelectCarTypeActivity;
import studio.imedia.vehicleinspection.SelectCarBrandActivity;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MyPicUtils;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarInfoFragment extends Fragment implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView mTitle;

    private RelativeLayout layoutDriverLicense;
    private ImageView imgDriverLicense;
    private RelativeLayout layoutCarSeries;
    private RelativeLayout layoutCarType;
    private RelativeLayout layoutCity;
    private LinearLayout layoutRegisterDate;
    private EditText etCarPlateNum;
    private TextView tvCarBrand;
    private TextView tvCarType;
    private EditText etCarFrameNum;
    private EditText etEngineNum;
    private TextView tvRegisterDate;
    private TextView tvCity;
    private Button btnSave;

    private Bitmap mLicensePic;
    private String mImgFileName;

    // 提交的车辆信息
    private int mId;
    private String mLicensePicPath;
    private int mCarBrandId;
    private int mCarTypeId;
    private String mEngineNum;
    private String mRegisterTime;
    private int mProvinceId;
    private int mCityId;
    private int mCountyId;
    private String mDetailedAddress;

    private StringBuffer mBaseUrl = new StringBuffer();
    private StringBuffer mUpPicUrl = new StringBuffer();
    private StringBuffer mUpInfoUrl = new StringBuffer();

    private boolean isLogin; // 登录状态
    private boolean isInfoInited; // 判断个人信息是否初始化
    private boolean isLicenseUpdate; // 行驶证图片是否有更新

    private final OkHttpClient mClient = new OkHttpClient();

    private static final int CAMERA = 0;
    private static final int LOCAL_PHOTO = 1;

    private static final int REQUEST_GALLERY_AFTER_KITKAT = 0x01;
    private static final int REQUEST_GALLERY_BEFORE_KITKAT = 0X02;
    private static final int REQUEST_CAMERA = 0x03;

    private static final String IMG_LICENSE_FILE_NAME = "license.jpeg";

    private static final int MSG_UP_PIC_SUCCESS = 0x00;
    private static final int MSG_UP_PIC_FAIL = 0x01;
    private static final int MSG_UP_INFO_SUCCESS = 0x02;
    private static final int MSG_UP_INFO_FAIL = 0x03;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UP_PIC_SUCCESS:
                    mLicensePicPath = (String) msg.obj;
                    uploadCarInfo(mUpInfoUrl, mLicensePicPath); // 上传爱车信息
                    break;
                case MSG_UP_PIC_FAIL:
                    Toast.makeText(getActivity(), "行驶证图片上传失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UP_INFO_SUCCESS:
                    Toast.makeText(getActivity(), "爱车信息保存成功", Toast.LENGTH_SHORT).show();
                    saveCarInfo(); // 保存爱车信息在本地preferences
                    updateCarInfo(); // 在界面上更新爱车信息
                    break;
                case MSG_UP_INFO_FAIL:
                    Toast.makeText(getActivity(), "信息上传失败，请稍候重试", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public CarInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_info, container, false);
        initToolbar(view); // 初始化toolbar
        findView(view); // 关联控件
        return view;
    }

    private void initToolbar(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_car_info));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initEvent(); // 监听事件回调
        initUrl(); // 初始化url
    }

    @Override
    public void onResume() {
        super.onResume();
        isLogin = (boolean) MySharedPreferencesUtils.get(getActivity(),
                StaticValues.KEY_LOGIN_STATE, StaticValues.TYPE_BOOLEAN);
        getBundle(); // 获取传值
        if (isLogin && isInfoInited == false) {
            isInfoInited = true;
            updateCarInfo();
        }
        if (!isLogin && isInfoInited) { // 退出登录后，清除界面
            isInfoInited = false;
            clearView();
        }
    }

    /**
     * 初始化车辆信息
     */
    private void updateCarInfo() {
        Context context = getActivity();
        // 从preferences里面取出数据
        mId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_ID, StaticValues.TYPE_INTEGER);
        mLicensePicPath = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_LICENSE_PIC,
                StaticValues.TYPE_STRING);
        String carPlateNum = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_PLATE_NUM,
                StaticValues.TYPE_STRING);
        String carBrand = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_BRAND_NAME,
                StaticValues.TYPE_STRING);
        mCarBrandId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_BRAND_ID,
                StaticValues.TYPE_INTEGER);
        String carType = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_TYPE_NAME,
                StaticValues.TYPE_STRING);
        mCarTypeId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_TYPE_ID,
                StaticValues.TYPE_INTEGER);
        String carFrameNum = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_FRAME_NUM,
                StaticValues.TYPE_STRING);
        mEngineNum = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_ENGINE_NUM,
                StaticValues.TYPE_STRING);
        mRegisterTime = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_CAR_REGISTER_TIME,
                StaticValues.TYPE_STRING);
        String province = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_PROVINCE_NAME,
                StaticValues.TYPE_STRING);
        mProvinceId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_PROVINCE_ID,
                StaticValues.TYPE_INTEGER);
        String city = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_CITY_NAME,
                StaticValues.TYPE_STRING);
        mCityId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_CITY_ID,
                StaticValues.TYPE_INTEGER);
        String county = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_COUNTY_NAME,
                StaticValues.TYPE_STRING);
        mCountyId = (int) MySharedPreferencesUtils.get(context, StaticValues.KEY_USER_COUNTY_ID,
                StaticValues.TYPE_INTEGER);

        // 将数据呈现到界面上
        // TODO 行驶证图片
        etCarPlateNum.setText(carPlateNum);
        etCarPlateNum.setSelection(etCarPlateNum.getText().toString().length());
        tvCarBrand.setText(carBrand);
        tvCarType.setText(carType);
        etCarFrameNum.setText(carFrameNum);
        etEngineNum.setText(mEngineNum);
        tvRegisterDate.setText(mRegisterTime);
        tvCity.setText(province + city);
    }

    /**
     * 将爱车信息保存至preferences
     */
    private void saveCarInfo() {
        Context context = getActivity();
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_LICENSE_PIC, mLicensePicPath);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_BRAND_ID, mCarBrandId);
        String carBrandName = tvCarBrand.getText().toString().trim();
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_BRAND_NAME, carBrandName);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_TYPE_ID, mCarTypeId);
        String carTypeName = tvCarType.getText().toString().trim();
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_TYPE_NAME, carTypeName);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_ENGINE_NUM, mEngineNum);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_CAR_REGISTER_TIME, mRegisterTime);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_USER_PROVINCE_ID, mProvinceId);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_USER_CITY_ID, mCityId);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_USER_COUNTY_ID, mCountyId);
        MySharedPreferencesUtils.save(context, StaticValues.KEY_USER_DETAILED_ADDRESS, mDetailedAddress);
    }

    /**
     * 关联控件
     */
    private void findView(View view) {
        layoutDriverLicense = (RelativeLayout) view.findViewById(R.id.driver_license);
        imgDriverLicense = (ImageView) view.findViewById(R.id.img_driver_license);

        layoutCarSeries = (RelativeLayout) view.findViewById(R.id.layout_car_brand);
        layoutCarType = (RelativeLayout) view.findViewById(R.id.layout_car_type);
        layoutRegisterDate = (LinearLayout) view.findViewById(R.id.layout_register_date);
        layoutCity = (RelativeLayout) view.findViewById(R.id.layout_city);

        etCarPlateNum = (EditText) view.findViewById(R.id.et_car_plate_num);
        tvCarBrand = (TextView) view.findViewById(R.id.tv_car_brand);
        tvCarType = (TextView) view.findViewById(R.id.tv_car_type);
        etCarFrameNum = (EditText) view.findViewById(R.id.et_car_frame_num);
        etEngineNum = (EditText) view.findViewById(R.id.et_engine_num);
        tvRegisterDate = (TextView) view.findViewById(R.id.tv_register_date);
        tvCity = (TextView) view.findViewById(R.id.tv_city);

        btnSave = (Button) view.findViewById(R.id.btn_save);
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        layoutDriverLicense.setOnClickListener(this);
        layoutCarSeries.setOnClickListener(this);
        layoutCarType.setOnClickListener(this);
        layoutRegisterDate.setOnClickListener(this);
        layoutCity.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        Context context = getActivity();
        String ip = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_URL_IP, StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(context, StaticValues.KEY_URL_PORT, StaticValues.TYPE_STRING);

        // 基本url
        mBaseUrl.append("http://");
        mBaseUrl.append(ip);
        mBaseUrl.append(":");
        mBaseUrl.append(port);

        // 上传信息url
        mUpInfoUrl.append(mBaseUrl);
        mUpInfoUrl.append("/Car/updateCarInfo.jsp");

        // 上传图片url
        mUpPicUrl.append(mBaseUrl);
        mUpPicUrl.append("/Car/uploadFile.jsp");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver_license:
                // 弹出框，显示选择上传相册图片或拍照
                showAlertDialog();
                break;
            case R.id.layout_car_brand:
                startActivity(new Intent(getActivity(), SelectCarBrandActivity.class));
                break;
            case R.id.layout_car_type:
                if (!MyWidgetUtils.isTextViewEmpty(tvCarBrand)) {
                    Intent intent = new Intent(getActivity(), SelectCarTypeActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(StaticValues.KEY_CAR_BRAND_ID, mCarBrandId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "请先选择品牌车系", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_register_date:
                MyWidgetUtils.pickerDialogToTextView(getActivity(), tvRegisterDate,
                        MyWidgetUtils.Type.DATE_PICKER_DIALOG, false);
                break;
            case R.id.layout_city:
                startActivity(new Intent(getActivity(), CityActivity.class));
                break;
            case R.id.btn_save:
                save(); // 保存信息
                break;
        }
    }

    /**
     * 获取传值
     */
    private void getBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (null != bundle) {
            // 选择车系
            String carBrand = bundle.getString(StaticValues.KEY_CAR_BRAND_NAME);
            mCarBrandId = bundle.getInt(StaticValues.KEY_CAR_BRAND_ID);
            if (null != carBrand && !TextUtils.isEmpty(carBrand)) {
                tvCarBrand.setText(carBrand);
                tvCarType.setText("");
            }

            // 选择车型
            String carType = bundle.getString(StaticValues.KEY_CAR_TYPE_NAME);
            mCarTypeId = bundle.getInt(StaticValues.KEY_CAR_TYPE_ID);
            if (null != carType && !TextUtils.isEmpty(carType)) {
                tvCarType.setText(carType);
            }

            // 常住城市
            mCityId = bundle.getInt(StaticValues.KEY_USER_CITY_ID);
            mCountyId = bundle.getInt(StaticValues.KEY_USER_COUNTY_ID);
            String cityCounty = bundle.getString(StaticValues.KEY_USER_CITY_COUNTY);
            if (cityCounty != null && !TextUtils.isEmpty(cityCounty)) {
                tvCity.setText(cityCounty);
            }
        }
    }

    /**
     * 清除界面数据
     */
    private void clearView() {
        etCarPlateNum.setText("");
        tvCarBrand.setText("");
        tvCarType.setText("");
        etCarFrameNum.setText("");
        etEngineNum.setText("");
        tvRegisterDate.setText("");
        tvCity.setText("");
    }

    /**
     * 保存信息
     */
    private void save() {
        if (isLogin) {
//            if (isLicenseUpdate)
//                uploadLicensePic(mUpPicUrl); // 先上传行驶证图片，再上传爱车信息
//            else
//                uploadCarInfo(mUpInfoUrl, null); // 上传爱车信息
            uploadCarInfo(mUpInfoUrl, null); // 上传爱车信息
        } else {
            Toast.makeText(getActivity(), "请先登录账户", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上传行驶证照片
     */
    private void uploadLicensePic(StringBuffer urlSB) {
        String url = urlSB.toString();
        String licensePic = null;
        // TODO 上传行驶证

        // TODO 假设已经上传成功
        Message msg = new Message();
        msg.what = MSG_UP_PIC_SUCCESS;
        // TODO 往message里面添加图片路径
        msg.obj = licensePic;
        mHandler.sendMessage(msg);
    }

    /**
     * 上传爱车信息
     */
    private void uploadCarInfo(StringBuffer urlSB, String picPath) {
        String url = urlSB.toString();
        // 获取爱车最新信息
        mLicensePicPath = picPath;
        mDetailedAddress = tvCity.getText().toString().trim();
        mEngineNum = etEngineNum.getText().toString().trim();
        mRegisterTime = tvRegisterDate.getText().toString().trim();

        // 上传爱车信息...
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder()
                .add("id", String.valueOf(mId))
                .add("carBrandId", String.valueOf(mCarBrandId))
                .add("carTypeId", String.valueOf(mCarTypeId))
                .add("engineNum", mEngineNum)
                .add("registerTime", mRegisterTime)
                .add("provinId", String.valueOf(mProvinceId)) // 省份id
                .add("cityId", String.valueOf(mCityId))
                .add("countyId", String.valueOf(mCountyId))
                .add("detailedAddress", mDetailedAddress);
        if (isLicenseUpdate && picPath != null)
            formEncodingBuilder.add("licensePic", mLicensePicPath);

        RequestBody formBody = formEncodingBuilder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getActivity(), "连接服务器失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                try {
                    int status = new JSONObject(jsonStr).getInt("status");
                    if (status == 0) {
                        mHandler.sendEmptyMessage(MSG_UP_INFO_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(MSG_UP_INFO_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示弹出框
     */
    private void showAlertDialog() {
        final String[] selections = {"拍照", "上传本地图片"};
        new AlertDialog.Builder(getActivity())
                .setTitle("上传行驶证")
                .setItems(selections, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case CAMERA:
                                // 拍照上传图片
                                selectPhotoFromCamera();
                                break;
                            case LOCAL_PHOTO:
                                // 本地上传图片
                                selectPhotoFromGallery();
                                break;
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    /**
     * 拍照上传图片
     */
    private void selectPhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 判断储存卡是否可用，存储照片文件
        if (hasSDCard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            IMG_LICENSE_FILE_NAME)));
        }
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    /**
     * 判断SD卡是否处于挂载状态
     */
    private boolean hasSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state))
            return true;
        return false;
    }

    /**
     * 从相册中上传图片
     * 根据系统版本4.4作为分界线
     */
    private void selectPhotoFromGallery() {
        final boolean isKitkat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitkat)
            selectAfterKitkat();
        else
            selectBeforeKitkat();
    }

    /**
     * 从4.4以上系统选择图片
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void selectAfterKitkat() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_AFTER_KITKAT);
    }

    /**
     * 从4.4以下系统选择图片
     */
    private void selectBeforeKitkat() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_GALLERY_BEFORE_KITKAT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    File tmpFile = new File(Environment.getExternalStorageDirectory(),
                            IMG_LICENSE_FILE_NAME);
                    mLicensePic = BitmapFactory.decodeFile(tmpFile.getPath());
                    imgDriverLicense.setImageBitmap(MyPicUtils.fitView(mLicensePic, imgDriverLicense));
                    isLicenseUpdate = true;
                } else {
                    Toast.makeText(getActivity(), "未检测到SD卡", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GALLERY_BEFORE_KITKAT:
            case REQUEST_GALLERY_AFTER_KITKAT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mImgFileName = MyPicUtils.getDataColumn(getActivity().getApplicationContext(),
                            data.getData(), null, null); // 得到文件名
                    mLicensePic = BitmapFactory.decodeFile(mImgFileName);
                    imgDriverLicense.setImageBitmap(MyPicUtils.fitView(mLicensePic, imgDriverLicense));
                    isLicenseUpdate = true;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private DrawerLayout mDrawerLayout;
//
//    /**
//     * 初始化监听事件
//     */
//    private void initListener() {
//        mDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
//        lvMenu.setOnItemClickListener(this);
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        switch (position) {
//            case 0: // 数据查看
//                // 如果点击的不是当前项，则跳转页面
//                if (DataActivity.class != getActivity().getClass()) {
//                    startActivity(new Intent(getActivity(), DataActivity.class));
//                    getActivity().finish();
//                }
//                // 如果点击的是当前项，则隐藏菜单
//                else {
//                    mDrawerLayout.closeDrawer(Gravity.LEFT);
//                }
//                break;
//            case 1: // 异常查看
//                // 如果点击的不是当前项，则跳转页面
//                if (ErrorActivity.class != getActivity().getClass()) {
//                    startActivity(new Intent(getActivity(), ErrorActivity.class));
//                    getActivity().finish();
//                }
//                // 如果点击的是当前项，则隐藏菜单
//                else {
//                    mDrawerLayout.closeDrawer(Gravity.LEFT);
//                }
//                break;
//            case 2: // 健康知识
//                // 如果点击的不是当前项，则跳转页面
//                if (HealthKnowledgeActivity.class != getActivity().getClass()) {
//                    startActivity(new Intent(getActivity(), HealthKnowledgeActivity.class));
//                    getActivity().finish();
//                }
//                // 如果点击的是当前项，则隐藏菜单
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//                break;
//            case 3: // 个性分析
//                // 如果点击的不是当前项，则跳转页面
//                if (AnalyseActivity.class != getActivity().getClass()) {
//                    startActivity(new Intent(getActivity(), AnalyseActivity.class));
//                    getActivity().finish();
//                }
//                // 如果点击的是当前项，则隐藏菜单
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//                break;
//            case 4: // 养老院
//                if (OrganizationActivity.class != getActivity().getClass()) {
//                    startActivity(new Intent(getActivity(), OrganizationActivity.class));
//                    getActivity().finish();
//                }
//                mDrawerLayout.closeDrawer(Gravity.LEFT);
//                break;
//        }
//    }
}
