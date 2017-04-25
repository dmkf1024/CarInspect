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

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import studio.imedia.vehicleinspection.CityActivity;
import studio.imedia.vehicleinspection.MainActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.SelectCarBrandActivity;
import studio.imedia.vehicleinspection.SelectCarTypeActivity;
import studio.imedia.vehicleinspection.net.RetrofitUtils;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.retrofitbean.CarBean;
import studio.imedia.vehicleinspection.retrofitbean.UserBean;
import studio.imedia.vehicleinspection.retrofitbean.response.BaseResponse;
import studio.imedia.vehicleinspection.utils.PicUtils;
import studio.imedia.vehicleinspection.utils.WidgetUtils;
import studio.imedia.vehicleinspection.utils.proxy.SPProxy;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarInfoFragment extends BaseFragment {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.img_driver_license)
    ImageView imgDriverLicense;
    @BindView(R.id.driver_license)
    RelativeLayout layoutDriverLicense;
    @BindView(R.id.et_car_plate_num)
    EditText etCarPlateNum;
    @BindView(R.id.tv_tip_car_brand)
    TextView tvTipCarBrand;
    @BindView(R.id.tv_car_brand)
    TextView tvCarBrand;
    @BindView(R.id.layout_car_brand)
    RelativeLayout layoutCarBrand;
    @BindView(R.id.tv_tip_car_type)
    TextView tvTipCarType;
    @BindView(R.id.tv_car_type)
    TextView tvCarType;
    @BindView(R.id.layout_car_type)
    RelativeLayout layoutCarType;
    @BindView(R.id.et_car_frame_num)
    EditText etCarFrameNum;
    @BindView(R.id.et_engine_num)
    EditText etEngineNum;
    @BindView(R.id.tv_register_date)
    TextView tvRegisterDate;
    @BindView(R.id.layout_register_date)
    LinearLayout layoutRegisterDate;
    @BindView(R.id.tv_tip_city)
    TextView tvTipCity;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.layout_city)
    RelativeLayout layoutCity;
    @BindView(R.id.btn_save)
    Button btnSave;

    private Context mContext;

    private Bitmap mLicensePic;
    private String mImgFileName;

    private String mCarBrand;
    private String mCarType;
    private int mBrandId;
    private int mTypeId;

    private String mProvinceName = "浙江";
    private String mCityName;
    private String mCountyName;
    private String mDetailedAddress = "";

    private int mProvinceId = 0;
    private int mCityId;
    private int mCountyId;

    // 提交的车辆信息
    private String mLicensePicPath;

    private StringBuffer mBaseUrl = new StringBuffer();
    private StringBuffer mUpPicUrl = new StringBuffer();
    private StringBuffer mUpInfoUrl = new StringBuffer();

    private boolean isLicenseUpdate; // 行驶证图片是否有更新

    private static final int CAMERA = 0;
    private static final int LOCAL_PHOTO = 1;

    private static final int REQUEST_GALLERY_AFTER_KITKAT = 0x01;
    private static final int REQUEST_GALLERY_BEFORE_KITKAT = 0X02;
    private static final int REQUEST_CAMERA = 0x03;

    private static final String IMG_LICENSE_FILE_NAME = "license.jpeg";

    public CarInfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initToolbar(); // 初始化toolbar
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(mToolbar);
        mTitle.setText(TITLE_CAR_INFO);
    }

    @Override
    protected Context initContext() {
        mContext = getActivity();
        return mContext;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (SPProxy.isOnline(mContext)) {
            initView();
        } else {// 退出登录后，清除界面
            clearView();
        }
        // 获取车系车型有关的信息并同步
        getBundle();
    }

    /**
     * 初始化车辆信息
     */
    private void initView() {
        CarBean carBean = MainActivity.mCarBean;
        UserBean userBean = MainActivity.mUserBean;
        String carPlateNum = carBean.getCarPlateNum();
        String carBrand = carBean.getCarBrand();
        String carType = carBean.getCarType();
        String carFrameNum = carBean.getCarFrameNum();
        String engineNum = carBean.getEngineNum();
        String registerTime = carBean.getRegisterTime();
        String province = userBean.getProvinceName();
        String city = userBean.getCityName();

        // 将数据呈现到界面上
        // TODO: 行驶证图片
        etCarPlateNum.setText(carPlateNum);
        etCarPlateNum.setSelection(etCarPlateNum.getText().toString().length());
        tvCarBrand.setText(carBrand);
        tvCarType.setText(carType);
        etCarFrameNum.setText(carFrameNum);
        etEngineNum.setText(engineNum);
        tvRegisterDate.setText(registerTime);
        tvCity.setText(province + city);
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
     * 获取车系车型有关的信息
     */
    private void getBundle() {
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        String from = bundle.getString(Constant.Key.FROM);
        if (TextUtils.equals(from, Constant.Activity.SELECT_CAR_BRAND)) {
            mCarBrand = bundle.getString(Constant.Key.CAR_BRAND_NAME);
            mBrandId = bundle.getInt(Constant.Key.CAR_BRAND_ID);
            tvCarBrand.setText(mCarBrand);
        } else if (TextUtils.equals(from, Constant.Activity.SELECT_CAR_TYPE)) {
            mCarType = bundle.getString(Constant.Key.CAR_TYPE_NAME);
            mTypeId = bundle.getInt(Constant.Key.CAR_TYPE_ID);
            tvCarType.setText(mCarBrand);
        } else if (TextUtils.equals(from, Constant.Activity.SELECT_CITY)) {
            mCityName = bundle.getString(Constant.Key.USER_CITY_NAME);
            mCityId = bundle.getInt(Constant.Key.USER_CITY_ID);
            mCountyName = bundle.getString(Constant.Key.USER_COUNTY_NAME);
            mCountyId = bundle.getInt(Constant.Key.USER_COUNTY_ID);
            tvCity.setText(mCityName + mCountyName);
        }
    }

    /**
     * 初始化url
     */
    private void initUrl() {

        // 上传信息url
        mUpInfoUrl.append(mBaseUrl);
        mUpInfoUrl.append("/Car/updateCarInfo.jsp");

        // 上传图片url
        mUpPicUrl.append(mBaseUrl);
        mUpPicUrl.append("/Car/uploadFile.jsp");
    }

    @OnClick({R.id.driver_license, R.id.layout_car_brand, R.id.layout_car_type, R.id.layout_register_date, R.id.layout_city, R.id.btn_save})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.driver_license:
                // 弹出框，显示选择上传相册图片或拍照
                showAlertDialog();
                break;
            case R.id.layout_car_brand:
                activityJump(SelectCarBrandActivity.class);
                break;
            case R.id.layout_car_type:
                if (!WidgetUtils.isTextViewEmpty(tvCarBrand)) {
                    activityJump(SelectCarTypeActivity.class, Constant.Key.CAR_BRAND_ID, mTypeId);
                } else {
                    Toast.makeText(getActivity(), SELECT_CAR_BRAND_FIRST, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout_register_date:
                WidgetUtils.pickerDialogToTextView(getActivity(), tvRegisterDate,
                        WidgetUtils.Type.DATE_PICKER_DIALOG, false);
                break;
            case R.id.layout_city:
                activityJump(CityActivity.class);
                break;
            case R.id.btn_save:
                if (SPProxy.isOnline(mContext)) {
                    // 保存
                    save();
                } else {
                    Toast.makeText(getActivity(), LOGIN_FIRST, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 保存信息
     */
    private void save() {
        // 获取爱车最新信息
        String userId = SPProxy.getUserId(mContext) + "";
        String engineNum = etEngineNum.getText().toString().trim();
        String registerTime = tvRegisterDate.toString().trim();
        String detailedAddress = tvCity.getText().toString().trim();

        RetrofitUtils.getInstance()
                .baseUrl(Constant.Net.BASE_URL_DEFAULT)
                .updateCarInfo(userId, mBrandId, mTypeId, engineNum, registerTime, mProvinceId,
                        mCityId, mCountyId, mDetailedAddress, new Subscriber<BaseResponse>() {
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
                            public void onNext(BaseResponse response) {
                                int status = response.getStatus();
                                if (status == Constant.Status.OK) {
                                    Toast.makeText(mContext, SAVE_SUCCESS, Toast.LENGTH_SHORT).show();
                                    // 同步本地数据
                                    syncLocalData();
                                }
                            }
                        });

        // TODO: 集成七牛云
    }

    /**
     * 同步本地数据
     */
    private void syncLocalData() {
        MainActivity.mCarBean.setCarBrand(mCarBrand);
        MainActivity.mCarBean.setCarType(mCarType);
        MainActivity.mUserBean.setProvince(mProvinceId);
        MainActivity.mUserBean.setCity(mCityId);
        MainActivity.mUserBean.setCounty(mCountyId);
        MainActivity.mUserBean.setProvinceName(mProvinceName);
        MainActivity.mUserBean.setCityName(mCityName);
        MainActivity.mUserBean.setCountyName(mCountyName);
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
                    imgDriverLicense.setImageBitmap(PicUtils.fitView(mLicensePic, imgDriverLicense));
                    isLicenseUpdate = true;
                } else {
                    Toast.makeText(getActivity(), "未检测到SD卡", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GALLERY_BEFORE_KITKAT:
            case REQUEST_GALLERY_AFTER_KITKAT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mImgFileName = PicUtils.getDataColumn(getActivity().getApplicationContext(),
                            data.getData(), null, null); // 得到文件名
                    mLicensePic = BitmapFactory.decodeFile(mImgFileName);
                    imgDriverLicense.setImageBitmap(PicUtils.fitView(mLicensePic, imgDriverLicense));
                    isLicenseUpdate = true;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
