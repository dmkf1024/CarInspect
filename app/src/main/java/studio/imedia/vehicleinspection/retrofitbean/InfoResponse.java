package studio.imedia.vehicleinspection.retrofitbean;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class InfoResponse extends BaseResponse {

    private UserBean user;
    private CarBean car;

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public CarBean getCar() {
        return car;
    }

    public void setCar(CarBean car) {
        this.car = car;
    }
}
