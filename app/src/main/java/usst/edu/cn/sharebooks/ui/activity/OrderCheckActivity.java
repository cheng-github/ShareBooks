package usst.edu.cn.sharebooks.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.trello.rxlifecycle2.android.ActivityEvent;
import java.util.ArrayList;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenHistoryOrderEvent;
import usst.edu.cn.sharebooks.model.historyorders.HistoryOrderItem;
import usst.edu.cn.sharebooks.model.historyorders.HtyUserInfo;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.HistoryOrdersAdapter;


public class OrderCheckActivity extends BaseActivity {
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private HistoryOrdersAdapter mAdapter;
    private User mUser;
    private View mNotice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RxBus.getInstance().tObservable(OpenHistoryOrderEvent.class)
                .doOnNext(new Consumer<OpenHistoryOrderEvent>() {
                    @Override
                    public void accept(OpenHistoryOrderEvent openHistoryOrderEvent) throws Exception {
                        openHistoryOrders(openHistoryOrderEvent.getUserInfo());
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordercheck);
        mUser = (User)getIntent().getSerializableExtra("User");
        setupToolBars();
        initMainViews();
        loadHistoryOrders();
    }

    private void setupToolBars(){
        mToolbar = (Toolbar)findViewById(R.id.tb_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initMainViews(){
        mNotice = findViewById(R.id.notice);
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.srl_refresh);
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadHistoryOrders();
                    }
                },1000);
            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_checkorders);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(OrderCheckActivity.this));
        mAdapter = new HistoryOrdersAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    //常规的加载历史订单操作
    private void loadHistoryOrders(){
        RetrofitSingleton.getInstance().loadHistoryOrdersInfo(mUser.getId())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Error","加载历史订单发送错误");
                    }
                })
                .doOnNext(new Consumer<ArrayList<HistoryOrderItem>>() {
                    @Override
                    public void accept(ArrayList<HistoryOrderItem> historyOrderItems) throws Exception {
//                        for (HistoryOrderItem item : historyOrderItems)
//                        Log.i("CheckOrders",item.bookName);
                        mAdapter.setLists(historyOrderItems);
                        mAdapter.notifyDataSetChanged();
                        if (historyOrderItems.size()==0)
                            mNotice.setVisibility(View.VISIBLE);
                        else
                            mNotice.setVisibility(View.INVISIBLE);
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        //算了，不加什么加载提示了，无聊
                        mSwipeRefresh.setRefreshing(false);
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void openHistoryOrders(HtyUserInfo userInfo){
        Intent intent = new Intent(OrderCheckActivity.this,HistoryOrdersDetailActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("UserInfo",userInfo);
        intent.putExtras(args);
        startActivity(intent);
    }
}
