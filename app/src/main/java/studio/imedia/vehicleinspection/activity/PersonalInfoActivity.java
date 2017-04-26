package studio.imedia.vehicleinspection.activity;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.PicUtils;
import studio.imedia.vehicleinspection.utils.SPUtil;
import studio.imedia.vehicleinspection.utils.WidgetUtils;
import studio.imedia.vehicleinspection.views.RoundImageView;

public class PersonalInfoActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.right_icon)
    ImageView rightIcon;
    @BindView(R.id.app_bar)
    Toolbar mToolbar;
    @BindView(R.id.img_next_avatar)
    ImageView imgNextAvatar;
    @BindView(R.id.img_user_avatar)
    RoundImageView imgUserAvatar;
    @BindView(R.id.avatar)
    RelativeLayout layoutAvatar;
    @BindView(R.id.img_next_nickname)
    ImageView imgNextNickname;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.username)
    RelativeLayout layoutUsername;
    @BindView(R.id.img_next_gendar)
    ImageView imgNextGendar;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.gender)
    RelativeLayout layoutGender;
    @BindView(R.id.et_signature)
    EditText etSignature;
    @BindView(R.id.btn_save)
    Button btnSave;

    private String mAvatarPath;
    private String mUsername;
    private int mGender;    // 0-男  1-女
    private String mSignature;

    private File mAvatarFile;

    private static final int MALE = 0;
    private static final int FEMALE = 1;

    private static final int CAMERA = 0;
    private static final int LOCAL_PHOTO = 1;

    private static final int REQUEST_GALLERY_AFTER_KITKAT = 0x01;
    private static final int REQUEST_GALLERY_BEFORE_KITKAT = 0X02;
    private static final int REQUEST_CAMERA = 0x03;

    private static final String IMG_LICENSE_FILE_NAME = "userAvatar.jpeg";


    private Context mContext = PersonalInfoActivity.this;

    private boolean isAvatarUpdate; // 头像是否有更新

    private Bitmap mAvatar;
    private String mImgFileName;

    private StringBuffer mSubInfoUrl = new StringBuffer();
    private StringBuffer mSubAvatarUrl = new StringBuffer();

    private final OkHttpClient mClient = new OkHttpClient();

    private static final int MSG_UPLOAD_AVATAR_SUCCESS = 0x00;
    private static final int MSG_UPLOAD_AVATAR_FAIL = 0x01;
    private static final int MSG_UPLOAD_INFO_SUCCESS = 0x02;
    private static final int MSG_UPLOAD_INFO_FAIL = 0x03;
    private static final int CONNECT_FAIL = 0x04;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD_AVATAR_SUCCESS:
                    mAvatarPath = (String) msg.obj;
                    uploadInfo(mSubInfoUrl, mAvatarPath); // 上传个人信息
                    break;
                case MSG_UPLOAD_AVATAR_FAIL:
                    Toast.makeText(mContext, "上传头像失败", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPLOAD_INFO_SUCCESS:
                    saveData(); // 保存数据到preferences
                    Toast.makeText(mContext, "信息修改成功", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                    break;
                case MSG_UPLOAD_INFO_FAIL:
                    Toast.makeText(mContext, "信息修改失败，请稍候再试", Toast.LENGTH_SHORT).show();
                    break;
                case CONNECT_FAIL:
                    Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        ButterKnife.bind(this);

        initToolbar(); // 初始化toolbar
        initData(); // 初始化用户数据
        initView(); // 初始化视图
        initEvent(); // 初始化监听事件
    }

    /**
     * 初始化toolbar
     */
    private void initToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle.setText(getString(R.string.title_personal_info));
    }

    /**
     * 初始化用户数据
     */
    private void initData() {
        mAvatarPath = (String) SPUtil.get(mContext, Constant.Key.USER_AVATAR,
                Constant.Type.STRING);
        mUsername = (String) SPUtil.get(mContext, Constant.Key.USER_NAME,
                Constant.Type.STRING);
        mGender = (int) SPUtil.get(mContext, Constant.Key.USER_GENDER,
                Constant.Type.INTEGER);
        mSignature = (String) SPUtil.get(mContext, Constant.Key.USER_SIGNATURE,
                Constant.Type.STRING);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        // TODO 设置头像

        // 设置用户名
        tvUsername.setText(mUsername);
        // 设置性别
        if (mGender == MALE) {
            tvGender.setText("男");
        } else {
            tvGender.setText("女");
        }
        // 设置
        etSignature.setText(mSignature);
        etSignature.setSelection(mSignature.length());
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        layoutAvatar.setOnClickListener(this);
        layoutUsername.setOnClickListener(this);
        layoutGender.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                // TODO 读取相册
                showImgPickerDialog();
                break;
            case R.id.username:
                WidgetUtils.showDialogWithET(mContext, "请输入昵称", tvUsername);
                break;
            case R.id.gender:
                String[] genders = {"男", "女"};
                WidgetUtils.showDialogWithItems(mContext, "请选择性别", tvGender, genders);
                break;
            case R.id.btn_save:
                initUrl(); // 初始化url
                save(); // 保存信息
                break;
        }
    }

    /**
     * 显示图片选择弹出框
     */
    private void showImgPickerDialog() {
        final String[] selections = {"拍照", "上传本地图片"};
        new AlertDialog.Builder(mContext)
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
                    mAvatarFile = tmpFile;
                    mAvatar = BitmapFactory.decodeFile(tmpFile.getPath());
//                    imgUserAvatar.setImageBitmap(PicUtils.fitView(mAvatar, imgUserAvatar));
                    imgUserAvatar.setImageBitmap(mAvatar);
                    isAvatarUpdate = true;
                } else {
                    Toast.makeText(mContext, "未检测到SD卡", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GALLERY_BEFORE_KITKAT:
            case REQUEST_GALLERY_AFTER_KITKAT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mImgFileName = PicUtils.getDataColumn(getApplicationContext(),
                            data.getData(), null, null); // 得到文件名
                    mAvatarFile = new File(mImgFileName); // 得到文件
                    Log.d("files", "the file from gallery is " + mAvatarFile.toString());
                    mAvatar = BitmapFactory.decodeFile(mImgFileName);
//                    imgUserAvatar.setImageBitmap(PicUtils.fitView(mAvatar, imgUserAvatar));
                    imgUserAvatar.setImageBitmap(mAvatar);
                    isAvatarUpdate = true;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        String ip = (String) SPUtil.get(mContext, Constant.Key.URL_IP, Constant.Type.STRING);
        String port = (String) SPUtil.get(mContext, Constant.Key.URL_PORT, Constant.Type.STRING);

        StringBuffer baseUrl = new StringBuffer();
        baseUrl.append("http://")
                .append(ip)
                .append(":")
                .append(port);

        mSubAvatarUrl.append(baseUrl);
        mSubAvatarUrl.append("/Car/uploadFile.jsp");

        mSubInfoUrl.append(baseUrl);
        mSubInfoUrl.append("/Car/updateUserInfo.jsp");
    }

    /**
     * 保存信息
     */
    private void save() {
//        if (isAvatarUpdate) {
//            Log.d("submit", "1");
//            uploadAvatar(mSubAvatarUrl, mAvatarFile);
//        } else {
//            Log.d("submit", "2");
//            uploadInfo(mSubInfoUrl, null);
//        }
        uploadInfo(mSubInfoUrl, null);
    }

    /**
     * 上传信息
     *
     * @param urlSB
     * @param avatarPath
     */
    private void uploadInfo(StringBuffer urlSB, String avatarPath) {
        Log.d("submit", "url " + urlSB.toString());
        String url = urlSB.toString();
        int id = (int) SPUtil.get(mContext, Constant.Key.USER_ID, Constant.Type.INTEGER);
        mUsername = tvUsername.getText().toString().trim();
        String gender = tvGender.getText().toString();
        if (gender.equals("男"))
            mGender = MALE;
        else
            mGender = FEMALE;
        mSignature = etSignature.getText().toString().trim();

        // 上传个人信息
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        formEncodingBuilder.add("id", String.valueOf(id))
                .add("name", mUsername)
                .add("gender", String.valueOf(mGender))
                .add("signature", mSignature);
        if (isAvatarUpdate && avatarPath != null)
            formEncodingBuilder.add("avatar", avatarPath);

        RequestBody formBody = formEncodingBuilder.build();

        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(mContext, "联系服务器失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String jsonStr = response.body().string();
                Log.d("submit", "json " + jsonStr);
                try {
                    int status = new JSONObject(jsonStr).getInt("status");
                    if (status == 0)
                        mHandler.sendEmptyMessage(MSG_UPLOAD_INFO_SUCCESS);
                    else
                        mHandler.sendEmptyMessage(MSG_UPLOAD_INFO_FAIL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 上传头像
     *
     * @param urlSB
     */
    private void uploadAvatar(StringBuffer urlSB, File avatarFile) {
        String url = urlSB.toString();
        String filename = avatarFile.getName();
        File file = avatarFile;
        String avatarPath = null;

        // TODO 上传图片
//        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        RequestBody fileBody = RequestBody.create(Constant.Media.TYPE_JPEG, file);

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"uploadFile\""),
                        fileBody)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

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

                String jsonStr = response.body().string();
                Log.d("files", jsonStr);
                try {
                    int status = new JSONObject(jsonStr).getInt("status");
                    Log.d("files", "the status is " + status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // TODO 判断图片是否上传成功
//        Message msg = new Message();
//        msg.what = MSG_UPLOAD_AVATAR_SUCCESS;
//        msg.obj = avatarPath;
//        mHandler.sendMessage(msg);
    }

    /**
     * 将数据信息存到preferences里面
     */
    private void saveData() {
        SPUtil.save(mContext, Constant.Key.USER_NAME, mUsername);
        SPUtil.save(mContext, Constant.Key.USER_GENDER, mGender);
        SPUtil.save(mContext, Constant.Key.USER_SIGNATURE, mSignature);
    }
}
