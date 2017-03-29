package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.bean.CarFile;

/**
 * Created by eric on 15/10/9.
 */
public class MyCarFileAdapter extends BaseAdapter {

    private List<CarFile> carFileList;
    private Context context;
    private LayoutInflater inflater;

    public MyCarFileAdapter(Context context, List<CarFile> carFileList) {
        this.context = context;
        this.carFileList = carFileList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return carFileList.size();
    }

    @Override
    public Object getItem(int position) {
        return carFileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        CarFile carFile = carFileList.get(position);
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.item_car_files, null);
            holder = new ViewHolder();
            holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tvStation = (TextView) convertView.findViewById(R.id.tv_station);
            holder.imgState = (ImageView) convertView.findViewById(R.id.img_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvDate.setText(carFile.getDate());
        holder.tvStation.setText(carFile.getStation());
        if (carFile.getState()) {
            holder.imgState.setImageResource(R.drawable.icon_pass);
        } else {
            holder.imgState.setImageResource(R.drawable.icon_failed);
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tvDate;
        private TextView tvStation;
        private ImageView imgState;
    }

}
