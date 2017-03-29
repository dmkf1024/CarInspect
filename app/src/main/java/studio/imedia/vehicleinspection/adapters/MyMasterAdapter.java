package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.bean.Master;
import studio.imedia.vehicleinspection.views.RoundImageView;

/**
 * Created by eric on 15/10/25.
 */
public class MyMasterAdapter extends BaseAdapter {

    private Context mContext;
    private List<Master> mMasterList;
    private LayoutInflater mInflater;

    public MyMasterAdapter(Context context, List<Master> masterList) {
        mContext = context;
        mMasterList = masterList;
        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return mMasterList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMasterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MyMasterViewHolder holder = null;
        Master master = mMasterList.get(position);
        if (null == convertView) {
            holder = new MyMasterViewHolder();
            convertView = mInflater.inflate(R.layout.item_master_parent, null);

            holder.imgAvatar = (RoundImageView) convertView.findViewById(R.id.img_avatar);
            holder.tvMasterName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.rating_bar);
            holder.tvServiceAmount = (TextView) convertView.findViewById(R.id.tv_service_amount);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);

            convertView.setTag(holder);
        } else {
            holder = (MyMasterViewHolder) convertView.getTag();
        }

        holder.imgAvatar.setImageBitmap(master.getAvatar());
        holder.tvMasterName.setText(master.getName());
        holder.ratingBar.setNumStars(master.getStartNum());
        holder.tvServiceAmount.setText(master.getServiceAmount() + "次服务");
        holder.tvPrice.setText(master.getPrice() + "");
        return convertView;
    }

    private class MyMasterViewHolder {
        private RoundImageView imgAvatar;
        private TextView tvMasterName;
        private RatingBar ratingBar;
        private TextView tvServiceAmount;
        private TextView tvPrice;
    }
}
