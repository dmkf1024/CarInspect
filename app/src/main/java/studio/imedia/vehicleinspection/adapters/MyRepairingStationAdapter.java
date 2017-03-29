package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.bean.RepairingStation;

/**
 * Created by eric on 15/10/16.
 */
public class MyRepairingStationAdapter extends BaseAdapter {

    private Context context;
    private List<RepairingStation> repairingStationList;
    private LayoutInflater inflater;

    public MyRepairingStationAdapter(Context context, List<RepairingStation> repairingStationList) {
        this.context = context;
        this.repairingStationList = repairingStationList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return repairingStationList.size();
    }

    @Override
    public Object getItem(int position) {
        return repairingStationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RepairingStation station = repairingStationList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_repairing_station, null);

            holder.imgStation = (ImageView) convertView.findViewById(R.id.img_station);
            holder.tvStationName = (TextView) convertView.findViewById(R.id.tv_station_name);
            holder.tvStationAddress = (TextView) convertView.findViewById(R.id.tv_station_address);
            holder.tvStationDistance = (TextView) convertView.findViewById(R.id.tv_station_distance);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgStation.setImageBitmap(station.getStationPic());
        holder.tvStationName.setText(station.getStationName());
        holder.tvStationAddress.setText(station.getStationAddress());
        holder.tvStationDistance.setText(station.getStationDistance() + "km");

        return convertView;
    }

    private class ViewHolder {
        private ImageView imgStation;
        private TextView tvStationName;
        private TextView tvStationAddress;
        private TextView tvStationDistance;
    }
}
