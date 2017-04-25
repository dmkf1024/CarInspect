package studio.imedia.vehicleinspection.retrofitbean.response;

import java.util.List;

import studio.imedia.vehicleinspection.retrofitbean.OrderBean;
import studio.imedia.vehicleinspection.retrofitbean.response.BaseResponse;

/**
 * Created by 代码咖啡 on 2017/4/18
 * <p>
 * Email: wjnovember@icloud.com
 */

public class OrderListResponse extends BaseResponse {
    private List<OrderBean> orders;

    public List<OrderBean> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderBean> orders) {
        this.orders = orders;
    }
}
