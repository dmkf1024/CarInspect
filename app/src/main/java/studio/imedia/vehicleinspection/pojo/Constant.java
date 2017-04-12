package studio.imedia.vehicleinspection.pojo;

import com.amap.api.maps.model.LatLng;
import com.squareup.okhttp.MediaType;

/**
 * Created by eric on 15/10/10.
 */
public class Constant {

    public static class Location {
        // 车检站
        public static final int NUM_VI = 11;
        public static final LatLng[] VI = {new LatLng(30.195887, 120.05817), new LatLng(30.181965, 120.21223),
                new LatLng(30.291603, 120.326005), new LatLng(30.378028, 119.973673), new LatLng(30.329851, 120.17022),
                new LatLng(30.340461, 120.179898), new LatLng(30.317372, 120.214351), new LatLng(30.307244, 120.16401),
                new LatLng(30.30422, 120.164627), new LatLng(29.813112, 119.668105), new LatLng(29.443078, 119.271344)};
        public static final String[] NAME_VI = {"杭州市第一车辆检测站", "杭州警苑综合服务部第二车辆检测站", "杭州第三汽车检验站", "杭州市公安局交警支队第五车辆检测站",
                "杭州警苑综合服务部第六车辆检测站", "杭州市警苑综合服务部第七车辆检测站", "杭州警苑综合服务部第八车辆检测站", "杭州警苑综合服务部第九车辆检测站", "杭州警苑综合服务部第十车辆检测站",
                "杭州市公安局第十一车辆检测站", "杭州市公安局机动车辆第十二检测站"};
    }

    /**
     * 网络接口相关
     */
    public static class Net {
        public static final String DEFAULT_IP = "127.0.0.1";
        public static final String DEFAULT_PORT = "80";

        public static final String API_NAME = "kqapi";
        public static final String API_LOGIN = "login.php";

        public static final String BASE_URL = "http://wj1024.com/Car/";

        public static final int READ_TIME_OUT = 6;
        public static final int CONNECT_TIME_OUT = 5;
    }

    public static class Type {
        // Object类型判断标记
        public static final int INTEGER = 0;
        public static final int STRING = 1;
        public static final int FLOAT = 2;
        public static final int LONG = 3;
        public static final int BOOLEAN = 4;
    }

    public static class Status {
        // 接口数据的状态
        public static final int OK = 0;
        public static final int FAIL = 1;
    }

    
    public static class Key {
        // key字符串
        public static final String LOGIN_STATE = "key_login_state";
        public static final String PROXY_STATE = "key_proxy_state";
        public static final String MASTER_ID = "key_master_id";
        public static final String MASTER_NAME = "key_master_name";
        public static final String ORDER_DATE = "key_order_date";
        public static final String ORDER_TIME = "key_order_time";
        public static final String ORDER_MSG_LEFT = "key_order_msg_left";
        public static final String ORDER_ID = "key_order_id";
        public static final String INSPECT_STATION = "key_inspect_station";
        public static final String URL_IP = "key_url_ip";
        public static final String URL_PORT = "key_url_port";
        public static final String STATION_ID = "key_station_id";

        public static final String CAR_BRAND_NAME = "key_car_brand_name";
        public static final String CAR_BRAND_ID = "key_car_brand_id";
        public static final String CAR_TYPE_NAME = "key_car_type_name";
        public static final String CAR_TYPE_ID = "key_car_type_id";
        public static final String CAR_FRAME_NUM = "key_car_frame_num";
        public static final String CAR_PLATE_NUM = "key_car_plate_num";
        public static final String CAR_ENGINE_NUM = "key_car_engine_num";
        public static final String CAR_LICENSE_PIC ="key_car_license_pic";
        public static final String CAR_REGISTER_TIME = "key_car_register_time";

        public static final String USER_ID = "key_car_id";
        public static final String USER_AVATAR = "key_user_avatar";
        public static final String USER_NAME = "key_user_name";
        public static final String USER_SCORE = "key_user_score";
        public static final String USER_GENDER = "key_user_gender";
        public static final String USER_SIGNATURE = "key_user_signature";
        public static final String USER_PROVINCE_ID = "key_user_province_id";
        public static final String USER_PROVINCE_NAME = "key_user_province_name";
        public static final String USER_CITY_ID = "key_user_city_id";
        public static final String USER_CITY_ID_TMP = "key_user_city_id_tmp";
        public static final String USER_CITY_NAME = "key_user_city_name";
        public static final String USER_CITY_NAME_TMP = "key_user_city_name_tmp";
        public static final String USER_COUNTY_ID = "key_user_county_id";
        public static final String USER_COUNTY_NAME = "key_user_county_name";
        public static final String USER_DETAILED_ADDRESS = "key_user_detailed_address";
        public static final String USER_CITY_COUNTY = "key_user_city_county";
        public static final String COUPON_ID = "key_coupon_id";
        public static final String IS_USE_COUPON = "key_is_use_coupon";

        public static final String PHONE = "key_phone";
        public static final String PASSWORD = "key_password";
        public static final String FROM = "key_from";
        public static final String LOGIN_PHONE_NUM = "key_login_account";
    }

    public static class Media {
        public static final MediaType TYPE_MARKDOWN =
                MediaType.parse("text/x-markdown; charset=utf-8");
        public static final MediaType TYPE_JPG =
                MediaType.parse("text/jpg; charset=utf-8");
        public static final MediaType TYPE_JPEG =
                MediaType.parse("text/jpeg; charset=utf-8");
//    public static final MediaType MEDIA_CONTENT_TYPE =
//            MediaType.parse("multipart/form-data");

        // 类型：语音、文本
        public static final int TYPE_SOUND = 0;
        public static final int TYPE_TEXT = 1;
    }

    public static class Tag {
        public static final String NET = "tag_net";
    }

    public static final int LENGTH_TIMELY_FLOW = 5;

    public static final String AUDIO_FILE_NAME = "/vehicle_inspection_recorder_audios";

    public static final String TAB_SELECTED = "tab_selected";

    public static final String ACTIVITY_SELECT_COUPON = "activity_select_coupon";
    public static final String ACTIVITY_REGISTER = "activity_register";
}
