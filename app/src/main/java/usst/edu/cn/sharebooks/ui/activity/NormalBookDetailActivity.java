package usst.edu.cn.sharebooks.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.model.event.normalbookmodel.NormalBookData;
import usst.edu.cn.sharebooks.util.Blur;


public class NormalBookDetailActivity extends BaseActivity {
    private NormalBookData normalBookData;
    private Bitmap bitmap;
    private Toolbar mToolBar;
    private ImageView mLargeImage;
    private ImageView mSmallImage;
    private TextView mBookName;
    private TextView mAuthorPressVersion;
    private TextView mBookIntro;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail_layout);
        initDataFromLastActivity();
        initView();
    }

    private void initDataFromLastActivity(){
        normalBookData = (NormalBookData)getIntent().getSerializableExtra("BookData");
        bitmap = (Bitmap)getIntent().getParcelableExtra("BookImage");
    }

    private void initView(){
        setupToolBar();
        mLargeImage = (ImageView)findViewById(R.id.iv_bigimage);
        mSmallImage = (ImageView)findViewById(R.id .iv_smallimage);
        if (bitmap != null){
            mSmallImage.setImageBitmap(bitmap);
            mLargeImage.setImageBitmap(Blur.apply(bitmap));
            mLargeImage.setAlpha(0.8f);
        }
        mBookName = (TextView)findViewById(R.id.tv_bookName);
        mBookName.setText(normalBookData.bookName);
        mAuthorPressVersion = (TextView)findViewById(R.id.tv_authorpressversion);
        mBookIntro = (TextView)findViewById(R.id.tv_bookIntro);
        mAuthorPressVersion.setText(normalBookData.author+"/"+normalBookData.press+"/"+normalBookData.version);
        mBookIntro.setText(normalBookData.bookIntro);
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
    }
}
