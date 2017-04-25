package studio.imedia.vehicleinspection.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import studio.imedia.vehicleinspection.activity.OrderInfoActivity;
import studio.imedia.vehicleinspection.R;
import studio.imedia.vehicleinspection.adapters.MyOrderAdapter;
import studio.imedia.vehicleinspection.bean.Order;
import studio.imedia.vehicleinspection.gbean.GOrder;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderingFragment extends Fragment implements AdapterView.OnItemClickListener {

    private TextView tvNoOrder;
    private ListView lvOrdering;
    private List<Order> mOrderList = new ArrayList<>();

    private MyOrderAdapter myOrderAdapter;

    public OrderingFragment(List<GOrder> gOrderList) {
        for (GOrder gOrder : gOrderList) {
            Order order = new Order();
            order.setStation(gOrder.getName());
            String orderTime = gOrder.getOrderTime();
            String yearStr = orderTime.substring(0, orderTime.indexOf("-"));
            int year = Integer.parseInt(yearStr);
            int month = Integer.parseInt(orderTime.substring(orderTime.indexOf("-") + 1, orderTime.lastIndexOf("-")));
            int day = Integer.parseInt(orderTime.substring(orderTime.lastIndexOf("-") + 1, orderTime.indexOf(" ")));
            order.setYear(year);
            order.setMonth(month);
            order.setDay(day);
            order.setPrice(gOrder.getPrice());
            order.setIsOrdered(false);
            order.setIsRated(false);
            mOrderList.add(order);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ordering, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        findView(); // 关联控件
        initEvent(); // 初始化监听事件
        setAdapter(); // 将数据放入adapter中
    }

    /**
     * 关联控件
     */
    private void findView() {
        lvOrdering = (ListView) getActivity().findViewById(R.id.lv_ordering);
        tvNoOrder = (TextView) getActivity().findViewById(R.id.tv_no_ordering_order);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {
        lvOrdering.setOnItemClickListener(this);
    }


    /**
     * 将数据放入adapter中
     */
    private void setAdapter() {
        if (mOrderList != null && mOrderList.size() > 0) {
            showList();
            if (null == myOrderAdapter) {
                myOrderAdapter = new MyOrderAdapter(getActivity(), mOrderList);
            }
            lvOrdering.setAdapter(myOrderAdapter);
        } else
            hideList();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_ordering:
                switch (position) {
                    default:
                        startActivity(new Intent(getActivity(), OrderInfoActivity.class));
                        break;
                }
                break;
        }
    }

    /**
     * 隐藏列表
     */
    private void hideList() {
        tvNoOrder.setVisibility(View.VISIBLE);
        lvOrdering.setVisibility(View.GONE);
    }

    /**
     * 显示列表
     */
    private void showList() {
        tvNoOrder.setVisibility(View.GONE);
        lvOrdering.setVisibility(View.VISIBLE);
    }
}
