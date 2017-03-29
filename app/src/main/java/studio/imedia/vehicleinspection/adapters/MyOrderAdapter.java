package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.RatingActivity;
import studio.imedia.vehicleinspection.bean.Order;

/**
 * Created by eric on 15/10/12.
 */
public class MyOrderAdapter extends BaseAdapter {

    private Context context;
    private List<Order> orderList;
    private LayoutInflater inflater;

    public MyOrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Order order = orderList.get(position);
        boolean isOrdered = order.isOrdered();
        if (null == convertView) {
            holder = new ViewHolder();
            if (isOrdered) {
                convertView = inflater.inflate(R.layout.item_ordered, null);
                holder.tvStation = (TextView) convertView.findViewById(R.id.tv_station);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
                holder.tvRated = (TextView) convertView.findViewById(R.id.tv_rated);
                holder.btnRate = (Button) convertView.findViewById(R.id.btn_rate);
            } else {
                convertView = inflater.inflate(R.layout.item_ordering, null);
                holder.tvStation = (TextView) convertView.findViewById(R.id.tv_station);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (isOrdered) {
            if (position % 2 == 0)
                holder.tvStation.setText(order.getStation() + "（自检）");
            else
                holder.tvStation.setText(order.getStation() + "（代检）");
            int year = order.getYear();
            int month = order.getMonth();
            int day = order.getDay();
            holder.tvDate.setText(year + "年" + month + "月" + day + "日");
            holder.tvPrice.setText("￥" + order.getPrice());
            boolean isRated = order.isRated();
            if (isRated) {
                holder.btnRate.setVisibility(View.INVISIBLE);
                holder.tvRated.setVisibility(View.VISIBLE);
            } else {
                holder.btnRate.setVisibility(View.VISIBLE);
                holder.tvRated.setVisibility(View.INVISIBLE);

                holder.btnRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, RatingActivity.class));
                    }
                });
            }
        } else {
            if (position % 3 == 0)
                holder.tvStation.setText(order.getStation() + "（代检）");
            else
                holder.tvStation.setText(order.getStation() + "（自检）");
            int year = order.getYear();
            int month = order.getMonth();
            int day = order.getDay();
            holder.tvDate.setText(year + "年" + month + "月" + day + "日");
            holder.tvPrice.setText("￥" + order.getPrice());
        }

        return convertView;
    }

    private class ViewHolder {
        private TextView tvStation;
        private TextView tvDate;
        private TextView tvPrice;
        private Button btnRate;
        private TextView tvRated;
    }
}
