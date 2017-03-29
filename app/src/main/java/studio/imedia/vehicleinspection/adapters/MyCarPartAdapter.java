package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.gbean.GPart;

/**
 * Created by eric on 15/12/10.
 */
public class MyCarPartAdapter extends BaseAdapter {

    private List<GPart> mPartList;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyCarPartAdapter(Context context, List<GPart> partList) {
        this.mPartList = partList;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mPartList.size();
    }

    @Override
    public Object getItem(int position) {
        return mPartList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        GPart part = mPartList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_car_part, null);
            holder.tvPartName = (TextView) convertView.findViewById(R.id.part_name);
            holder.tvPartNorm = (TextView) convertView.findViewById(R.id.part_norm);
            holder.tvPartPrice = (TextView) convertView.findViewById(R.id.price);
            holder.tvPartVipPrice = (TextView) convertView.findViewById(R.id.vip_price);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = part.getName();
        String norm = part.getNorm();
        String price = String.valueOf(part.getPrice());
        String vipPrice = String.valueOf(part.getVipPrice());

        holder.tvPartName.setText(name);
        holder.tvPartNorm.setText(norm);
        holder.tvPartPrice.setText(price);
        holder.tvPartPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvPartVipPrice.setText(vipPrice);

        return convertView;
    }

    private class ViewHolder {
        TextView tvPartName;
        TextView tvPartNorm;
        TextView tvPartPrice;
        TextView tvPartVipPrice;
    }
}
