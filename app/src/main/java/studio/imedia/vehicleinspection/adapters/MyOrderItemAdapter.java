package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.gbean.GOrderItem;

/**
 * Created by eric on 15/12/9.
 */
public class MyOrderItemAdapter extends BaseAdapter {

    private List<GOrderItem> mList;
    private Context mContext;
    private LayoutInflater mInflater;

    public MyOrderItemAdapter(Context context, List<GOrderItem> list) {
        this.mContext = context;
        this.mList = list;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        GOrderItem orderItem = mList.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_order_submit, null);
            holder.tvItemName = (TextView) convertView.findViewById(R.id.item_name);
            holder.tvItemPrice = (TextView) convertView.findViewById(R.id.item_price);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String itemName = orderItem.getName();
        float itemPrice = orderItem.getPrice();
        DecimalFormat decimalFormat=new DecimalFormat(".00"); // 保留两位小数
        String itemPriceStr = decimalFormat.format(itemPrice);

        holder.tvItemName.setText(itemName);
        holder.tvItemPrice.setText("￥" + itemPriceStr);

        return convertView;
    }

    private class ViewHolder {
        TextView tvItemName;
        TextView tvItemPrice;
    }
}
