package studio.imedia.vehicleinspection.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.interfaces.RVListener;
import studio.imedia.vehicleinspection.pojo.Constant;
import studio.imedia.vehicleinspection.retrofitbean.OrderBean;

/**
 * Created by eric on 15/10/9.
 */
public class MyCarFileAdapter extends RecyclerView.Adapter {

    private List<OrderBean> carFileList = new ArrayList<>();
    private Context mContext;

    private RVListener mListener;

    public MyCarFileAdapter(Context context, List<OrderBean> carFileList) {
        this.mContext = context;

        if (carFileList != null && carFileList.size() > 0) {
            carFileList.addAll(carFileList);
        }
    }

    public void setOnItemClickListener(RVListener listener) {
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_car_files, parent, false);
        final MyViewHolder holder = new MyViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                mListener.onItemClick(v, position);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderBean carFile = carFileList.get(position);
        ((MyViewHolder)holder).initData(carFile);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return carFileList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_station)
        TextView tvStation;
        @BindView(R.id.img_enter)
        ImageView imgEnter;
        @BindView(R.id.img_state)
        ImageView imgState;

        public MyViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        public void initData(OrderBean carFile) {

            String dateTime = carFile.getOrderTime();
            String date = dateTime.substring(0, dateTime.indexOf(" "));
            tvDate.setText(date);
            String station = carFile.getName();
            tvStation.setText(station);
            int status = carFile.getOrderStatus();
            if (status == Constant.Status.ORDER_PASS) {
                imgState.setImageResource(R.drawable.icon_pass);
            } else {
                imgState.setImageResource(R.drawable.icon_failed);
            }
        }
    }
}
