package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.bean.Coupon;

/**
 * Created by eric on 15/10/12.
 */
public class MyCouponAdapter extends BaseAdapter {

    private Context context;
    private List<Coupon> couponList;
    private LayoutInflater inflater;

    public MyCouponAdapter(Context context, List<Coupon> couponList) {
        this.context = context;
        this.couponList = couponList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return couponList.size();
    }

    @Override
    public Object getItem(int position) {
        return couponList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Coupon coupon = couponList.get(position);
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_coupon, null);
            holder.tvPrice = (TextView) convertView.findViewById(R.id.tv_price);
            holder.tvCouponName = (TextView) convertView.findViewById(R.id.tv_coupon_name);
            holder.tvCitizen = (TextView) convertView.findViewById(R.id.tv_citizen);
            holder.tvDeadline = (TextView) convertView.findViewById(R.id.tv_deadline);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvPrice.setText(coupon.getPrice() + "");
        String couponName = coupon.getCouponName();
        if (couponName != null && !TextUtils.isEmpty(couponName))
            holder.tvCouponName.setText(coupon.getCouponName());
        else
            holder.tvCouponName.setText("优惠券");
        String city = coupon.getCity();
        if (city != null && !city.equals("null"))
            holder.tvCitizen.setText("·限" + coupon.getCity() + "用户体验使用");
        else
            holder.tvCitizen.setText("·无地区限制体验使用");
        int year = coupon.getYear();
        int month = coupon.getMonth();
        int day = coupon.getDay();
        holder.tvDeadline.setText("·" + year + "年" + month + "月" + day + "日前使用");
        return convertView;
    }

    private class ViewHolder {
        private TextView tvPrice;
        private TextView tvCouponName;
        private TextView tvCitizen;
        private TextView tvDeadline;
    }
}
