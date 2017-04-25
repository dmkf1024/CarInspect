package studio.imedia.vehicleinspection.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import studio.imedia.vehicleinspection.R;

/**
 * Created by 代码咖啡 on 2017/4/18
 * <p>
 * Email: wjnovember@icloud.com
 */

public abstract class BaseFragment extends Fragment {

    protected static String TITLE_CAR_FILE;
    protected static String TITLE_CAR_INFO;

    protected static String LOADING;
    protected static String CONNECT_FAILED;
    protected static String SELECT_CAR_BRAND_FIRST;
    protected static String LOGIN_FIRST;
    protected static String SAVE_SUCCESS;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 初始化上下文
        initContext();
        // 初始化字符串
        initString(getActivity());
    }

    protected abstract Context initContext();

    /**
     * 初始化字符串
     */
    protected static void initString(Activity activity) {
        TITLE_CAR_FILE = activity.getString(R.string.title_car_file);
        TITLE_CAR_INFO = activity.getString(R.string.title_car_info);

        LOADING = activity.getString(R.string.loading);
        CONNECT_FAILED = activity.getString(R.string.connect_failed);
        SELECT_CAR_BRAND_FIRST = activity.getString(R.string.select_car_brand_first);
        LOGIN_FIRST = activity.getString(R.string.login_first);
        SAVE_SUCCESS = activity.getString(R.string.save_success);
    }

    /**
     * activity跳转
     *
     * @param cls
     *      目标Activity
     */
    protected void activityJump(Class<?> cls) {
        Context context = initContext();
        context.startActivity(new Intent(context, cls));
    }

    /**
     * activity跳转
     *
     * @param cls
     *      目标Activity
     * @param bundle
     *      键值对
     */
    protected void activityJump(Class<?> cls, Bundle bundle) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     *
     * @param cls
     * @param key
     * @param str
     */
    protected void activityJump(Class<?> cls, String key, String str) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, str);
        context.startActivity(intent);
    }

    /**
     *
     * @param cls
     * @param key
     * @param d
     */
    protected void activityJump(Class<?> cls, String key, double d) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, d);
        context.startActivity(intent);
    }

    /**
     *
     * @param cls
     * @param key
     * @param i
     */
    protected void activityJump(Class<?> cls, String key, int i) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, i);
        context.startActivity(intent);
    }

    /**
     *
     * @param cls
     * @param key
     * @param b
     */
    protected void activityJump(Class<?> cls, String key, boolean b) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, b);
        context.startActivity(intent);
    }

    /**
     *
     * @param cls
     * @param key
     * @param p
     */
    protected void activityJump(Class<?> cls, String key, Parcelable p) {
        Context context = initContext();
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, p);
        context.startActivity(intent);
    }
}
