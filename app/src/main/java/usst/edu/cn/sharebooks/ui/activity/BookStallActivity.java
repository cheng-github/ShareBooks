package usst.edu.cn.sharebooks.ui.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import usst.edu.cn.sharebooks.model.event.DeleteSellBookEvent;
import usst.edu.cn.sharebooks.model.sellstall.DeleteSellResponse;
import usst.edu.cn.sharebooks.model.sellstall.SellBook;
import usst.edu.cn.sharebooks.model.sellstall.SellBookStallList;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.ui.adapter.PersonalSellBookDetailAdapter;
import usst.edu.cn.sharebooks.ui.holder.SearchViewHolder;
import usst.edu.cn.sharebooks.ui.view.SpaceItemDecoration;
import usst.edu.cn.sharebooks.util.DialogUtil;
import usst.edu.cn.sharebooks.util.KeyBoardUtils;
import usst.edu.cn.sharebooks.util.ScreenUtils;


public class BookStallActivity extends BaseActivity {
    private static final int SEARCH_JIACAO = 1;
    private static final int SEARCH_OTHERS = 2;
    //view
    private FloatingActionButton mFab1;
    private FloatingActionButton mFab2;
    private FloatingActionMenu mFab;
    private RelativeLayout mBookLayout;
    private TextView mNotice;
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mSellBookListDetail;
    private PersonalSellBookDetailAdapter mAdapter;
    private PopupWindow popupWindow;
    private SearchViewHolder holder;
    private int SEARCH_CATEGORY = 0;//1表示搜索教材  2表示搜索其他书籍.
    private User mUser;
    private SellBookStallList mSellBookStallListData = new SellBookStallList();//用户书摊的书籍数据
    private boolean autoRefresh = false;

    @Override
    protected void onStart() {
        super.onStart();
//        RxBus.getInstance().tObservable(DeleteSellBookEvent.class)
//                .doOnNext(new Consumer<DeleteSellBookEvent>() {
//                    @Override
//                    public void accept(@NonNull DeleteSellBookEvent deleteSellBookEvent) throws Exception {
//                        deleteSellBook(deleteSellBookEvent.getBookName());
//                    }
//                }).subscribe();
//暂时还不清楚这里不能使用rxbus的原因
//原因清楚了  不能放在这里  要放在onCreat()这个声明周期里
    }

    @Override
    protected void onCreate(Bundle savedInstanced){
        super.onCreate(savedInstanced);
        setContentView(R.layout.bookstall_activity);
        initView();//应该先调用这个  不然可能会产生空指针异常
        setupToolBar();
        initDatas();
    }

    private void initDatas(){
        mUser = (User) getIntent().getSerializableExtra("User");
        Log.i("TestData1",mUser.getId()+""+"\nUserName"+mUser.getNickName());
        loadUserSellBook();
    }

    private void setupToolBar(){
        mToolbar = (Toolbar)findViewById(R.id.bookstallToolbar);
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
                        loadUserSellBook();
                    }
                },1000);
            }
        });
        }
        //设置书架上数据的详细信息的adapter
        mSellBookListDetail = (RecyclerView)findViewById(R.id.yun_book);
        mSellBookListDetail.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new PersonalSellBookDetailAdapter(new PersonalSellBookDetailAdapter.DeleteAction() {
            @Override
            public void deleteBook(String bookName) {
                deleteSellBook(bookName);
            }
        });
        mSellBookListDetail.setAdapter(mAdapter);//居然忘记写这一句...
        mSellBookListDetail.addItemDecoration(new SpaceItemDecoration(30));//设置item间隔
        //设置点击外部关闭 删除
        mSellBookListDetail.setOnTouchListener(new View.OnTouchListener() {
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
                            Toast.makeText(BookStallActivity.this,"输入不能为空",Toast.LENGTH_SHORT).show();
                            break;
                        case SearchViewHolder.RESULT_SEARCH_SEARCH_CANCEL:
                            popupWindow.dismiss();
                            break;
                        case SearchViewHolder.RESULT_SEARCH_SEARCH:
                            String key = holder.mSearchContent.getText().toString();
                            Intent intent = new Intent(BookStallActivity.this,SearchResultActivity.class);
                            Bundle args = new Bundle();
                            args.putString("KeyWord",key);
                            args.putInt("Category",SEARCH_CATEGORY);
                            args.putSerializable("User",mUser);
                            args.putInt("AddCategory",1);
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
                KeyBoardUtils.closeKeyBord(holder.mSearchContent,BookStallActivity.this);
                Log.i("Test","KeyBoardUtils.closeKeyBord(holder.mSearchContent,BookStallActivity.this);");
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
                holder.mSearchContent.setHint("搜索教材书名、作者");
            }else {
                holder.mSearchContent.setHint("搜索书籍书名、作者、ISBN");
            }
       }
      KeyBoardUtils.openKeyBord(holder.mSearchContent,BookStallActivity.this);
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
        popupWindow.showAtLocation(mToolbar, Gravity.NO_GRAVITY,0, ScreenUtils.getStatusHeight(BookStallActivity.this));
        animator.start();
    }

    private void loadUserSellBook(){
        RetrofitSingleton.getInstance().getUserSellBookDatas(mUser.getId())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        mSwipeRefresh.setRefreshing(true);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.i("TestData","...........加载个人书籍出错..............");
                        Toast.makeText(BookStallActivity.this,"网络连接超时",Toast.LENGTH_SHORT).show();
                        mSwipeRefresh.setRefreshing(false);
                    }
                })
                .doOnNext(new Consumer<SellBookStallList>() {
                    @Override
                    public void accept(@NonNull SellBookStallList sellBookStallList) throws Exception {
                        for (SellBook sellBook:sellBookStallList.sellBookList){
                            Log.i("TestData","BookName"+sellBook.bookName+"\n");
                        }
                        mSellBookStallListData = sellBookStallList;
                        for (SellBook sellBook:mSellBookStallListData.sellBookList){
                            Log.i("TestData","BookName mSellBookStallListData"+sellBook.bookName+"\n");
                        }
                        mAdapter.setList(mSellBookStallListData.sellBookList);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        mSwipeRefresh.setRefreshing(false);
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void deleteSellBook(String bookName){
        Map<String,String> datas = new HashMap<>();
        datas.put("RequestWay","1");
        datas.put("BookName",bookName);
        datas.put("userId",mUser.getId()+"");
        RetrofitSingleton.getInstance().deleteSellBookAction(datas)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showTriangleDialogForLoading(BookStallActivity.this,"删除中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Toast.makeText(BookStallActivity.this,"服务器异常",Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnNext(new Consumer<DeleteSellResponse>() {
                    @Override
                    public void accept(@NonNull DeleteSellResponse deleteSellResponse) throws Exception {
                        // Toast.makeText(BookStallActivity.this,deleteSellResponse.delMessage,Toast.LENGTH_SHORT).show();
                        //下来应该还应该有刷新数据的操作
                        if (deleteSellResponse.delMessage.equals("删除成功!")){
                            Toast.makeText(BookStallActivity.this,deleteSellResponse.delMessage,Toast.LENGTH_SHORT).show();
                           autoRefresh = true;
                        }else {
                            autoRefresh = false;
                        }
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(BookStallActivity.this);
                        if (autoRefresh){
                            loadUserSellBook(); //设置只有成功删除了书籍才会进行数据刷新的操作
                        }
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        holder.onDestroy();
    }
}
