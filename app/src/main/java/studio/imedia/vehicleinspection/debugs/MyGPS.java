package studio.imedia.vehicleinspection.debugs;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * Created by eric on 15/11/14.
 */
public class MyGPS {

    private double latitude;
    private double longitude;

    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;

    public MyGPS(Context context) {
        aMap = new AMap();
        mAMapLocationManager = LocationManagerProxy.getInstance(context);
        mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, aMap);
    }

    class AMap implements AMapLocationListener {

        @Override
        public void onLocationChanged(AMapLocation location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("haha", "insides:" + latitude + "--" + longitude);
        }

        @Override
        public void onLocationChanged(Location location) {

            Log.d("haha", "onLocationChanged __" + location.toString());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("haha", "onStatusChanged");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("haha", "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("haha", "onProviderdisabled");
        }
    }

    public void destroyAMapLocationListener() {
        mAMapLocationManager.removeUpdates(aMap);
        mAMapLocationManager.destroy();
        mAMapLocationManager = null;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "MyGPS [latitude: " + latitude + ", longitude: " + longitude + "]";
    }
}
