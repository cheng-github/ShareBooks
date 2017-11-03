package usst.edu.cn.sharebooks.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenNormalBookDetailEventForSell;
import usst.edu.cn.sharebooks.model.event.normalbookmodel.NormalBookData;
import usst.edu.cn.sharebooks.model.order.OrderBookActionResponse;
import usst.edu.cn.sharebooks.model.sellstall.OneUserSellStallResponse;
import usst.edu.cn.sharebooks.model.sellstall.UserSellStallItemForOthers;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.UserSellStallItemAdapter;
import usst.edu.cn.sharebooks.util.DialogUtil;
import usst.edu.cn.sharebooks.util.RxUtil;

public class UserSellStallListActivity extends BaseActivity {
    private User mUser;
    private int mUserId;
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private UserSellStallItemAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean isManuRresh = false;
    private boolean autoRefresh  = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        RxBus.getInstance().tObservable(OpenNormalBookDetailEventForSell.class)
                .doOnNext(new Consumer<OpenNormalBookDetailEventForSell>() {
                    @Override
                    public void accept(@NonNull OpenNormalBookDetailEventForSell openNormalBookDetailEventForSell) throws Exception {
                        openBookDetail(openNormalBookDetailEventForSell);
                    }
                })
                .compose(RxUtil.<OpenNormalBookDetailEventForSell>io())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usersellstall_detail);
        initDatasFromLastActivity();
        initView();
        loadBooksData(mUserId);
    }

    private void initDatasFromLastActivity(){
        mUserId = getIntent().getIntExtra("UserId",0);
      //  Log.i("TestNormal","mUserId="+mUserId);
        mUser = (User)getIntent().getSerializableExtra("User");
    }

    private void initView(){
        setupToolBar();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swl_swipefresh);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mSwipeRefreshLayout.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isManuRresh = true;
                                loadBooksData(mUserId);
                            }
                        },1000);
                    }
                });
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_booklist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UserSellStallItemAdapter(new UserSellStallItemAdapter.OrderAction() {
            @Override
            public void order(UserSellStallItemForOthers item) {
                if (item.user_Id != mUser.getId())
                    orderSellBook(item);//wait to do
                else
                    Toast.makeText(UserSellStallListActivity.activity,"无法预订自己的书籍",Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(mAdapter);
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

    private void setupToolBar(){
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setNavigationIcon(R.drawable.arrow_back_userinfo);
    }

    private void loadBooksData(int userID){
        RetrofitSingleton.getInstance().loadOneUserAllSellBook(userID)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if (!isManuRresh){
                            DialogUtil.showTriangleDialogForLoading(UserSellStallListActivity.this,"加载中...");
                        }else{
                            mSwipeRefreshLayout.setRefreshing(true);
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(UserSellStallListActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<OneUserSellStallResponse>() {
                    @Override
                    public void accept(@NonNull OneUserSellStallResponse oneUserSellStallResponse) throws Exception {
                        mAdapter.setList(oneUserSellStallResponse.lists);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (!isManuRresh){
                            DialogUtil.hideDialogForLoading(UserSellStallListActivity.this);
                        }else {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void orderSellBook(UserSellStallItemForOthers item){
        int requestWay = 4;
        Map<String,String> infs = new HashMap<>();
        infs.put("UserId",mUser.getId()+"");
        infs.put("OrderedUserId",item.user_Id+"");//
        infs.put("BookName",item.bookName);
        infs.put("Author",item.author);
        infs.put("Press",item.press);
        infs.put("Version",item.version);
        infs.put("BookIntro",item.bookIntro);
        infs.put("ImageUrl",item.imageUrl);
        infs.put("IsJiaoCai",item.isJiaoCai+"");
        RetrofitSingleton.getInstance().orderBookActionResponseObservable(requestWay,infs)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showTriangleDialogForLoading(UserSellStallListActivity.this,"正在预定...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(UserSellStallListActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<OrderBookActionResponse>() {
                    @Override
                    public void accept(@NonNull OrderBookActionResponse orderBookActionResponse) throws Exception {
                        Toast.makeText(UserSellStallListActivity.this,orderBookActionResponse.status,Toast.LENGTH_SHORT).show();
                        if (orderBookActionResponse.status.equals("已添加到我的预订,请到我的预订界面查看详情"))
                            autoRefresh = true;
                        else
                            autoRefresh = false;
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(UserSellStallListActivity.this);
                        if (autoRefresh){
                            loadBooksData(mUserId);
                        }
                    }
                })
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void openBookDetail(OpenNormalBookDetailEventForSell bookData){
        Intent intent = new Intent(this,NormalBookDetailActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("BookData",bookData.getNormalBookData());
        args.putParcelable("BookImage",bookData.getBitmap());
        intent.putExtras(args);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(this,bookData.getImageView(),"book_image");
            startActivity(intent,optionsCompat.toBundle());
        }else {
            startActivity(intent);
        }
    }
}
