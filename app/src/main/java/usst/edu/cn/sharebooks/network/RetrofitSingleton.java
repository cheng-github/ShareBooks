package usst.edu.cn.sharebooks.network;



import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import usst.edu.cn.sharebooks.Constants;
import usst.edu.cn.sharebooks.ShareApplication;
import usst.edu.cn.sharebooks.model.AllAvailableBook;
import usst.edu.cn.sharebooks.util.NetWorkUtil;
import usst.edu.cn.sharebooks.util.RxUtil;

//使用单例模式的网络访问
public class RetrofitSingleton {
    private static RetrofitSingleton sInstance;
    private static OkHttpClient sOkHttpClient;
    private static Retrofit sRetrofit;
    private static ApiInterface sApiInterface;

    public static RetrofitSingleton getInstance(){
        if (sInstance == null){
            sInstance = new RetrofitSingleton();
        }
        return sInstance;
    }

    private RetrofitSingleton(){
        initOkHttp();
        initRetrofit();
        sApiInterface = sRetrofit.create(ApiInterface.class);
    }

    private void initRetrofit(){
        sRetrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface.HOST)
                .client(sOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private void initOkHttp(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        File cacheFile = new File(Constants.NET_CACHE);
        Cache cache = new Cache(cacheFile,1024*1024*20);//设置20M的缓存大小
        //设置缓存拦截器
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //在没有网络的情况下直接使用缓存
                if (!NetWorkUtil.isNetWorkConnected(ShareApplication.getAppContext())){
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response =  chain.proceed(request);
                //存在网络的情况下的response的设置
                if (NetWorkUtil.isNetWorkConnected(ShareApplication.getAppContext())){
                    int maxAge =10;
                    response.newBuilder()
                            .header("Cache-Control", "public, max-age=" + maxAge)
                            .removeHeader("Pragma")
                            .build();
                }
                return response;
            }
        };
        builder.cache(cache).addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        sOkHttpClient = builder.build();
    }
    //注意subscribeOn()主要改变的是订阅的线程。即call()执行的线程
   // ObserveOn()主要改变的是发送的线程。即onNext()执行的线程。
    //这两点别搞混淆了

    //下面开始调用Retrofit获取服务器数据
    public Observable<AllAvailableBook> fetchGivenBookData(String bookName, String author){
        return sApiInterface.mGivenBookApi(bookName,author)
//                .flatMap(new Function<AllAvailableBook, ObservableSource<AllAvailableBook>>() {
//                    @Override
//                    public ObservableSource<AllAvailableBook> apply(@NonNull AllAvailableBook allAvailableBook) throws Exception {
//                        return Observable.just(allAvailableBook);
//                    }
//                })
                .compose(RxUtil.<AllAvailableBook>io()) //注意这里使用泛型需要指名对象使用
                ;//简单直接获取数据的测试
    }
}
