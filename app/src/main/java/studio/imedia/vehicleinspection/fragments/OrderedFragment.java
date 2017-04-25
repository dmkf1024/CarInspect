package studio.imedia.vehicleinspection.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyOrderAdapter;
import studio.imedia.vehicleinspection.bean.Order;
import studio.imedia.vehicleinspection.gbean.GOrder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderedFragment extends Fragment {

    @BindView(R.id.lv_ordered)
    ListView lvOrdered;
    @BindView(R.id.tv_no_ordered_order)
    TextView tvNoOrderedOrder;
    private List<Order> mOrderList = new ArrayList<>();

    private MyOrderAdapter myOrderAdapter;

    public OrderedFragment() {
        // Required empty public constructor
    }

    public OrderedFragment(List<GOrder> gOrderList) {
        Log.d("orders", "ordered");
        for (GOrder gOrder : gOrderList) {
            Order order = new Order();
            order.setStation(gOrder.getName());
            String orderTime = gOrder.getOrderTime();
            int year = Integer.parseInt(orderTime.substring(0, orderTime.indexOf("-")));
            int month = Integer.parseInt(orderTime.substring(orderTime.indexOf("-") + 1, orderTime.lastIndexOf("-")));
            int day = Integer.parseInt(orderTime.substring(orderTime.lastIndexOf("-"), orderTime.indexOf(" ")));
            order.setYear(year);
            order.setMonth(month);
            order.setDay(day);
            order.setPrice(gOrder.getPrice());
            order.setIsOrdered(true);
            if (gOrder.getOrderStatus() >= 4)
                order.setIsRated(true);
            else
                order.setIsRated(false);
            mOrderList.add(order);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ordered, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView(); // 关联控件
        setAdapter(); // 将数据存入adapter中
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvOrdered = (ListView) getActivity().findViewById(R.id.lv_ordered);
        tvNoOrderedOrder = (TextView) getActivity().findViewById(R.id.tv_no_ordered_order);
    }

    /**
     * 将数据存入adapter中
     */
    private void setAdapter() {
        if (mOrderList != null && mOrderList.size() > 0) {
            showList();
            if (null == myOrderAdapter) {
                myOrderAdapter = new MyOrderAdapter(getActivity(), mOrderList);
            }
            lvOrdered.setAdapter(myOrderAdapter);
        } else
            hideList();
    }

    /**
     * 隐藏列表
     */
    private void hideList() {
        lvOrdered.setVisibility(View.GONE);
        tvNoOrderedOrder.setVisibility(View.VISIBLE);
    }

    /**
     * 显示列表
     */
    private void showList() {
        lvOrdered.setVisibility(View.VISIBLE);
        tvNoOrderedOrder.setVisibility(View.GONE);
    }
}
