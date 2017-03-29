package studio.imedia.vehicleinspection.application;

import android.app.Application;
import android.content.ComponentCallbacks;
import android.os.Environment;
import android.util.Log;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.StaticValues;
import studio.imedia.vehicleinspection.utils.MyFileUtils;
import studio.imedia.vehicleinspection.utils.MySharedPreferencesUtils;

/**
 * Created by eric on 15/10/15.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initIpPort(); // 初始化IP地址和端口号
        clearAudioFiles(); // 清除录音文件
        clearLogHisRecord(); // 清除上次登录状态
    }

    /**
     * 初始化IP地址和端口号
     */
    private void initIpPort() {
        String ip = (String) MySharedPreferencesUtils.get(getApplicationContext(), StaticValues.KEY_URL_IP,
                StaticValues.TYPE_STRING);
        String port = (String) MySharedPreferencesUtils.get(getApplicationContext(), StaticValues.KEY_URL_PORT,
                StaticValues.TYPE_STRING);

        if (null == ip || ip.isEmpty()) {
            ip = getResources().getString(R.string.default_ip);
        }
        if (null == port || port.isEmpty()) {
            port = getResources().getString(R.string.default_port);
        }

        MySharedPreferencesUtils.save(getApplicationContext(), StaticValues.KEY_URL_IP, ip);
        MySharedPreferencesUtils.save(getApplicationContext(), StaticValues.KEY_URL_PORT, port);
    }

    /**
     * 清除历史录音文件
     */
    private void clearAudioFiles() {
        String folderPath = Environment.getExternalStorageDirectory() + "/" + StaticValues.AUDIO_FILE_NAME;
        MyFileUtils.deleteAllFiles(folderPath, "amr");
    }

    /**
     * 清除历史登录记录
     * 软清除
     */
    private void clearLogHisRecord() {
        MySharedPreferencesUtils.save(getApplicationContext(), StaticValues.KEY_LOGIN_STATE, false);
    }
}
