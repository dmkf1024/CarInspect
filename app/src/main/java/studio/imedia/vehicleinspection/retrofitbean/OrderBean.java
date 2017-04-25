package studio.imedia.vehicleinspection.retrofitbean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 代码咖啡 on 2017/4/18
 * <p>
 * Email: wjnovember@icloud.com
 */

public class OrderBean implements Parcelable {

    /**
     * orderTime : 2015-11-22 21:48:08
     * price : 40
     * stationPic :
     * name : 杭州警苑综合服务部第二车辆检测站
     * orderStatus : 1
     * id : 1
     * isPass : 0
     * isProxy : 1
     */

    private String orderTime;
    private int price;
    private String stationPic;
    private String name;
    private int orderStatus;
    private int id;
    private int isPass;
    private int isProxy;

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStationPic() {
        return stationPic;
    }

    public void setStationPic(String stationPic) {
        this.stationPic = stationPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIsPass() {
        return isPass;
    }

    public void setIsPass(int isPass) {
        this.isPass = isPass;
    }

    public int getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(int isProxy) {
        this.isProxy = isProxy;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.orderTime);
        dest.writeInt(this.price);
        dest.writeString(this.stationPic);
        dest.writeString(this.name);
        dest.writeInt(this.orderStatus);
        dest.writeInt(this.id);
        dest.writeInt(this.isPass);
        dest.writeInt(this.isProxy);
    }

    public OrderBean() {
    }

    protected OrderBean(Parcel in) {
        this.orderTime = in.readString();
        this.price = in.readInt();
        this.stationPic = in.readString();
        this.name = in.readString();
        this.orderStatus = in.readInt();
        this.id = in.readInt();
        this.isPass = in.readInt();
        this.isProxy = in.readInt();
    }

    public static final Parcelable.Creator<OrderBean> CREATOR = new Parcelable.Creator<OrderBean>() {
        @Override
        public OrderBean createFromParcel(Parcel source) {
            return new OrderBean(source);
        }

        @Override
        public OrderBean[] newArray(int size) {
            return new OrderBean[size];
        }
    };
}
