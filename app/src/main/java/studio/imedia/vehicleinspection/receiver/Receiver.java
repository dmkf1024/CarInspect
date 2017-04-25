package studio.imedia.vehicleinspection.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Receiver extends BroadcastReceiver {

    private double latitude;
    private double longitude;
    private int[] index;
    private double[] distance;
    private LatLongListner latLongListner;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        } else {
            Bundle bundle = intent.getExtras();
            latitude = bundle.getDouble("纬度");
            longitude = bundle.getDouble("经度");
            index = bundle.getIntArray("下标");
            distance = bundle.getDoubleArray("距离");
            latLongListner.OnReceived(latitude, longitude, index, distance);
            Log.d("msg", "经纬度：" + latitude + "  " + longitude + " " + index[0] + " " + distance[index[0]]);
        }

    }

    public interface LatLongListner {
        void OnReceived(double latitude, double longitude, int[] index, double[] distance);

    }

    public void setOnReceivveLatLongListner(LatLongListner latLongListner) {
        this.latLongListner = latLongListner;
    }

}
