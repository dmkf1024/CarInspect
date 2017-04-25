package studio.imedia.vehicleinspection.utils.proxy;

import android.content.Context;

import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

/**
 * Created by 代码咖啡 on 2017/4/18
 * <p>
 * Email: wjnovember@icloud.com
 */

public class SPProxy {

    /**
     * 保存用户id
     * @param context
     * @param id
     */
    public static void saveUserId(Context context, int id) {
        SPUtil.save(context, Constant.Key.USER_ID, id);
    }

    public static int getUserId(Context context) {
        return (int) SPUtil.get(context, Constant.Key.USER_ID, Constant.Type.INTEGER);
    }

    /**
     * 保存用户登录的手机号
     * @param context
     * @param phone
     */
    public static void saveLoginPhone(Context context, String phone) {
        SPUtil.save(context, Constant.Key.LOGIN_PHONE_NUM, phone);
    }

    /**
     * 获取用户登录的手机号
     * @param context
     * @return
     */
    public static String getLoginPhone(Context context) {
        return (String)SPUtil.get(context, Constant.Key.LOGIN_PHONE_NUM, Constant.Type.STRING);
    }

    /**
     * 判断用户是否在线
     * @param context
     * @return
     */
    public static boolean isOnline(Context context) {
        return getUserId(context) != Constant.Status.OFFLINE;
    }

}
