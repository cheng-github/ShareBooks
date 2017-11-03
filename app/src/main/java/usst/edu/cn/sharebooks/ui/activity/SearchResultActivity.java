package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.douban.DouBanBook;
import usst.edu.cn.sharebooks.model.douban.DouBanResponse;
import usst.edu.cn.sharebooks.model.search.JiaoCai;
import usst.edu.cn.sharebooks.model.search.SearchJiaoCaiResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.SearchDouBanBookAdapter;
import usst.edu.cn.sharebooks.ui.adapter.SearchJiaoCaiBookAdapter;
import usst.edu.cn.sharebooks.ui.view.SpaceItemDecoration;
import usst.edu.cn.sharebooks.util.DialogUtil;


public class SearchResultActivity extends BaseActivity {
    private static final int SEARCH_JIACAO = 1;
    private static final int SEARCH_OTHERS = 2;
    private int SEARCH_CATEGORY = 0;
    private User mUser;
    private String searchKeyWords;
    private int ADD_CATEGORY = 0;//默认为0
    //...........
    //view
    private Toolbar mToolBar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mRecyclerView;
    private SearchJiaoCaiResponse mSearchJiaoCaiResponse;
    private SearchJiaoCaiBookAdapter mAdapter;
    private SearchDouBanBookAdapter mAdapter2;
    private DouBanResponse mDouBanResponse;
    private boolean isRefreshManu = false;
    //   private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result);
        initDataFromLastActivity();
        setupToolBar();
        initView();
        loadSearchBooksData();
    }

    private void initDataFromLastActivity(){
        mUser = (User)getIntent().getSerializableExtra("User");
        SEARCH_CATEGORY = getIntent().getIntExtra("Category",0);
        searchKeyWords = getIntent().getStringExtra("KeyWord");
        ADD_CATEGORY = getIntent().getIntExtra("AddCategory",0);
        Log.i("TestData",mUser.getNickName()+"\n"+SEARCH_CATEGORY+"\n"+searchKeyWords);
    }

    private void setupToolBar(){
        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.sr_refresh);
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
                        isRefreshManu = true;
                        loadSearchBooksData();
                    }
                },1000);
            }
        });
        //recycler view set
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_booklist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(30));
        if (SEARCH_CATEGORY == SEARCH_JIACAO ){
            mAdapter = new SearchJiaoCaiBookAdapter(mUser,this);//这个adapter应该是与这个activity的生命周期绑定在一起的
            //所以这么使用应该是没毛病的
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setAddType(ADD_CATEGORY);
            mAdapter.notifyDataSetChanged();
        }else if (SEARCH_CATEGORY == SEARCH_OTHERS){
            mAdapter2 = new SearchDouBanBookAdapter(this,mUser);
            mRecyclerView.setAdapter(mAdapter2);
            mAdapter2.setAddType(ADD_CATEGORY);
            mAdapter2.notifyDataSetChanged();
        }
    }

    private void loadSearchBooksData(){
        switch (SEARCH_CATEGORY){
            case SEARCH_JIACAO:
                Map<String,String> infs = new HashMap<>();
                infs.put("RequestWay","2");
                infs.put("KeyWord",searchKeyWords);
                RetrofitSingleton.getInstance().searchJiaoCaiResponseAction(infs)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                if (isRefreshManu){
                                    mSwipeRefresh.setRefreshing(true);
                                }else {
                                    DialogUtil.showTriangleDialogForLoading(SearchResultActivity.this,"搜索中...");
                                }
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                mSwipeRefresh.setRefreshing(false);
                                //wait to show something intersting
                                Toast.makeText(SearchResultActivity.this,"加载数据出错",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .doOnNext(new Consumer<SearchJiaoCaiResponse>() {
                            @Override
                            public void accept(@NonNull SearchJiaoCaiResponse searchJiaoCaiResponse) throws Exception {
                                mSearchJiaoCaiResponse = searchJiaoCaiResponse;
                                for (JiaoCai item:mSearchJiaoCaiResponse.books){
                                    Log.i("TestData",item.bookName+"\n");
                                }
                                mAdapter.setList(mSearchJiaoCaiResponse.books);
                                mAdapter.notifyDataSetChanged();
                                //to do 设置一个判断  然后如果没有搜索结果  就换一个布局
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (isRefreshManu){
                                    mSwipeRefresh.setRefreshing(false);
                                }else {
                                    DialogUtil.hideDialogForLoading(SearchResultActivity.this);
                                }
                            }
                        })
                        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe();
                break;
            case SEARCH_OTHERS:
                //wait to do
                RetrofitSingleton.getInstance().searchBookFromDouban(searchKeyWords)
                        .doOnSubscribe(new Consumer<Disposable>() {
                            @Override
                            public void accept(@NonNull Disposable disposable) throws Exception {
                                if (isRefreshManu){
                                    mSwipeRefresh.setRefreshing(true);
                                }else {
                                    DialogUtil.showTriangleDialogForLoading(SearchResultActivity.this,"搜索中...");
                                }
                            }
                        })
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                if (isRefreshManu){
                                    mSwipeRefresh.setRefreshing(false);
                                }else {
                                    DialogUtil.hideDialogForLoading(SearchResultActivity.this);
                                }
                                Toast.makeText(SearchResultActivity.this,"加载数据出错",Toast.LENGTH_SHORT).show();
                            }
                        })
                        .doOnNext(new Consumer<DouBanResponse>() {
                            @Override
                            public void accept(@NonNull DouBanResponse douBanResponse) throws Exception {
                                mDouBanResponse = douBanResponse;
                                for (DouBanBook item:mDouBanResponse.books){
                                    for (String author:item.author)
                                    Log.i("TestDou",author+"\n");
                                }
                                mAdapter2.setList(mDouBanResponse.books);
                                mAdapter2.notifyDataSetChanged();
                                //to do 设置一个判断  然后如果没有搜索结果  就换一个布局
                            }
                        })
                        .doOnComplete(new Action() {
                            @Override
                            public void run() throws Exception {
                                if (isRefreshManu){
                                    mSwipeRefresh.setRefreshing(false);
                                }else {
                                    DialogUtil.hideDialogForLoading(SearchResultActivity.this);
                                }
                            }
                        })
                        .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                        .subscribe();
                break;
        }
    }
}
