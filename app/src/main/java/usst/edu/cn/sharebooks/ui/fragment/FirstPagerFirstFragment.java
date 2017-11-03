package usst.edu.cn.sharebooks.ui.fragment;

import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.trello.rxlifecycle2.android.FragmentEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseFragment;
import usst.edu.cn.sharebooks.model.donate.AllAvailableBook;
import usst.edu.cn.sharebooks.model.donate.GivenBookItem;
import usst.edu.cn.sharebooks.model.order.OrderBookActionResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.activity.MainActivity;
import usst.edu.cn.sharebooks.ui.adapter.GivenBookAdapter;
import usst.edu.cn.sharebooks.util.DialogUtil;

public class FirstPagerFirstFragment extends BaseFragment {
    private View rootView;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private ImageView mImageView;
    private GivenBookAdapter adapter;
    //Data
    List<GivenBookItem> givenBookItems = new ArrayList<>();
    //load 条件判断
    boolean isViewCreated = false;
    private User user;

    public static FirstPagerFirstFragment newInstance(User user){
        FirstPagerFirstFragment firstPagerFirstFragment = new FirstPagerFirstFragment();
        Bundle args = new Bundle();
        args.putSerializable("User",user);
        firstPagerFirstFragment.setArguments(args);
        return firstPagerFirstFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("TestLifeCycle","onCreateView() from FirstPagerFirstFragment");
        rootView = inflater.inflate(R.layout.firstpager_firstfragment,container,false);
        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiprefresh);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerview);
        mImageView = (ImageView)rootView.findViewById(R.id.iv_erro);
        this.user = (User)getArguments().getSerializable("User");
        return rootView;
    }

    //这个方法是在view layout视图加载后才调用的，也就是 onCreateView(...)方法之后
    //无返回值
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i("TestLifeCycle","onViewCreated() from FirstPagerFirstFragment");
        // load("all","all");
        initView();//是否在调用这个方法前加判断??
        loadData();
        isViewCreated = true;
    }

    private void initView(){
        if (mRefreshLayout != null){
            //设置加载的动态颜色
            mRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            mRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            mRefreshLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadData();//匿名内部类可以自由的调用其外部类的方法
                                }
                            },1000);
                        }
                    });
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       // adapter = new GivenBookAdapter(givenBookItems);
        //下面两句都需要修改
        adapter = new GivenBookAdapter(new GivenBookAdapter.OrderAction() {
            @Override
            public void order(GivenBookItem item) {
                if (item.donateUser_Id != user.getId())
                    orderDonate(item);
                else
                    Toast.makeText(getContext(),"无法预订自己的书籍",Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(adapter);
        //设置点击外部关闭
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                    if (null != viewCache) {
                        viewCache.smoothClose();
                    }
                }
                return false;
            }
        });
    }

    private void orderDonate(GivenBookItem item){
        int requestWay = 1;
        Map<String,String> infs = new HashMap<>();
        infs.put("UserId",user.getId()+"");
        infs.put("BookName",item.bookName);
        infs.put("Author",item.author);
        infs.put("Press",item.press);
        infs.put("BookIntro",item.bookIntro);
        infs.put("ImageUrl",item.imageUrl);
        infs.put("IsJiaoCai",item.isJiaoCai+"");
        infs.put("DonateUserId",item.donateUser_Id+"");
        infs.put("Version",item.version);
        RetrofitSingleton.getInstance().orderBookActionResponseObservable(requestWay,infs)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showTriangleDialogForLoading(getContext(),"加载中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(getContext(),"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<OrderBookActionResponse>() {
                    @Override
                    public void accept(@NonNull OrderBookActionResponse orderBookActionResponse) throws Exception {
                        Toast.makeText(getContext(),orderBookActionResponse.status,Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(getContext());
                        loadData();
                    }
                })
                .compose(this.bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe();
    }

    private void loadData(){
        RetrofitSingleton.getInstance().fetchGivenBookData()
                .compose(this.<AllAvailableBook>bindUntilEvent(FragmentEvent.DESTROY))//记得要绑定生命周期
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        mRefreshLayout.setVisibility(View.INVISIBLE);
                        mImageView.setVisibility(View.VISIBLE);
                    }
                })
                .doOnNext(new Consumer<AllAvailableBook>() {
                    @Override
                    public void accept(@NonNull AllAvailableBook allAvailableBook) throws Exception {
                        //givenBookItems = allAvailableBook.shareBook;
                        //不调用这两句就无法成功更新数据
                        adapter.setList(allAvailableBook.shareBook);
                        adapter.notifyDataSetChanged();
//                        for (GivenBookItem book:givenBookItems){
//                            Log.i("Test",book.imageUrl.toString());
//                        }
                        Log.i("Test","doOnNext()");
                        Log.d("Test","ViewRootImpl "+ Process.myPid()+" Thread: "+ Process.myTid()+" name "+Thread.currentThread().getName());
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mRefreshLayout.setRefreshing(false);
                        Log.i("Test","doOnComplete()");
                        Log.d("Test","ViewRootImpl "+ Process.myPid()+" Thread: "+ Process.myTid()+" name "+Thread.currentThread().getName());
                        Log.i("Test","数据加载完毕");
                        //  ToastUtil.showShort("数据加载完毕.");
                    }
                })
                .subscribe();
    }

    @Override
    protected void loadWhenVisible() {
        //这个的作用是不是用于加载数据????
        //这个问题  以后再去弄懂吧
//        Log.i("TestLifeCycle","loadWhenVisible() from FirstPagerFirstFragment 1");
//        //这么说来...这个嵌套在MainFirstFragmnet里的fragmnet 居然是调用这个方法在调用onCreateView()和onViewCreated()之前
//        if (isViewCreated){
//            Log.i("TestLifeCycle","loadWhenVisible() from FirstPagerFirstFragment 2");
//            load("all","all");
//        }
        //这个还是不要用的好  切换一次 就加载一次  哪有那么多流量
    }
}
