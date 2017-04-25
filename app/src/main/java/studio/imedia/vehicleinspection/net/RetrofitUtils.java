package studio.imedia.vehicleinspection.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import studio.imedia.vehicleinspection.pojo.Constant;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class RetrofitUtils {

    private static RetrofitUtils mInstance;

    private static HttpLoggingInterceptor mInterceptor;
    private static OkHttpClient.Builder mClientBuilder;
    private static Retrofit.Builder mRetrofitBuilder;


    private static RetrofitAPI mApi;

    private static Observable mObservable;

    private RetrofitUtils() {

    }

    public static RetrofitUtils getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtils.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitUtils();
                    mInterceptor = new HttpLoggingInterceptor()
                            .setLevel(HttpLoggingInterceptor.Level.BODY);
                    mClientBuilder = new OkHttpClient.Builder()
                            .addInterceptor(mInterceptor);
                    mRetrofitBuilder = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
                }
            }
        }
        mClientBuilder.readTimeout(Constant.Net.READ_TIME_OUT, TimeUnit.SECONDS)
                .connectTimeout(Constant.Net.CONNECT_TIME_OUT, TimeUnit.SECONDS);
        return mInstance;
    }

    /**
     * 设置基本Url
     *
     * @param url
     * @return
     */
    public RetrofitUtils baseUrl(String url) {
        mRetrofitBuilder.baseUrl(url);
        return mInstance;
    }

    /**
     * 设置基本url
     *
     * @param url
     * @return
     */
    public RetrofitUtils baseUrl(HttpUrl url) {
        mRetrofitBuilder.baseUrl(url);
        return mInstance;
    }

    /**
     * 设置读取超时
     *
     * @param seconds
     * @return
     */
    public RetrofitUtils readTimeOut(int seconds) {
        mClientBuilder.readTimeout(seconds, TimeUnit.SECONDS);
        return mInstance;
    }

    /**
     * 设置连接超时
     *
     * @param seconds
     * @return
     */
    public RetrofitUtils connectTimeOut(int seconds) {
        mClientBuilder.connectTimeout(seconds, TimeUnit.SECONDS);
        return mInstance;
    }

    /**
     * 构建
     *
     * @return
     */
//    private static RetrofitUtils build() {
//        mRetrofitBuilder.client(mClientBuilder.build())
//                .build();
//        return mInstance;
//    }

    /**
     * 创建请求
     *
     * @return
     */
    private RetrofitUtils create() {
        if (mApi == null) {
            mApi = mRetrofitBuilder.client(mClientBuilder.build())
                    .build()
                    .create(RetrofitAPI.class);
        }
        return mInstance;
    }

    /**
     * 登录
     *
     * @param phone
     * @param pwd
     * @param subscriber
     * @return
     */
    public RetrofitUtils login(String phone, String pwd, Subscriber subscriber) {
        create();
        mObservable = mApi.login(phone, pwd);
        config(subscriber);
        return mInstance;
    }

    /**
     * 获取订单列表
     * @param id
     * @param subscriber
     * @return
     */
    public RetrofitUtils getOrderList(String id, Subscriber subscriber) {
        create();
        mObservable = mApi.getOrderList(id);
        config(subscriber);
        return mInstance;
    }

    /**
     * 获取爱车档案信息
     * @param orderId
     * @param subscriber
     * @return
     */
    public RetrofitUtils getArchivesInfo(String orderId, Subscriber subscriber) {
        create();
        mObservable = mApi.getArchivesInfo(orderId);
        config(subscriber);
        return mInstance;
    }

    /**
     * 更新爱车信息
     * @param userId
     * @param carBrandId
     * @param carTypeId
     * @param engineNum
     * @param registerTime
     * @param provinceId
     * @param cityId
     * @param countyId
     * @param detailedAddress
     * @param subscriber
     * @return
     */
    public RetrofitUtils updateCarInfo(String userId, int carBrandId, int carTypeId, String engineNum,
                                        String registerTime, int provinceId, int cityId, int countyId,
                                        String detailedAddress, Subscriber subscriber) {
        create();
        mObservable = mApi.updateCarInfo(userId, carBrandId + "", carTypeId + "", engineNum, registerTime,
                provinceId + "", cityId + "", countyId + "", detailedAddress);
        config(subscriber);
        return mInstance;
    }


    /**
     * 配置被观察者
     *
     * @param subscriber
     */
    private void config(Subscriber subscriber) {
        if (mObservable != null) {
            mObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(subscriber);
        }
    }


}
