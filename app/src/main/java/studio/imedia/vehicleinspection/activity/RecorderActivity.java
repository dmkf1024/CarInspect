package studio.imedia.vehicleinspection.activity;

import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.activity.BaseActivity;
import studio.imedia.vehicleinspection.adapters.MyRecorderAdapter;
import studio.imedia.vehicleinspection.bean.Recorder;
import studio.imedia.vehicleinspection.managers.MediaManager;
import studio.imedia.vehicleinspection.views.AudioRecorderButton;

public class RecorderActivity extends BaseActivity {

    @BindView(R.id.id_recorder_button)
    AudioRecorderButton mAudioRecorderButton;
    @BindView(R.id.id_listview)
    ListView mListView;

    private ArrayAdapter<Recorder> mAdapter;
    private List<Recorder> mDatas = new ArrayList<Recorder>();

    private View mAnimView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        ButterKnife.bind(this);

        mAudioRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void onFinish(float seconds, String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                mDatas.add(recorder);
                mAdapter.notifyDataSetChanged();
                mListView.setSelection(mDatas.size() - 1);
            }
        });

        mAdapter = new MyRecorderAdapter(this, mDatas);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.drawable.adj);
                    mAnimView = null;
                }
                // 播放动画
                mAnimView = view.findViewById(R.id.id_recorder_anim);
                mAnimView.setBackgroundResource(R.drawable.play_anim);
                AnimationDrawable anim = (AnimationDrawable) mAnimView.getBackground();
                anim.start();
                // 播放音频
                MediaManager.playSound(mDatas.get(position).getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.drawable.adj);
                    }
                });
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaManager.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaManager.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
    }
}
