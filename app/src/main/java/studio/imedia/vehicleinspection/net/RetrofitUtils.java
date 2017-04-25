package studio.imedia.vehicleinspection.net;

import android.content.Context;

import java.util.concurrent.TimeUnit;

//import okhttp3.HttpUrl;
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//import retrofit2.converter.gson.GsonConverterFactory;
//import rx.Observable;
//import rx.Subscriber;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//import studio.imedia.vehicleinspection.pojo.Constant;

/**
 * Created by 代码咖啡 on 17/4/6
 * <p>
 * Email: wjnovember@icloud.com
 */

public class RetrofitUtils {
//
//    private static RetrofitUtils mInstance;
//
//    private static HttpLoggingInterceptor mInterceptor;
//    private static OkHttpClient.Builder mClientBuilder;
//    private static Retrofit.Builder mRetrofitBuilder;
//
//
//    private static RetrofitAPI mApi;
//
//    private static Observable mObservable;
//
//    private RetrofitUtils() {
//
//    }
//
//    public static RetrofitUtils getInstance() {
//        if (mInstance == null) {
//            synchronized (RetrofitUtils.class) {
//                if (mInstance == null) {
//                    mInstance = new RetrofitUtils();
//                    mInterceptor = new HttpLoggingInterceptor()
//                            .setLevel(HttpLoggingInterceptor.Level.BODY);
//                    mClientBuilder = new OkHttpClient.Builder()
//                            .addInterceptor(mInterceptor);
//                    mRetrofitBuilder = new Retrofit.Builder()
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .addCallAdapterFactory(RxJavaCallAdapterFactory.create());
//                }
//            }
//        }
//        mClientBuilder.readTimeout(Constant.Net.READ_TIME_OUT, TimeUnit.SECONDS)
//                .connectTimeout(Constant.Net.CONNECT_TIME_OUT, TimeUnit.SECONDS);
//        return mInstance;
//    }
//
//    /**
//     * 设置基本Url
//     *
//     * @param url
//     * @return
//     */
//    private static RetrofitUtils baseUrl(String url) {
//        mRetrofitBuilder.baseUrl(url);
//        return mInstance;
//    }
//
//    /**
//     * 设置基本url
//     *
//     * @param url
//     * @return
//     */
//    private static RetrofitUtils baseUrl(HttpUrl url) {
//        mRetrofitBuilder.baseUrl(url);
//        return mInstance;
//    }
//
//    /**
//     * 设置读取超时
//     *
//     * @param seconds
//     * @return
//     */
//    public static RetrofitUtils readTimeOut(int seconds) {
//        mClientBuilder.readTimeout(seconds, TimeUnit.SECONDS);
//        return mInstance;
//    }
//
//    /**
//     * 设置连接超时
//     *
//     * @param seconds
//     * @return
//     */
//    public static RetrofitUtils connectTimeOut(int seconds) {
//        mClientBuilder.connectTimeout(seconds, TimeUnit.SECONDS);
//        return mInstance;
//    }
//
//    /**
//     * 构建
//     *
//     * @return
//     */
////    private static RetrofitUtils build() {
////        mRetrofitBuilder.client(mClientBuilder.build())
////                .build();
////        return mInstance;
////    }
//
//    /**
//     * 创建请求
//     *
//     * @return
//     */
//    private static RetrofitUtils create(Context context) {
//        baseUrl(Constant.Net.BASE_URL);
//        if (mApi == null) {
//            mApi = mRetrofitBuilder.client(mClientBuilder.build())
//                    .build()
//                    .create(RetrofitAPI.class);
//        }
//        return mInstance;
//    }
//
//    /**
//     * 登录
//     *
//     * @param context
//     * @param phone
//     * @param pwd
//     * @param subscriber
//     * @return
//     */
//    public static RetrofitUtils login(Context context, String phone, String pwd, Subscriber subscriber) {
//        create(context);
//        mObservable = mApi.login(phone, pwd);
//        config(subscriber);
//        return mInstance;
//    }
//
//
//    /**
//     * 配置被观察者
//     *
//     * @param subscriber
//     */
//    private static void config(Subscriber subscriber) {
//        if (mObservable != null) {
//            mObservable.subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(subscriber);
//        }
//    }


}
