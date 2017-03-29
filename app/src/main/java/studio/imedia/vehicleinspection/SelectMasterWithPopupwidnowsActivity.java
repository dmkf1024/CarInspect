package studio.imedia.vehicleinspection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import studio.imedia.vehicleinspection.adapters.MyMasterAdapter;
import studio.imedia.vehicleinspection.bean.Master;
import studio.imedia.vehicleinspection.bean.Recorder;
import studio.imedia.vehicleinspection.managers.MediaManager;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;
import studio.imedia.vehicleinspection.utils.MyWidgetUtils;
import studio.imedia.vehicleinspection.views.AudioRecorderButton;

public class SelectMasterWithPopupwidnowsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView mTitle;
    private RelativeLayout container;
    private RelativeLayout noMaster;

    private ListView lvMaster;
    private List<Master> masterList;

    private MyMasterAdapter adapter;

    private Recorder recorder;
    private View popupView;

    private PopupWindow popupWindow;

    private Context mContext = SelectMasterWithPopupwidnowsActivity.this;

    private String mDate;
    private String mTime;
    private String mStation;
    private int mStationId;
    private String mMsgLeft;

    private boolean isPlaying = false;

    private String mUrl;
    private String mIp;
    private String mPort;

    private final OkHttpClient mClient = new OkHttpClient();
    private final Gson mGson = new Gson();
    private GMasterInfo gMasterInfo;

    private static final int MSG_OK = 0x01;
    private static final int MSG_FAIL = 0x02;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_OK:
                    Log.d("master", "ok");
                    gMasterInfo = (GMasterInfo) msg.obj;
                    initData(); // 初始化数据，从接口中获取
                    setAdapter();
                    break;
                case MSG_FAIL:
                    Log.d("master", "fail");
                    hideList(); // 隐藏列表
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_master_with_popupwidnows);

        initToolbar(); // 初始化toolbar
        findView();  // 关联控件
        getBundle(); // 获取bundle传过来的值
        initEvent(); // 监听事件回调
        initUrl(); // 初始化url
        getDataFromUrl(mUrl); // 从接口获取数据

    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mTitle = (TextView) mToolbar.findViewById(R.id.title);
        mTitle.setText(getString(R.string.title_select_master));
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvMaster = (ListView) findViewById(R.id.lv_master);
        container = (RelativeLayout) findViewById(R.id.container);
        noMaster = (RelativeLayout) findViewById(R.id.no_master);
    }

    /**
     * 获取bundle传过来的值
     */
    private void getBundle() {
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            mDate = bundle.getString(StaticValues.KEY_ORDER_DATE);
            mTime = bundle.getString(StaticValues.KEY_ORDER_TIME);
            mStation = bundle.getString(StaticValues.KEY_INSPECT_STATION);
            mStationId = bundle.getInt(StaticValues.KEY_STATION_ID);
        }
    }

    /**
     * 监听事件回调
     */
    private void initEvent() {
        lvMaster.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow(position);
            }
        });
    }

    /**
     * 初始化url
     */
    private void initUrl() {
        mPort = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_PORT,
                StaticValues.TYPE_STRING);
        mIp = (String) MySharedPreferencesUtils.get(mContext, StaticValues.KEY_URL_IP,
                StaticValues.TYPE_STRING);

        mUrl = "http://" + mIp + ":" + mPort + "/Car/masterInfo.jsp?id=" + mStationId;
        Log.d("master", mUrl);
    }

    /**
     * 从url中获取数据
     */
    private void getDataFromUrl(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String json = response.body().string();
                GMasterInfo info = mGson.fromJson(json, GMasterInfo.class);
                int status = info.status;
                List<GMaster> list = info.masters;
                if (status == 0 && list != null && list.size() > 0) {
                    Message msg = new Message();
                    msg.what = MSG_OK;
                    msg.obj = info;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(MSG_FAIL);
                }

            }
        });

    }

    private static class GMasterInfo {
        List<GMaster> masters;
        int status;
    }

    private static class GMaster {
        int id;
        String name;
        int ratingStars;
        int servicePrice;
        String avatar;
        int serviceTimes;
    }

    /**
     * 显示弹出框
     */
    private void showPopupWindow(final int position) {
        popupView = LayoutInflater.from(mContext).inflate(R.layout.item_master_children, null);

        // 关联控件
        CheckBox checkBox = (CheckBox) popupView.findViewById(R.id.checkbox);
        TextView tvMsgTo = (TextView) popupView.findViewById(R.id.tv_message_to);
        final RelativeLayout layoutMessage = (RelativeLayout) popupView.findViewById(R.id.layout_message);
        final Button btnSwitch = (Button) popupView.findViewById(R.id.btn_switch);
        final EditText etLeaveMsg = (EditText) popupView.findViewById(R.id.et_leave_msg);
        final AudioRecorderButton audioButton = (AudioRecorderButton) popupView.findViewById(R.id.id_recorder_button);
        final RelativeLayout layoutAudio = (RelativeLayout) popupView.findViewById(R.id.layout_audio);
        final View animView = popupView.findViewById(R.id.id_recorder_anim);
        final TextView tvRecorderTime = (TextView) popupView.findViewById(R.id.id_recorder_time);
        Button btnSubmit = (Button) popupView.findViewById(R.id.btn_submit);

        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        ColorDrawable cd = new ColorDrawable(getResources().getColor(R.color.color_activity_bg_02));
        popupWindow.setBackgroundDrawable(cd);
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        popupWindow.showAtLocation(container, Gravity.BOTTOM | Gravity.CENTER, 0, 0);

        // 弹出窗口消失事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1f);
                MediaManager.release();
            }
        });
        if (null != popupWindow && popupWindow.isShowing()) {
            backgroundAlpha(0.4f);
        } else {
            backgroundAlpha(1f);
        }

        tvMsgTo.setText("给" + masterList.get(position).getName() + "留言");

        // 选中框事件
        // 选中勾选框，显示留言操作；取消勾选框，隐藏留言操作
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                MyWidgetUtils.displayViewByCheckBox(isChecked, true, MyWidgetUtils.Type.TYPE_GONE,
                        layoutMessage);
            }
        });

        // 切换按钮的点击事件
        final int[] curState = {StaticValues.TYPE_TEXT};
        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaManager.release();
                if (StaticValues.TYPE_TEXT == curState[0]) {
                    curState[0] = StaticValues.TYPE_SOUND;
                    btnSwitch.setBackgroundResource(R.drawable.icon_keyboard);

                    // 隐藏文本输入和语音输出
                    etLeaveMsg.setVisibility(View.GONE);
                    layoutAudio.setVisibility(View.GONE);
                    // 显示AudioButton
                    audioButton.setVisibility(View.VISIBLE);
                } else {
                    curState[0] = StaticValues.TYPE_TEXT;
                    btnSwitch.setBackgroundResource(R.drawable.icon_microphone);

                    // 隐藏AudioButton和语音输出
                    audioButton.setVisibility(View.GONE);
                    layoutAudio.setVisibility(View.GONE);
                    // 显示文本输入
                    etLeaveMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        // 提交按钮点击事件
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMsgLeft = etLeaveMsg.getText().toString().trim();
                popupWindow.dismiss();
                Intent intent = new Intent(mContext, SubmitOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(StaticValues.KEY_PROXY_STATE, true);
                bundle.putString(StaticValues.KEY_MASTER_NAME, masterList.get(position).getName());
                bundle.putString(StaticValues.KEY_ORDER_DATE, mDate);
                bundle.putString(StaticValues.KEY_ORDER_TIME, mTime);
                bundle.putString(StaticValues.KEY_INSPECT_STATION, mStation);
                bundle.putInt(StaticValues.KEY_STATION_ID, mStationId);
                bundle.putString(StaticValues.KEY_ORDER_MSG_LEFT, mMsgLeft);
                bundle.putInt(StaticValues.KEY_MASTER_ID, masterList.get(position).getId());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // 录音结束事件
        audioButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Log.d("file", filePath);
                layoutAudio.setVisibility(View.VISIBLE);
                audioButton.setVisibility(View.GONE);
                recorder = new Recorder(seconds, filePath);
                tvRecorderTime.setText(Math.round(seconds) + "\"");
            }
        });

        // 录音播放按钮点击事件
        layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != animView) {
                    animView.setBackgroundResource(R.drawable.adj);
                }

                if (!isPlaying) {
                    isPlaying = true;
                    // 播放动画
                    animView.setBackgroundResource(R.drawable.play_anim);
                    AnimationDrawable anim = (AnimationDrawable) animView.getBackground();
                    anim.start();
                    // 播放音频
                    MediaManager.playSound(recorder.getFilePath(), new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            animView.setBackgroundResource(R.drawable.adj);
                        }
                    });
                } else {
                    isPlaying = false;
                    animView.setBackgroundResource(R.drawable.adj);
                    MediaManager.release();
                }
            }
        });
    }

    /**
     * 背景变暗
     */
    private void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    private void initData() {
        if (masterList == null) {
            masterList = new ArrayList<>();
        }
        if (null != gMasterInfo.masters) {
            showList(); // 显示师傅列表
            List<GMaster> gMasterList = gMasterInfo.masters;
            for (GMaster gMaster : gMasterList) {
                Master master = new Master();
                master.setId(gMaster.id);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_head);
                master.setAvatar(bitmap);
                master.setName(gMaster.name);
                master.setStartNum(gMaster.ratingStars);
                master.setServiceAmount(gMaster.serviceTimes);
                master.setPrice(gMaster.servicePrice);
                masterList.add(master);
            }
        } else {
            hideList(); // 隐藏师傅列表
        }
    }


    /**
     * 配置适配器
     */
    private void setAdapter() {
        adapter = new MyMasterAdapter(mContext, masterList);
        lvMaster.setAdapter(adapter);
    }

    /**
     * 显示师傅列表
     * 有师傅数据时调用
     */
    private void showList() {
        lvMaster.setVisibility(View.VISIBLE);
        noMaster.setVisibility(View.GONE);
    }

    /**
     * 隐藏师傅列表
     * 没有师傅数据时调用
     */
    private void hideList() {
        lvMaster.setVisibility(View.GONE);
        noMaster.setVisibility(View.VISIBLE);
    }
}
