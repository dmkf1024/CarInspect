package studio.imedia.vehicleinspection.debugs;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import studio.imedia.vehicleinspection.R;

public class LocationActivity extends AppCompatActivity {

    private MyGPS myGPS = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String gpsInfo = myGPS.toString();
                    Log.d("haha", "caller: " + gpsInfo);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        new Thread(new Runnable() {
            @Override
            public void run() {
                myGPS = new MyGPS(LocationActivity.this);
                Log.d("haha", "first" + myGPS.toString());
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myGPS.destroyAMapLocationListener();
    }

    //
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        initiazlieGPS();
//
//        // 每次重新回到界面的时候注册广播接收者
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(Constants.INTENT_ACTION_UPDATE_DATA);
//        filter.addAction(Constants.INTENT_ACTION_UPDATE_TIME);
//        registerReceiver(receiver, filter);
//
//
//        //其他需要处理的逻辑
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (receiver != null)
//            unregisterReceiver(receiver);
//    }


//    /**
//     * 自定义广播接收者类
//     *
//     * @author Jywang
//     * @time 2014-11-4
//     * @email jywangkeep@163.com
//     *
//     */
//    public class Receiver extends BroadcastReceiver {
//        /**
//         * 空构造函数,用于初始化Recevier
//         */
//        public Receiver() {
//
//        }
//
//        private void disposeUpdateDataAction(Intent intent) {
//            //书写更新控件数据逻辑
//        }
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent == null)
//                return;
//            if (Constants.INTENT_ACTION_UPDATE_DATA.equals(intent.getAction()))
//                disposeUpdateDataAction(intent);
//            else if (Constants.INTENT_ACTION_UPDATE_TIME.equals(intent
//                    .getAction()))
//                LocationActivity.this.updateTime();
//        }
//    }
}
