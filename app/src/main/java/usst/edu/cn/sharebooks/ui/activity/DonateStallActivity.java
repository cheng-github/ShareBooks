package usst.edu.cn.sharebooks.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.donate.DeleteDonateBookResponse;
import usst.edu.cn.sharebooks.model.donate.UserPersonalDonateStallItem;
import usst.edu.cn.sharebooks.model.donate.UserPersonalDonateStallResponse;
import usst.edu.cn.sharebooks.model.event.DeleteDonateBookEvent;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.PersonalDonateBookDetailAdapter;
import usst.edu.cn.sharebooks.ui.holder.SearchViewHolder;
import usst.edu.cn.sharebooks.ui.view.SpaceItemDecoration;
import usst.edu.cn.sharebooks.util.DialogUtil;
import usst.edu.cn.sharebooks.util.KeyBoardUtils;
import usst.edu.cn.sharebooks.util.ScreenUtils;


public class DonateStallActivity extends BaseActivity {
    private static final int SEARCH_JIACAO = 1;
    private static final int SEARCH_OTHERS = 2;
    private int SEARCH_CATEGORY = 0;//1表示搜索教材  2表示搜索其他书籍.

    private User mUser;
    private boolean isManuRefresh = false;
    private List<UserPersonalDonateStallItem> list = new ArrayList<>();
    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    private FloatingActionMenu mFab;
    private RelativeLayout mBookLayout;
    private TextView mNotice;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mDonateBookListDetail;
    private PopupWindow popupWindow;
    private SearchViewHolder holder;
    private PersonalDonateBookDetailAdapter mAdapter;

    @Override
    protected void onStart() {
        super.onStart();
//        RxBus.getInstance().tObservable(DeleteDonateBookEvent.class)
//                .doOnNext(new Consumer<DeleteDonateBookEvent>() {
//                    @Override
//                    public void accept(@NonNull DeleteDonateBookEvent deleteDonateBookEvent) throws Exception {
//                        deleteDonateBook(deleteDonateBookEvent.getBookName(),deleteDonateBookEvent.getUserId());
//                    }
//                })
//                .subscribe();
        //暂时还不清楚这里不能使用rxbus的原因
        //因为  需要放在onCreate()里面
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donatestall_layout);
        initDataFromLastActivity();
        setupToolBar();
        initView();
        loadUserDonatingBook(mUser.getId());
    }

    private void initDataFromLastActivity(){
        mUser = (User) getIntent().getSerializableExtra("User");
        //获取从MainActivity传过来来的User信息
    }

    private void setupToolBar(){
        mToolbar = (Toolbar)findViewById(R.id.donatestall);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView(){
        //如果没有任何数据就设置为不可见
        mBookLayout = (RelativeLayout) findViewById(R.id.all_mybook);
        mNotice = (TextView)findViewById(R.id.tv_notice);
        //设置fab
        mFab = (FloatingActionMenu)findViewById(R.id.add_book);
        mFab1 = (FloatingActionButton)findViewById(R.id.fab1);
        mFab2 = (FloatingActionButton)findViewById(R.id.fab2);
        mFab.setClosedOnTouchOutside(true);
        mFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SEARCH_CATEGORY = SEARCH_JIACAO;//1 表示搜索教材
                showPopupWindow(SEARCH_CATEGORY);
            }
        });
        mFab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SEARCH_CATEGORY = SEARCH_OTHERS;
                showPopupWindow(SEARCH_OTHERS);
            }
        });
        mSwipeRefresh = (SwipeRefreshLayout)findViewById(R.id.swiprefresh);
        if (mSwipeRefresh != null){
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
                            isManuRefresh = true;
                            loadUserDonatingBook(mUser.getId());
                        }
                    },1000);
                }
            });
        }
        mDonateBookListDetail = (RecyclerView)findViewById(R.id.donating_book);
        mDonateBookListDetail.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PersonalDonateBookDetailAdapter(new PersonalDonateBookDetailAdapter.DeleteBookAction() {
            @Override
            public void deleteBook(String bookName, int userId) {
                deleteDonateBook(bookName,userId);
            }
        });
        mDonateBookListDetail.setAdapter(mAdapter);
        mDonateBookListDetail.addItemDecoration(new SpaceItemDecoration(30));//设置item间隔
        //设置点击外部关闭 删除
        mDonateBookListDetail.setOnTouchListener(new View.OnTouchListener() {
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

    /**
     * 显示搜索框
     */
    private void showPopupWindow(int category){
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (popupWindow == null){
            holder = new SearchViewHolder(this, new SearchViewHolder.OnSearchHandlerListener() {
                @Override
                public void onSearch(int code) {
                    switch (code){
                        case SearchViewHolder.RESULT_SEARCH_EMPTY_KEYWORD:
                            Toast.makeText(DonateStallActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                            break;
                        case SearchViewHolder.RESULT_SEARCH_SEARCH_CANCEL:
                            popupWindow.dismiss();
                            break;
                        case SearchViewHolder.RESULT_SEARCH_SEARCH:
                            String key = holder.mSearchContent.getText().toString();
                            //打开搜索结果页面  这个应该是与之前的一样的.....不过怎么区分不同的打开开始呢...?!
                            Intent intent = new Intent(DonateStallActivity.this,SearchResultActivity.class);
                            Bundle args = new Bundle();
                            args.putString("KeyWord",key);
                            args.putInt("Category",SEARCH_CATEGORY);
                            args.putSerializable("User",mUser);
                            args.putInt("AddCategory",2);
                            intent.putExtras(args);
                            startActivity(intent);
                            break;
                }
            }
        });
            popupWindow = new PopupWindow
                    (holder.getContentView(),WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//不设置这一句外部点击没有反应
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    holder.mSearchContent.setText("");
                    KeyBoardUtils.closeKeyBord(holder.mSearchContent,DonateStallActivity.this);
                    ValueAnimator animator = ValueAnimator.ofFloat(0.7f, 1f);
                    animator.setDuration(500);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            lp.alpha = (float) animation.getAnimatedValue();
                            lp.dimAmount = (float) animation.getAnimatedValue();
                            getWindow().setAttributes(lp);
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                        }
                    });
                    animator.start();
                }
            });

        }
        if (holder != null){
            if (SEARCH_CATEGORY == SEARCH_JIACAO){
                holder.mSearchContent.setHint("搜索教材书名丶作者");
            }else {
                holder.mSearchContent.setHint("搜索书籍书名丶作者丶ISBN");
            }
        }
        KeyBoardUtils.openKeyBord(holder.mSearchContent,DonateStallActivity.this);
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0.7f);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                       @Override
                                       public void onAnimationUpdate(ValueAnimator animation) {
                                           lp.alpha = (float) animation.getAnimatedValue();
                                           lp.dimAmount = (float) animation.getAnimatedValue();
                                           getWindow().setAttributes(lp);
                                           getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                       }
                                   }
        );
        popupWindow.showAtLocation(mToolbar, Gravity.NO_GRAVITY,0, ScreenUtils.getStatusHeight(DonateStallActivity.this));
        animator.start();
}

    // to load the user book
    private void loadUserDonatingBook(int userId){
        RetrofitSingleton.getInstance().getPersonalBookData(userId)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        if (!isManuRefresh){
                            DialogUtil.showDotsDialogForLoading(DonateStallActivity.this,"加载中...");
                        }else{
                            mSwipeRefresh.setRefreshing(true);
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        if (!isManuRefresh){
                            DialogUtil.hideDialogForLoading(DonateStallActivity.this);
                        }else {
                            mSwipeRefresh.setRefreshing(false);
                        }
                        Toast.makeText(DonateStallActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<UserPersonalDonateStallResponse>() {
                    @Override
                    public void accept(@NonNull UserPersonalDonateStallResponse userPersonalDonateStallResponse) throws Exception {
                        list = userPersonalDonateStallResponse.list;
                        mAdapter.setList(list);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        if (!isManuRefresh){
                            DialogUtil.hideDialogForLoading(DonateStallActivity.this);
                        }else {
                            mSwipeRefresh.setRefreshing(false);
                        }
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    //to  delete the book
    private void deleteDonateBook(String bookName,int userId){
        RetrofitSingleton.getInstance().deleteDonateBookResponseObservable(bookName,userId)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showDotsDialogForLoading(DonateStallActivity.this,"删除中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DialogUtil.hideDialogForLoading(DonateStallActivity.this);
                        Toast.makeText(DonateStallActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<DeleteDonateBookResponse>() {
                    @Override
                    public void accept(@NonNull DeleteDonateBookResponse deleteDonateBookResponse) throws Exception {
                        Toast.makeText(DonateStallActivity.this,deleteDonateBookResponse.status,Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(DonateStallActivity.this);
                    //    loadUserDonatingBook(mUser.getId());//应该是这一句的问题....
                        //应该修改一下这里....
                        loadUserDonatingBook(mUser.getId());
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

//    private void refreshData(){
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSwipeRefresh.setRefreshing(true);
//                isManuRefresh = true;
//                loadUserDonatingBook(mUser.getId());
//            }
//        },2000);
//    }
}

