package studio.imedia.vehicleinspection.application;

import android.app.Application;
import android.os.Environment;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.MyFileUtils;
import studio.imedia.vehicleinspection.utils.SPUtil;

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
        String ip = (String) SPUtil.get(getApplicationContext(), Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(getApplicationContext(), Constant.Key.URL_PORT,
                Constant.Type.STRING);

        if (null == ip || ip.isEmpty()) {
            ip = getResources().getString(R.string.default_ip);
        }
        if (null == port || port.isEmpty()) {
            port = getResources().getString(R.string.default_port);
        }

        SPUtil.save(getApplicationContext(), Constant.Key.URL_IP, ip);
        SPUtil.save(getApplicationContext(), Constant.Key.URL_PORT, port);
    }

    /**
     * 清除历史录音文件
     */
    private void clearAudioFiles() {
        String folderPath = Environment.getExternalStorageDirectory() + "/" + Constant.AUDIO_FILE_NAME;
        MyFileUtils.deleteAllFiles(folderPath, "amr");
    }

    /**
     * 清除历史登录记录
     * 软清除
     */
    private void clearLogHisRecord() {
        SPUtil.save(getApplicationContext(), Constant.Key.LOGIN_STATE, false);
    }
}
