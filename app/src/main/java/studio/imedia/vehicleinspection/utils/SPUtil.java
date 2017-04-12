package studio.imedia.vehicleinspection.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import studio.imedia.vehicleinspection.pojo.Constant;

/**
 * Created by Cooffee on 15/10/15.
 */
public class SPUtil {

    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    private static final String FILE_PREFERENCES = "carInspection";

    /**
     * 存入数据
     * @param context
     * @param key
     * @param value
     */
    public static void save(Context context, String key, Object value) {
        if (null == sharedPreferences) {
            sharedPreferences = context.getSharedPreferences(FILE_PREFERENCES, Context.MODE_PRIVATE);
        }
        if (null == editor) {
            editor = sharedPreferences.edit();
        }

        // 判断value的类型，并存入value
        if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        }  else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }

        editor.commit();
        editor = null;
        sharedPreferences = null;
    }

    /**
     * 从sharedpreferences里面获取数据
     * @param context
     * @param key
     * @param valueType
     * @return
     */
    public static Object get(Context context, String key, int valueType) {
        Object result = null;
        sharedPreferences = context.getSharedPreferences(FILE_PREFERENCES, Activity.MODE_WORLD_READABLE);
        // 判断要获取的数据的类型
        switch (valueType) {
            case Constant.Type.INTEGER:
                result = sharedPreferences.getInt(key, -1);
                break;
            case Constant.Type.STRING:
                result = sharedPreferences.getString(key, "");
                break;
            case Constant.Type.FLOAT:
                result = sharedPreferences.getFloat(key, -1);
                break;
            case Constant.Type.LONG:
                result = sharedPreferences.getLong(key, -1);
                break;
            case Constant.Type.BOOLEAN:
                result = sharedPreferences.getBoolean(key, false);
                break;
        }
        if (TextUtils.equals(key, Constant.Key.URL_IP)) {
            result = "wj1024.com";
        } else if (TextUtils.equals(key, Constant.Key.URL_PORT)) {
            result = "8080";
        }
        sharedPreferences = null;
        return result;
    }
}
