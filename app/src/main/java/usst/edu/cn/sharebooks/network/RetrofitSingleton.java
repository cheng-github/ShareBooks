package usst.edu.cn.sharebooks.network;



import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import usst.edu.cn.sharebooks.BuildConfig;
import usst.edu.cn.sharebooks.Constants;
import usst.edu.cn.sharebooks.ShareApplication;
import usst.edu.cn.sharebooks.model.articlelist.ArticleContent.ArticleDetail;
import usst.edu.cn.sharebooks.model.articlelist.ArticleHeader.SimpleArticle;
import usst.edu.cn.sharebooks.model.articlelist.ArticleIDList;
import usst.edu.cn.sharebooks.model.donate.AllAvailableBook;
import usst.edu.cn.sharebooks.model.donate.DeleteDonateBookResponse;
import usst.edu.cn.sharebooks.model.donate.UserAddDonateBookResponse;
import usst.edu.cn.sharebooks.model.donate.UserPersonalDonateStallResponse;
import usst.edu.cn.sharebooks.model.douban.DouBanResponse;
import usst.edu.cn.sharebooks.model.historyorders.HistoryOrderItem;
import usst.edu.cn.sharebooks.model.order.OrderBookActionResponse;
import usst.edu.cn.sharebooks.model.order.OrderDealResultResponse;
import usst.edu.cn.sharebooks.model.order.PersonalOrderResponse;
import usst.edu.cn.sharebooks.model.search.SearchJiaoCaiResponse;
import usst.edu.cn.sharebooks.model.sellstall.AddSellBookResponse;
import usst.edu.cn.sharebooks.model.sellstall.AllUserSellStallResponse;
import usst.edu.cn.sharebooks.model.sellstall.DeleteSellResponse;
import usst.edu.cn.sharebooks.model.sellstall.OneUserSellStallResponse;
import usst.edu.cn.sharebooks.model.sellstall.SellBookStallList;
import usst.edu.cn.sharebooks.model.user.LoginResponse;
import usst.edu.cn.sharebooks.model.user.RegisterResponser;
import usst.edu.cn.sharebooks.model.user.UpdateUserInfoResponse;
import usst.edu.cn.sharebooks.ui.activity.MainActivity;
import usst.edu.cn.sharebooks.ui.activity.SplashActivity;
import usst.edu.cn.sharebooks.util.NetWorkUtil;
import usst.edu.cn.sharebooks.util.RxUtil;
import usst.edu.cn.sharebooks.util.ToastUtil;

//使用单例模式的网络访问
public class RetrofitSingleton {
    private static RetrofitSingleton sInstance;
    private static OkHttpClient sOkHttpClient;
    private static Retrofit sRetrofit;
    private static ApiInterface sApiInterface;
//调用网络请求的activity的引用
    private Context mContext;

    public Context getmContext() {
        return mContext;
    }

    public RetrofitSingleton setmContext(Context mContext) {
        this.mContext = mContext;
        return getInstance();
    }

    public static RetrofitSingleton getInstance(){
        if (sInstance == null){
            sInstance = new RetrofitSingleton();
        }
        return sInstance;
    }

    private RetrofitSingleton(){
        initOkHttp();//对okHttp进行相关设置
        //下面两句都是使用retrofit对okhttp进行了一些封装,最终的网络调用都是通过一个ApiInterface这个类实现的
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
        //设置超时异常处理拦截器,注意所有的拦截器都必须返回一个response对象,也就是在这个response对象里保存我们在拦截器里的相关设置
//        Interceptor timeOutInterceptor = new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                return onTimeOutIntercep(chain);
//            }
//        };
//        builder.addInterceptor(timeOutInterceptor);
        File cacheFile = new File(Constants.NET_CACHE);
        Cache cache = new Cache(cacheFile,1024*1024*20);//设置20M的缓存大小
        //设置缓存拦截器
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                //在没有网络的情况下直接使用缓存
                if (!NetWorkUtil.isNetWorkConnected(ShareApplication.getAppContext())) {
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
        if (BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new StethoInterceptor()); //添加facebook的网络debug框架
        }
        //设置超时
        builder.connectTimeout(60, TimeUnit.SECONDS);//尽量多设置一点时间,测试一下三十秒是否还是会跑出异常
        //依旧会抛出异常crashed掉我的程序
        //解决方法应该是增加网络超时这种情况的处理,以及为了避免网络差的环境下加载数据，增加TimeOut的连接时长是非常有必要的
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        builder.retryOnConnectionFailure(true);
        //错误重连  不设置错误重连
      //  builder.retryOnConnectionFailure(true);
        sOkHttpClient = builder.build();
//        其实retrofit的最大的作用就是使用简单的方式(注解)将不同的http访问封装到一个类里去
//        sOkHttpClient.newCall()
    }
    //注意subscribeOn()主要改变的是订阅的线程。即call()执行的线程
   // ObserveOn()主要改变的是发送的线程。即onNext()执行的线程。
    //这两点别搞混淆了

//    /**
//     * 网络连接超时处理捕获
//     * @return
//     */
//    private Response onTimeOutIntercep(Interceptor.Chain chain)throws IOException{
//        try{
//            Response response = chain.proceed(chain.request());
//            String content = "";
////            return response.newBuilder().body(ResponseBody.create(response.body().contentType(),content)).build();
//        }catch (SocketTimeoutException e){
//            e.printStackTrace();
////            开始进行超时情况的处理
////            ToastUtil.showShort("网络连接超时");
//            Log.e("Error","-------------------------------网络请求超时-------------------------------");
//            if (mContext instanceof SplashActivity)
//                ((SplashActivity)mContext).timeOutHanding();
//            else if (mContext instanceof MainActivity)
//                ((MainActivity) mContext).onTimeOutHanding();
//        }
//        return chain.proceed(chain.request());
//    }

    //下面开始调用Retrofit获取服务器数据
    public Observable<AllAvailableBook> fetchGivenBookData(){
        return sApiInterface.mGivenBookApi()
//                .flatMap(new Function<AllAvailableBook, ObservableSource<AllAvailableBook>>() {
//                    @Override
//                    public ObservableSource<AllAvailableBook> apply(@NonNull AllAvailableBook allAvailableBook) throws Exception {
//                        return Observable.just(allAvailableBook);
//                    }
//                })
                .compose(RxUtil.<AllAvailableBook>io());
                //注意这里使用泛型需要指名对象使用
                //简单直接获取数据的测试
    }

    public Observable<UserPersonalDonateStallResponse> getPersonalBookData(int userId){
        return sApiInterface.lookDonateBook(3,userId)
                .compose(RxUtil.<UserPersonalDonateStallResponse>io());
    }

    public Observable<UserAddDonateBookResponse> addDonateBookResponseObservable(Map<String,String> infs){
        return sApiInterface.addGivenBook(1,infs)
                .compose(RxUtil.<UserAddDonateBookResponse>io());
    }

    public Observable<DeleteDonateBookResponse> deleteDonateBookResponseObservable(String delBookName,int userId){
        return sApiInterface.deleteDonateBook(2,delBookName,userId)
                .compose(RxUtil.<DeleteDonateBookResponse>io())
//                .filter(new Predicate<DeleteDonateBookResponse>() {
//                    @Override
//                    public boolean test(@NonNull DeleteDonateBookResponse deleteDonateBookResponse) throws Exception {
//                        return false;
//                    }
//                })
                ;
    }

    public Observable<RegisterResponser> userRegister(Map<String,String> map){
        return sApiInterface.register(map)
                .compose(RxUtil.<RegisterResponser>io());
    }

    public Observable<LoginResponse> userLogin(String userName, String passWord){
        return sApiInterface.login(userName,passWord)
                .compose(RxUtil.<LoginResponse>io());
    }

    public Observable<SellBookStallList> getUserSellBookDatas(int bookStallId){
    return sApiInterface.getUserSellBookStallData(bookStallId)
            .compose(RxUtil.<SellBookStallList>io());
    }

    public Observable<DeleteSellResponse> deleteSellBookAction(Map<String,String> infs){
        return sApiInterface.postUserReqeustData(infs)
                .compose(RxUtil.<DeleteSellResponse>io());
    }

    public Observable<SearchJiaoCaiResponse> searchJiaoCaiResponseAction(Map<String,String> infs){
       return sApiInterface.postSearchJiaocaiReuqest(infs)
               .compose(RxUtil.<SearchJiaoCaiResponse>io());
    }

    public Observable<AddSellBookResponse> addSellBookResponseObservable(Map<String,String> infs){
        return sApiInterface.postAddSellBookAction(infs)
                .compose(RxUtil.<AddSellBookResponse>io());
    }

    public Observable<DouBanResponse> searchBookFromDouban(String keyWord){
        return sApiInterface.getBookInfoFromDouban(keyWord,12)
                .compose(RxUtil.<DouBanResponse>io());
    }

    public Observable<AllUserSellStallResponse> getAllUserSellStallInformaiton(){
        return sApiInterface.getAllSellBookStallAction()
                .compose(RxUtil.<AllUserSellStallResponse>io());
    }

    public Observable<UpdateUserInfoResponse> updateImageAction(int userId,String imageUrl,RequestBody requestBody){
        return sApiInterface.updateUserImage(1,userId,imageUrl,requestBody)
                .compose(RxUtil.<UpdateUserInfoResponse>io());
    }

    public Observable<UpdateUserInfoResponse> updateUserOtherInfo(int way,int userId,String newStr){
        return sApiInterface.updateUserOtherInfo(way,userId,newStr)
                .compose(RxUtil.<UpdateUserInfoResponse>io());
    }

    public Observable<OrderBookActionResponse> orderBookActionResponseObservable(int requestWay,Map<String,String> infs){
        return sApiInterface.orderBook(requestWay,infs)
                .compose(RxUtil.<OrderBookActionResponse>io());
    }

    public Observable<PersonalOrderResponse> orderListAcitonResponse(int requestWay,int userId){
        return sApiInterface.orderList(requestWay,userId)
                .compose(RxUtil.<PersonalOrderResponse>io());
    }

    public Observable<OrderDealResultResponse> donateOrderDealResponse(int result,Map<String,String> infs){
        return sApiInterface.donateOrderResultDeal(3,result,infs)
                .compose(RxUtil.<OrderDealResultResponse>io());
    }

    public Observable<OneUserSellStallResponse> loadOneUserAllSellBook(int userId){
        return sApiInterface.loadOneUserAllSellBook(1,userId)
                .compose(RxUtil.<OneUserSellStallResponse>io());
    }

    public Observable<ArticleIDList> getArticleIDList(){
        return sApiInterface.getArticleIDList().compose(RxUtil.<ArticleIDList>io());
    }

    public Observable<SimpleArticle> loadArticleHeader(String item_id){
        return sApiInterface.loadArticleHeader(item_id).compose(RxUtil.<SimpleArticle>io());
    };

    public Observable<ArticleDetail> loadArticleContent(String item_id){
        return sApiInterface.loadArticleContent(item_id).compose(RxUtil.<ArticleDetail>io());
    }

    public Observable<ArrayList<HistoryOrderItem>> loadHistoryOrdersInfo(int userId){
        return sApiInterface.getHistoryOrdersInfo(userId).compose(RxUtil.<ArrayList<HistoryOrderItem>>io());
    }
}
