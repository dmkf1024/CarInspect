package studio.imedia.vehicleinspection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class BaseActivity extends AppCompatActivity {

    protected static String HINT_CONNECT_FAILED;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化字符串
        initString();
    }

    /**
     * 初始化字符串
     */
    private void initString() {
        HINT_CONNECT_FAILED = getString(R.string.connect_failed);
    }
}
