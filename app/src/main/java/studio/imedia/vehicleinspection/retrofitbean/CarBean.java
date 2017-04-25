package studio.imedia.vehicleinspection.retrofitbean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class CarBean implements Parcelable {


    /**
     * carPlateNum :
     * carFrameNum :
     * carBrand :
     * carType : 奔驰
     * registerTime : 2015-12-07 18:15:13
     * licensePic :
     * engineNum : 123421
     */

    private String carPlateNum;
    private String carFrameNum;
    private String carBrand;
    private String carType;
    private String registerTime;
    private String licensePic;
    private String engineNum;

    public String getCarPlateNum() {
        return carPlateNum;
    }

    public void setCarPlateNum(String carPlateNum) {
        this.carPlateNum = carPlateNum;
    }

    public String getCarFrameNum() {
        return carFrameNum;
    }

    public void setCarFrameNum(String carFrameNum) {
        this.carFrameNum = carFrameNum;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getLicensePic() {
        return licensePic;
    }

    public void setLicensePic(String licensePic) {
        this.licensePic = licensePic;
    }

    public String getEngineNum() {
        return engineNum;
    }

    public void setEngineNum(String engineNum) {
        this.engineNum = engineNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.carPlateNum);
        dest.writeString(this.carFrameNum);
        dest.writeString(this.carBrand);
        dest.writeString(this.carType);
        dest.writeString(this.registerTime);
        dest.writeString(this.licensePic);
        dest.writeString(this.engineNum);
    }

    public CarBean() {
    }

    protected CarBean(Parcel in) {
        this.carPlateNum = in.readString();
        this.carFrameNum = in.readString();
        this.carBrand = in.readString();
        this.carType = in.readString();
        this.registerTime = in.readString();
        this.licensePic = in.readString();
        this.engineNum = in.readString();
    }

    public static final Parcelable.Creator<CarBean> CREATOR = new Parcelable.Creator<CarBean>() {
        @Override
        public CarBean createFromParcel(Parcel source) {
            return new CarBean(source);
        }

        @Override
        public CarBean[] newArray(int size) {
            return new CarBean[size];
        }
    };
}
