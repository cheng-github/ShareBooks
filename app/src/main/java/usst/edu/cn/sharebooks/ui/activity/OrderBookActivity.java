package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.drakeet.materialdialog.MaterialDialog;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.order.OrderDealResultResponse;
import usst.edu.cn.sharebooks.model.order.PersonalOrderItem;
import usst.edu.cn.sharebooks.model.order.PersonalOrderResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.OrderBookAdapter;
import usst.edu.cn.sharebooks.ui.view.SpaceItemDecoration;
import usst.edu.cn.sharebooks.util.DialogUtil;


    public class OrderBookActivity extends BaseActivity {
        private User mUser;
        private Toolbar mToolBar;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private RecyclerView mRecyclerView;
        private OrderBookAdapter mAdapter;
        private boolean isManuRefresh = false;
        private MaterialDialog mMaterialDialog;
        private boolean autoRefresh = false;
        private View mNotice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderbook_activity);
        initDataFromLastActivity();
        initView();
        loadOrderBooksData();
    }

    private void initDataFromLastActivity(){
        mUser = (User) getIntent().getSerializableExtra("User");
    }

    private void initView(){
        setupToolBar();
        mNotice = findViewById(R.id.tv_notice);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_swiperefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isManuRefresh = true;
                        loadOrderBooksData();
                    }
                },1000);
            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OrderBookAdapter(new OrderBookAdapter.OrderAction() {
            @Override
            public void dealAction(int way, PersonalOrderItem item) {
                switch (way){ //1表示交易成功的结果 2表示交易失败
                    case 1:
                  //   Log.i("TestNormal","item.bookName---------from dealAction"+item.bookName);
                    //    Toast.makeText(OrderBookActivity.this,"成功调用",Toast.LENGTH_SHORT).show();
                        ensureBookResult(1,item);
                        break;
                    case 2:
                 //   Log.i("TestNormal","item.bookName---------from dealAction"+item.bookName);
                        ensureBookResult(0,item);
                    //    Toast.makeText(OrderBookActivity.this,"失败调用",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, OrderBookActivity.this);
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addItemDecoration(new SpaceItemDecoration(30));  别加这个间距了，加了贼丑
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

//原来锅在这里....final....难怪值没有变化  所以...长点记性...吧
//    private void showDealResultDialog(int choose,final PersonalOrderItem item){
//        switch (choose){
//            case 1:
//                if(mMaterialDialog == null){
//                    mMaterialDialog = new MaterialDialog(this);
//                }
//                mMaterialDialog.setMessage("设置贡献结果为成功吗?")
//                        .setPositiveButton("确认", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ensureBookResult(1,item);
//                                mMaterialDialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton("取消", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mMaterialDialog.dismiss();
//                            }
//                        })
//                        .setCanceledOnTouchOutside(true)
//                        .show();
//                break;
//            case  0:
//                if(mMaterialDialog == null){
//                    mMaterialDialog = new MaterialDialog(this);
//                }
//                mMaterialDialog.setMessage("设置贡献结果为失败吗?")
//                        .setPositiveButton("确认", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ensureBookResult(0,item);
//                                mMaterialDialog.dismiss();
//                            }
//                        })
//                        .setNegativeButton("取消", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mMaterialDialog.dismiss();
//                            }
//                        })
//                        .setCanceledOnTouchOutside(true)
//                        .show();
//            //    ensureBookResult(0,item);
//                break;
//        }
//    }

    private void setupToolBar(){
        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //   mToolBar.setTitle("所有订单");
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadOrderBooksData(){
        RetrofitSingleton.getInstance().orderListAcitonResponse(2,mUser.getId())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if (!isManuRefresh)
                        DialogUtil.showTriangleDialogForLoading(OrderBookActivity.this,"加载中...");
                        else
                            mSwipeRefreshLayout.setRefreshing(true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(OrderBookActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                }).doOnNext(new Consumer<PersonalOrderResponse>() {
                   @Override
                    public void accept(@NonNull PersonalOrderResponse personalOrderResponse) throws Exception {
                       mAdapter.setOrderList(personalOrderResponse.orderList);
                       mAdapter.notifyDataSetChanged();
                       if (personalOrderResponse.orderList.size() == 0){
                           mNotice.setVisibility(View.VISIBLE);
                       }else {
                           mNotice.setVisibility(View.INVISIBLE);
                       }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (!isManuRefresh)
                        DialogUtil.hideDialogForLoading(OrderBookActivity.this);
                        else
                            mSwipeRefreshLayout.setRefreshing(false);
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();

    }

    private void ensureBookResult(int result,PersonalOrderItem item){
        //1表示交易成功  0表示交易失败
        Log.i("TestNormal","item.orderedUserId"+item.orderUserId);
        Log.i("TestNormal","mUser.getId()"+mUser.getId());
        Log.i("TestNormal","item.bookName"+item.bookName);
        switch (result){
            case 1:
                Map<String,String> infs = new HashMap<>();
                infs.put("UserId",mUser.getId()+"");
                infs.put("OrderId",item.orderUserId+"");
                infs.put("BookName",item.bookName);
                infs.put("Author",item.author);
                infs.put("Press",item.press);
                infs.put("Version",item.version);
                infs.put("ImageUrl",item.imagerUrl);
                infs.put("isJiaoCai",item.isJiaoCai+"");
                infs.put("OrderTime",item.orderDate);
                infs.put("OrderType",item.orderType+"");
            //    Log.i("TestNormal","item.orderUserId+"+item.orderUserId);
                RetrofitSingleton.getInstance().donateOrderDealResponse(1,infs)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                DialogUtil.showTriangleDialogForLoading(OrderBookActivity.this,"加载中...");
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Toast.makeText(OrderBookActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .doOnNext(new Consumer<OrderDealResultResponse>() {
                            @Override
                            public void accept(@NonNull OrderDealResultResponse orderDealResultResponse) throws Exception {
                                Toast.makeText(OrderBookActivity.this,orderDealResultResponse.status,Toast.LENGTH_SHORT).show();
                                if (orderDealResultResponse.status.equals("处理订单成功")){
                                    autoRefresh = true;
                                }else {
                                    autoRefresh = false;
                                }
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                            DialogUtil.hideDialogForLoading(OrderBookActivity.this);
                                if (autoRefresh){
                                    loadOrderBooksData();
                                }
                            }
                        })
                        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe();
                break;
            case 0:
                Map<String,String> infs2 = new HashMap<>();
                infs2.put("UserId",mUser.getId()+"");
                infs2.put("BookName",item.bookName);
            //    infs2.put("OrderType",item.orderType+""); 不需要加
               Log.i("TestNormal","mUser.getId()="+mUser.getId());
               Log.i("TestNormal","item.orderUserId"+item.orderUserId);
                RetrofitSingleton.getInstance().donateOrderDealResponse(0,infs2)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                DialogUtil.showTriangleDialogForLoading(OrderBookActivity.this,"加载中...");
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                Toast.makeText(OrderBookActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .doOnNext(new Consumer<OrderDealResultResponse>() {
                            @Override
                            public void accept(@NonNull OrderDealResultResponse orderDealResultResponse) throws Exception {
                                Toast.makeText(OrderBookActivity.this,orderDealResultResponse.status,Toast.LENGTH_SHORT).show();
                                if (orderDealResultResponse.status.equals("处理订单成功")){
                                    autoRefresh = true;
                                }else {
                                    autoRefresh = false;
                                }
                            }
                        })
                       .doOnComplete(new Action() {
                           @Override
                           public void run() throws Exception {
                               DialogUtil.hideDialogForLoading(OrderBookActivity.this);
                               if (autoRefresh){
                                   //关闭对话框再进行数据的刷新操作
                                   loadOrderBooksData();
                               }
                           }
                       })
                        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe();
                break;
        }
    }
}
