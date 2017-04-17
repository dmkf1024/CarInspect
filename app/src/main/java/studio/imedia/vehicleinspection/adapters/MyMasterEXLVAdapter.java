package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.SubmitOrderActivity;
import studio.imedia.vehicleinspection.bean.Master;
import studio.imedia.vehicleinspection.bean.Recorder;
import studio.imedia.vehicleinspection.managers.MediaManager;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.WidgetUtils;
import studio.imedia.vehicleinspection.views.AudioRecorderButton;
import studio.imedia.vehicleinspection.views.RoundImageView;

/**
 * Created by eric on 15/10/11.
 */
public class MyMasterEXLVAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Master> masterList;
    private LayoutInflater inflater;
    private ExpandableListView listView;

    private String date;
    private String time;
    private String station;

    private Recorder mRecorder;

    public MyMasterEXLVAdapter(Context context, List<Master> masterList, ExpandableListView listView) {
        this.context = context;
        this.masterList = masterList;
        inflater = LayoutInflater.from(context);
        this.listView = listView;
    }

    public MyMasterEXLVAdapter(Context context, List<Master> masterList, ExpandableListView listView, Bundle bundle) {
        this(context, masterList, listView);
        if (null != bundle) {
            date = bundle.getString(Constant.Key.ORDER_DATE);
            time = bundle.getString(Constant.Key.ORDER_TIME);
            station = bundle.getString(Constant.Key.INSPECT_STATION);
        }
    }

    @Override
    public int getGroupCount() {
        return masterList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return masterList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        Master master = masterList.get(groupPosition);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_master_parent, null);
            holder = new GroupViewHolder();
            holder.imgAvatar = (RoundImageView) convertView.findViewById(R.id.img_avatar);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            holder.tvServiceAmount = (TextView) convertView.findViewById(R.id.tv_service_amount);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.imgFolder = (ImageView) convertView.findViewById(R.id.img_folder);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        holder.imgAvatar.setImageBitmap(master.getAvatar());
        holder.tvName.setText(master.getName());
        holder.ratingBar.setNumStars(master.getStartNum());
        holder.tvServiceAmount.setText(master.getServiceAmount() + "次服务");
        holder.tvPrice.setText(master.getPrice() + "");
        if (isExpanded) {
            holder.imgFolder.setImageResource(R.drawable.icon_up);
        } else {
            holder.imgFolder.setImageResource(R.drawable.icon_down);
        }
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildrenViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_master_children, null);
            holder = new ChildrenViewHolder();
            holder.layoutMessage = (RelativeLayout) convertView.findViewById(R.id.layout_message);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox);
            holder.btnSwitch = (Button) convertView.findViewById(R.id.btn_switch);
            holder.btnSubmit = (Button) convertView.findViewById(R.id.btn_submit);
            holder.etLeaveMsg = (EditText) convertView.findViewById(R.id.et_leave_msg);
            holder.layoutAudio = (RelativeLayout) convertView.findViewById(R.id.layout_audio);
            holder.audioButton = (AudioRecorderButton) convertView.findViewById(R.id.id_recorder_button);
            holder.animView = convertView.findViewById(R.id.id_recorder_anim);
            holder.tvRecorderTime = (TextView) convertView.findViewById(R.id.id_recorder_time);

            convertView.setTag(holder);
        } else {
            holder = (ChildrenViewHolder) convertView.getTag();
        }

        resetChildView(holder.checkBox, holder.layoutMessage, holder.btnSwitch, holder.etLeaveMsg,
                holder.audioButton, holder.layoutAudio, holder.animView); // 重置子Item

        // 选中框事件
        // 选中勾选框，显示留言操作；取消勾选框，隐藏留言操作
        final ChildrenViewHolder finalHolder = holder;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WidgetUtils.displayViewByCheckBox(isChecked, true, WidgetUtils.Type.TYPE_GONE,
                        finalHolder.layoutMessage);
            }
        });

        // 切换按钮点击事件
        final int[] curState = {Constant.Media.TYPE_TEXT};
        finalHolder.btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.Media.TYPE_TEXT == curState[0]) {
                    curState[0] = Constant.Media.TYPE_SOUND;
                    finalHolder.btnSwitch.setBackgroundResource(R.mipmap.ic_launcher);

                    // 隐藏文本输入和语音输出
                    finalHolder.etLeaveMsg.setVisibility(View.GONE);
                    finalHolder.layoutAudio.setVisibility(View.GONE);
                    // 显示AudioButton
                    finalHolder.audioButton.setVisibility(View.VISIBLE);
                } else {
                    curState[0] = Constant.Media.TYPE_TEXT;
                    finalHolder.btnSwitch.setBackgroundResource(R.drawable.icon_microphone);

                    // 隐藏AudioButton和语音输出
                    finalHolder.audioButton.setVisibility(View.GONE);
                    finalHolder.layoutAudio.setVisibility(View.GONE);
                    // 显示文本输入
                    finalHolder.etLeaveMsg.setVisibility(View.VISIBLE);
                }
            }
        });

        // 提交按钮点击事件
        holder.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SubmitOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean(Constant.Key.PROXY_STATE, true);
                bundle.putString(Constant.Key.MASTER_NAME, masterList.get(groupPosition).getName());
                bundle.putString(Constant.Key.ORDER_DATE, date);
                bundle.putString(Constant.Key.ORDER_TIME, time);
                bundle.putString(Constant.Key.INSPECT_STATION, station);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        // 录音按钮点击事件
        holder.audioButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        listView.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        listView.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                        listView.setEnabled(true);
                        finalHolder.layoutAudio.setVisibility(View.VISIBLE);
                        finalHolder.audioButton.setVisibility(View.GONE);
                        break;
                }
                return false;
            }
        });

        // 录音结束事件
        holder.audioButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                mRecorder = new Recorder(seconds, filePath);
                finalHolder.tvRecorderTime.setText(Math.round(seconds) + "\"");
            }
        });

        // 录音播放按钮点击事件
        holder.layoutAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != finalHolder.animView) {
                    finalHolder.animView.setBackgroundResource(R.drawable.adj);
                }

                // 播放动画
                finalHolder.animView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) finalHolder.animView.getBackground();
                anim.start();
                // 播放音频
                MediaManager.playSound(mRecorder.getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        finalHolder.animView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
        return convertView;
    }

    /**
     * 重置子item
     * @param checkBox
     * @param layoutMessage
     * @param switchButton
     * @param etMessage
     * @param audioRecorderButton
     * @param layoutAudio
     */
    private void resetChildView(CheckBox checkBox, RelativeLayout layoutMessage, Button switchButton, EditText etMessage,
                                AudioRecorderButton audioRecorderButton, RelativeLayout layoutAudio, View animView) {
        checkBox.setChecked(true);
        WidgetUtils.displayViewByCheckBox(checkBox, true, WidgetUtils.Type.TYPE_GONE,
                layoutMessage);
        switchButton.setBackgroundResource(R.drawable.icon_microphone);
        etMessage.setVisibility(View.VISIBLE);
        audioRecorderButton.setVisibility(View.GONE);
        layoutAudio.setVisibility(View.GONE);
        animView.setBackgroundResource(R.drawable.adj);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class GroupViewHolder {
        private RoundImageView imgAvatar;
        private TextView tvName;
        private RatingBar ratingBar;
        private TextView tvServiceAmount;
        private TextView tvPrice;
        private ImageView imgFolder;
    }

    private class ChildrenViewHolder {
        private CheckBox checkBox;
        private RelativeLayout layoutMessage;
        private Button btnSubmit;
        private EditText etLeaveMsg;
        private AudioRecorderButton audioButton;
        private RelativeLayout layoutAudio;
        private Button btnSwitch;
        private View animView;
        private TextView tvRecorderTime;
    }
}
