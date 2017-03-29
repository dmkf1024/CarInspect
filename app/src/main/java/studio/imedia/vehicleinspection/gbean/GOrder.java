package studio.imedia.vehicleinspection.gbean;

/**
 * Created by eric on 15/11/29.
 */
public class GOrder {
    private int id;
    private int isPass;
    private int isProxy;
    private String name;
    private int orderStatus;
    private String orderTime;
    private int price;
    private String stationPic;

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
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
}
