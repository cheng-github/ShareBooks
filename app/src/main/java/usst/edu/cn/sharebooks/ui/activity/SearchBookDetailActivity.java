package usst.edu.cn.sharebooks.ui.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import me.drakeet.materialdialog.MaterialDialog;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.model.donate.UserAddDonateBookResponse;
import usst.edu.cn.sharebooks.model.douban.DouBanBook;
import usst.edu.cn.sharebooks.model.search.JiaoCai;
import usst.edu.cn.sharebooks.model.sellstall.AddSellBookResponse;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.network.RetrofitSingleton;
import usst.edu.cn.sharebooks.util.Blur;
import usst.edu.cn.sharebooks.util.DialogUtil;


public class SearchBookDetailActivity extends BaseActivity {
    private User mUser;
    private JiaoCai mJiaoCai;
    private Bitmap mBitmap;
    private Toolbar mToolBar;
    private ImageView mLargeImage;
    private ImageView mSmallImage;
    private TextView mBookName;
    private TextView mAuthorPressVersion;
    private TextView mBookIntro;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private MaterialDialog mMaterialDialog1;
    private MaterialDialog mMaterialDialog2;
    private AddSellBookResponse mAddSellBookResponse;
    private DouBanBook mDouBanBook;
    private int BookType= 0;
    private String mDouBanAuthor;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout mRootView;
    //...........
    private int ADD_CATEGORY = 0;
    private int ADD_TOSELLSTALL = 1;
    private int ADD_TODONATESTALL = 2;
 //   private String imageUrl = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail_layout);
        initDataFromLastActivity();
        initView();
        setupToolBar();
    }

    private void initDataFromLastActivity(){
        mUser = (User)getIntent().getSerializableExtra("User");
        BookType = getIntent().getIntExtra("BookType",0); //确认是否添加的是教材
        ADD_CATEGORY = getIntent().getIntExtra("AddType",0);//确认添加到哪一个书架上
        Log.i("TestDou",BookType+"");
        if (BookType ==1){
            mJiaoCai = (JiaoCai)getIntent().getSerializableExtra("BookInfo");//这里我好想还没有将两种书分开
        } else{
            mDouBanBook = (DouBanBook)getIntent().getSerializableExtra("DouBanInfo");
        }
      //那就先暂且实现一个 然后再去实现另外一个吧
        mBitmap = (Bitmap)getIntent().getParcelableExtra("book_image");
        //  imageUrl = getIntent().getStringExtra("ImageUrl");
    }

    private void setupToolBar(){
        mToolBar = (Toolbar)findViewById(R.id.toolbar);
        appBarLayout = (AppBarLayout)findViewById(R.id.abl_appbarlayout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCollapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.ctl_collasping);
        mCollapsingToolbarLayout.setTitleEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //这里的vertivalOffset就是一个负值  这个负的值的绝对值表示往下拉的距离
                if (verticalOffset <= -mLargeImage.getHeight()/2){
                    mCollapsingToolbarLayout.setTitle(getString(R.string.s_bookdetail));
                }else {
                    mCollapsingToolbarLayout.setTitle("");
                }
            }
        });
        mToolBar.setNavigationIcon(R.drawable.back_searchdetail);
     //   mToolBar.setTitle(R.string.s_bookdetail);
        //设置状态栏透明  好像不起作用
//        mRootView = (CoordinatorLayout)findViewById(R.id.rootview);
//        mRootView.setStatusBarBackgroundResource(R.color.statusTrans);
    }

    private void initView(){
        mLargeImage = (ImageView)findViewById(R.id.iv_bigimage);
        mSmallImage = (ImageView)findViewById(R.id .iv_smallimage);
        if (mBitmap != null){
            mSmallImage.setImageBitmap(mBitmap);
            mLargeImage.setImageBitmap(Blur.apply(mBitmap));
            mLargeImage.setAlpha(0.8f);
        }
        mBookName = (TextView)findViewById(R.id.tv_bookName);
        mAuthorPressVersion = (TextView)findViewById(R.id.tv_authorpressversion);
        mBookIntro = (TextView)findViewById(R.id.tv_bookIntro);
        //分为两个设置数据
        if (BookType == 1){
            mBookName.setText(mJiaoCai.bookName);
            String authorPressVersion = mJiaoCai.author+"/"+mJiaoCai.press+"/"+mJiaoCai.version;
            mAuthorPressVersion.setText(authorPressVersion);
            mBookIntro.setText(mJiaoCai.bookIntro);
        }else if (BookType == 2){
            StringBuilder allAuthorName = new StringBuilder();
            for (String author:mDouBanBook.author){
                allAuthorName.append(author+"/");
            }
            allAuthorName.deleteCharAt(allAuthorName.length()-1);
            mDouBanAuthor = allAuthorName.toString();
            if (mDouBanBook.translator.length == 1){
                allAuthorName.append("   "+mDouBanBook.translator[0]+" 译");
            }else if (mDouBanBook.translator.length >1){
                allAuthorName.append("   ");
                for (String trans:mDouBanBook.translator){
                    allAuthorName.append(trans+"/");
                }
                allAuthorName.deleteCharAt(allAuthorName.length()-1);
                allAuthorName.append(" 译");
            }
            allAuthorName.append("/"+mDouBanBook.press+"/"+mDouBanBook.version);
            mBookName.setText(mDouBanBook.bookName);
            mAuthorPressVersion.setText(allAuthorName.toString());
            mBookIntro.setText(mDouBanBook.bookIntro);
        }
        mMaterialDialog1 = new MaterialDialog(this);
        mMaterialDialog2 = new MaterialDialog(this);
    }

    private void showSellAlertDialog(){
        if (mMaterialDialog1 != null){
            mMaterialDialog1.setMessage("确认要添加这本书到书摊吗？")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog1.dismiss();
                            addSellBook();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog1.dismiss();
                        }
                    })
                    .setCanceledOnTouchOutside(true)
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            //这里可以加一个进度条什么的
                            //不应该在这里加
                        }
                    })
                    .show();
        }
    }

    private void showDonateAlertDialog(){
        if (mMaterialDialog2 != null){
            mMaterialDialog2.setMessage("确认要贡献这本书吗?")
                    .setPositiveButton("确认", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog2.dismiss();
                            //上传数据到服务器
                            addDonateBook();
                        }
                    })
                    .setNegativeButton("取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog2.dismiss();
                        }
                    })
                    .setCanceledOnTouchOutside(true)
                    .show();
        }
    }

    //to add donate book
    private void addDonateBook(){
        Map<String,String> infs = new HashMap<>();
        if (BookType == 1){
            infs.put("bookName",mJiaoCai.bookName);
            infs.put("author",mJiaoCai.author);
            infs.put("press",mJiaoCai.press);
            infs.put("version",mJiaoCai.version);
            infs.put("bookIntro",mJiaoCai.bookIntro);
            infs.put("wishes","blablablallllalala");
            infs.put("imageUrl",mJiaoCai.imageUrl);
            infs.put("isJiaoCai","1");
            infs.put("donateUser_id",mUser.getId()+"");
        }else {
            infs.put("bookName",mDouBanBook.bookName);
            infs.put("author",mDouBanAuthor);
            infs.put("press",mDouBanBook.press);
            infs.put("version",mDouBanBook.version);
            infs.put("bookIntro",mDouBanBook.bookIntro);
            infs.put("wishes","blablablallllalala");
            infs.put("imageUrl",mDouBanBook.imageUrl);
            infs.put("isJiaoCai","0");//这里怎么写成了2...应该写0...
            infs.put("donateUser_id",mUser.getId()+"");
        }
        RetrofitSingleton.getInstance().addDonateBookResponseObservable(infs)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showDotsDialogForLoading(SearchBookDetailActivity.this,"加载中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        DialogUtil.hideDialogForLoading(SearchBookDetailActivity.this);
                    }
                })
                .doOnNext(new Consumer<UserAddDonateBookResponse>() {
                    @Override
                    public void accept(@NonNull UserAddDonateBookResponse userAddDonateBookResponse) throws Exception {
                        Toast.makeText(SearchBookDetailActivity.this,userAddDonateBookResponse.status,Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(SearchBookDetailActivity.this);
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    private void addSellBook(){
        Map<String,String>  infs;
        if (BookType == 1){
            infs = new HashMap<>();
            infs.put("RequestWay","3");
            infs.put("UserId",String.valueOf(mUser.getId()));
            infs.put("BookName",mJiaoCai.bookName);
            infs.put("Author",mJiaoCai.author);
            infs.put("Press",mJiaoCai.press);
            infs.put("Version",mJiaoCai.version);
            infs.put("BookIntro",mJiaoCai.bookIntro);
            infs.put("ImageUrl",mJiaoCai.imageUrl);
            infs.put("XueyuanCategory",String.valueOf(mJiaoCai.xueyuanCategory));
            infs.put("BookState","1");
            infs.put("IsJiaoCai","1");//1表示教材
        }else{
            infs = new HashMap<>();
            infs.put("RequestWay","3");
            infs.put("UserId",String.valueOf(mUser.getId()));
            infs.put("BookName",mDouBanBook.bookName);
            infs.put("Author",mDouBanAuthor);
            infs.put("Press",mDouBanBook.press);
            infs.put("Version",mDouBanBook.version);
            infs.put("BookIntro",mDouBanBook.bookIntro);
            infs.put("ImageUrl",mDouBanBook.imageUrl);
            infs.put("XueyuanCategory","0");//0表示不属于任何学院
            infs.put("BookState","1");
            infs.put("IsJiaoCai","0");//0表示其它书籍
        }
        RetrofitSingleton.getInstance().addSellBookResponseObservable(infs)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        DialogUtil.showTriangleDialogForLoading(SearchBookDetailActivity.this,"加载中...");
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                })
                .doOnNext(new Consumer<AddSellBookResponse>() {
                    @Override
                    public void accept(@NonNull AddSellBookResponse addSellBookResponse) throws Exception {
                        mAddSellBookResponse = addSellBookResponse;
                    }
                })
                .doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        DialogUtil.hideDialogForLoading(SearchBookDetailActivity.this);
                        Toast.makeText(SearchBookDetailActivity.this,mAddSellBookResponse.statue,Toast.LENGTH_SHORT).show();
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_jiaocaimenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_book:
                //写...一个弹窗吧....
                //加一个判断进行不同种类的添加....不就搞定了
                if (ADD_CATEGORY == ADD_TOSELLSTALL){
                    showSellAlertDialog();
                }else if (ADD_CATEGORY == ADD_TODONATESTALL){
                    showDonateAlertDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
     //   Toast.makeText(SearchBookDetailActivity.this,"按下了back键",Toast.LENGTH_SHORT).show();
        super.onBackPressed();
    }

    //经过测试这个在dialog显示的时候会失效
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            System.out.println("按下了back键   onKeyDown()");
//            DialogUtil.hideDialogForLoading(SearchBookDetailActivity.this);
//            return false;
//        }else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }
}
