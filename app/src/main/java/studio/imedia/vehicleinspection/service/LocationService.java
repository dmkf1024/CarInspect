package studio.imedia.vehicleinspection.service;

/**
 * Created by Aaron on 2015/11/14 0014.
 */

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studio.imedia.vehicleinspection.bean.InspectionStation;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.utils.SPUtil;

/**
 * 定位服务,基于高德的定位API V1.3.0实现
 *
 * @author Jywang
 * @email jywangkeep@163.com
 */
public class LocationService extends Service implements AMapLocationListener {
    public static final long LOCATION_UPDATE_MIN_TIME = -1;         //定位一次
    public static final float LOCATION_UPDATE_MIN_DISTANCE = 5;
    private List<InspectionStation> stationFromOkHttpList;

    // 位置服务代理
    private LocationManagerProxy locationManagerProxy;
    private LocalBroadcastManager localBroadcastManager;
    private String[] orderList;
    private double[] orderList1;
    private int[] index;
    private String mUrl;
    private AMapLocation aMapLocation1;
    private final OkHttpClient mClient = new OkHttpClient();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    getOrder(aMapLocation1, stationFromOkHttpList);
                    getDistance();
                    Bundle bundle = new Bundle();
                    if (null != index && null != orderList) {
                        bundle.putIntArray("下标", index);
                        bundle.putDoubleArray("距离", orderList1);

                        Log.d("msg", "list下标index：" + index[1]);
                    }
                    // 发送广播传送地点位置信息到地图显示界面
                    // 当数据正常获取的时候，把位置信息通过广播发送到接受方,
                    // 也就是需要处理这些数据的组件。
                    localBroadcastManager = LocalBroadcastManager.getInstance(getBaseContext());//getBaseContext()不知道行不行
                    Intent intent = new Intent();
                    intent.putExtra("纬度",
                            aMapLocation1.getLatitude());
                    intent.putExtra("经度",
                            aMapLocation1.getLongitude());
                    intent.putExtras(bundle);
                    intent.setAction("获取定位经纬度值");
                    localBroadcastManager.sendBroadcast(intent);
                    break;
            }
        }
    };

    public LocationService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("msg", "启动了service");
        init();
        //使用参数为Context的方法，Service也是Context实例，
        //是四大组件之一
        locationManagerProxy = LocationManagerProxy.getInstance(this);
        // 定位方式设置为混合定位，包括网络定位和GPS定位
        locationManagerProxy.requestLocationData(
                LocationProviderProxy.AMapNetwork, LOCATION_UPDATE_MIN_TIME,
                LOCATION_UPDATE_MIN_DISTANCE, this);
        // 如果定位方式包括GPS定位需要手动设置GPS可用
//        locationManagerProxy.setGpsEnable(true);
        ;
        Log.v("locationservice", "locationservicestart");
    }

    private void init() {
        /**
         * 初始化url
         */
        initUrl();

    }

    private void initUrl() {
        String ip = (String) SPUtil.get(this, Constant.Key.URL_IP,
                Constant.Type.STRING);
        String port = (String) SPUtil.get(this, Constant.Key.URL_PORT,
                Constant.Type.STRING);
        mUrl = "http://" + ip + ":" + port + "/Car/inspectionStationInfo.jsp?cityId=2";
//        Log.d("flow", mUrl);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        super.onDestroy();

        // 在Service销毁的时候销毁定位资源
        if (locationManagerProxy != null) {
            locationManagerProxy.removeUpdates(this);
            locationManagerProxy.destory();
        }
        //设置为null是为了提醒垃圾回收器回收资源
        locationManagerProxy = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        //在较新的SDK版本中，这个方法在位置发生变化的时候不会被
        //调用。这个方法默认是使用原生GPS服务的时候，当位置
        //变化被调用的方法。
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //当位置发生变化的时候调用这个方法。
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        // 如果位置获取错误则不作处理，退出本方法
        // 返回错误码如果为0则表明定位成功，反之则定位失败
        //在虚拟机测试的时候，返回错误码31，为未知错误
        //如果使用虚拟机测试的时候遇到这个问题，建议使用真机测试。
        Log.d("msg","启动了onLocationChanged");
        if (aMapLocation == null
                || aMapLocation.getAMapException().getErrorCode() != 0) {
            Log.v("locationservice", aMapLocation == null ? "null" : "not null");
            if (aMapLocation != null) {
                Log.v("locationservice", "errorcode"
                        + aMapLocation.getAMapException().getErrorCode());
                Log.v("locationservice", "errormessage"
                        + aMapLocation.getAMapException().getErrorMessage());
            }
            Log.v("locationservice", "request error");
            return;
        }
        aMapLocation1 = aMapLocation;
        getDataFromUrl(mUrl);
        Log.d("msg", "service的经纬度为:" + aMapLocation.getLatitude() + "  " + aMapLocation.getLongitude());

    }

    /**
     * 计算每个加油站与定位点之间的距离并进行排序
     */
    protected void getOrder(AMapLocation aMapLocation, List<InspectionStation> inspectionStations) {
        if (inspectionStations.size() > 0) {
            String[] distanceOrder = new String[inspectionStations.size()];
            orderList = new String[inspectionStations.size()];

            LatLng location = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

            for (int i = 0; i < inspectionStations.size(); i++) {
                // 获取到inspectionStations的经纬度与定位经纬度比较
                BigDecimal bigDecimal = new BigDecimal(AMapUtils.calculateLineDistance(location
                        , new LatLng(inspectionStations.get(i).getLongitude(), inspectionStations.get(i).getLatitude())));
                distanceOrder[i] = bigDecimal.toPlainString();

                Log.d("msg", inspectionStations.get(i).getStationName() + "距离定位位置距离为： " + distanceOrder[i] + " 米");

                orderList[i] = distanceOrder[i];//将原来的顺序赋给orderList数组，即orderList数组为未排序的distanceOrder数组
//            Log.d("msg", "转换的距离为： " + distanceOrder[i] + "米");
            }
            Arrays.sort(distanceOrder); //对distanceOrder进行升序排序
            index = new int[inspectionStations.size()];
            index = getIndex(orderList,distanceOrder,inspectionStations.size());
        }
    }

    /**
     * 返回排序后的新序列在旧序列的下标
     *
     * @param a   排序前的String
     * @param b   排序后的String
     * @param num a，b的个数
     * @return
     */
    protected int[] getIndex(String[] a, String[] b, int num) {
        int[] o = new int[num];
        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                if (b[i].equals(a[j]))
                    o[i] = j;
            }
        }
        return o;
    }

    protected void getDistance() {
        if (null != orderList) {
            orderList1 = new double[orderList.length];
            Log.d("msg",orderList[1]);
            for (int i = 0; i < orderList.length; i++) {
                orderList1[i] =(Math.round(Double.parseDouble(orderList[i]) / 500) * 0.5);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 从接口获取数据
     *
     * @param url
     */
    private void getDataFromUrl(String url) {
        if (stationFromOkHttpList == null) {
            stationFromOkHttpList = new ArrayList<>();
        } else {
            stationFromOkHttpList.clear();
        }
        Request request = new Request.Builder()
                .url(url)
                .build();

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String result = response.body().string();
                parseJson(result);
                Message msg = new Message();
                msg.what = 0;
                handler.sendMessage(msg);
//                parseJsonByGson(result);
            }
        });
    }

    /**
     * 解析json格式数据
     *
     * @param json json格式的字符串
     */
    private void parseJson(String json) {
        try {
            JSONArray jStations = new JSONObject(json).getJSONArray("inspectStations");
            int jStationLength = jStations.length();
            Log.d("flow", jStationLength + " -- length");
            for (int i = 0; i < jStationLength; i++) {
                JSONObject jStation = jStations.getJSONObject(i);
                InspectionStation station = new InspectionStation();

                // 车检站纬度
                String lat = jStation.getString("lat");
                station.setLatitude(Double.parseDouble(lat));

                // 车检站经度
                String lng = jStation.getString("lng");
                station.setLongitude(Double.parseDouble(lng));


                // 车检站名字
                String name = jStation.getString("name");
                station.setStationName(name);

                // 车检站id
                int id = jStation.getInt("id");
                station.setId(id);

                stationFromOkHttpList.add(station);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
