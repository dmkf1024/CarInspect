package studio.imedia.vehicleinspection.retrofitbean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class UserBean implements Parcelable {

    /**
     * gender : 0
     * signature : 我的个性签名！
     * city : 0
     * county : 0
     * avatar :
     * score : 0
     * province :
     * cityName :
     * detailedAddress : 浙江省杭州市
     * name : cooffee
     * id : 10
     * provinceName :
     * countyName :
     */

    private int gender;
    private String signature;
    private int city;
    private int county;
    private int province;
    private String provinceName;
    private String countyName;
    private String cityName;
    private String avatar;
    private int score;
    private String detailedAddress;
    private String name;
    private int id;

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getCounty() {
        return county;
    }

    public void setCounty(int county) {
        this.county = county;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDetailedAddress() {
        return detailedAddress;
    }

    public void setDetailedAddress(String detailedAddress) {
        this.detailedAddress = detailedAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.gender);
        dest.writeString(this.signature);
        dest.writeInt(this.city);
        dest.writeInt(this.county);
        dest.writeString(this.avatar);
        dest.writeInt(this.score);
        dest.writeInt(this.province);
        dest.writeString(this.cityName);
        dest.writeString(this.detailedAddress);
        dest.writeString(this.name);
        dest.writeInt(this.id);
        dest.writeString(this.provinceName);
        dest.writeString(this.countyName);
    }

    public UserBean() {
    }

    protected UserBean(Parcel in) {
        this.gender = in.readInt();
        this.signature = in.readString();
        this.city = in.readInt();
        this.county = in.readInt();
        this.avatar = in.readString();
        this.score = in.readInt();
        this.province = in.readInt();
        this.cityName = in.readString();
        this.detailedAddress = in.readString();
        this.name = in.readString();
        this.id = in.readInt();
        this.provinceName = in.readString();
        this.countyName = in.readString();
    }

    public static final Parcelable.Creator<UserBean> CREATOR = new Parcelable.Creator<UserBean>() {
        @Override
        public UserBean createFromParcel(Parcel source) {
            return new UserBean(source);
        }

        @Override
        public UserBean[] newArray(int size) {
            return new UserBean[size];
        }
    };
}
