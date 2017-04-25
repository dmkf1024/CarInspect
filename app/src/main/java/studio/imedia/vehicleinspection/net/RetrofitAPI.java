package studio.imedia.vehicleinspection.net;


import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;
import studio.imedia.vehicleinspection.retrofitbean.response.BaseResponse;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public interface RetrofitAPI {

    @GET("getInfo.jsp")
    Observable<BaseResponse> login(
            @Query("phone") String phone,
            @Query("password") String password
    );

    @GET("getOrderListById.jsp")
    Observable<BaseResponse> getOrderList(
            @Query("id") String id
    );

    @GET("getArchivesInfoByOrderId.jsp")
    Observable<BaseResponse> getArchivesInfo(
            @Query("orderId") String orderId
    );

    @GET("updateCarInfo.jsp")
    Observable<BaseResponse> updateCarInfo(
            @Query("id") String userId,
            @Query("carBrandId") String carBrandId,
            @Query("carTypeId") String carTypeId,
            @Query("engineNum") String engineNum,
            @Query("registerTime") String registerTime,
            @Query("provinId") String provinceId,
            @Query("cityId") String cityId,
            @Query("countyId") String countyId,
            @Query("detailedAddress") String detailedAddress
    );

}
