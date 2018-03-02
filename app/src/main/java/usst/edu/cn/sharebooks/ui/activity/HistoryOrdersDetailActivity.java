package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.model.historyorders.HtyUserInfo;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class HistoryOrdersDetailActivity extends BaseActivity {
    private Toolbar mToolbar;
    private CircleImageView mCircleImage;
    private TextView mName;
    private ImageView mSex;
    private TextView mContri;
    private TextView mGetBook;
    private TextView mStallDes;
    private TextView mXueYuan;
    private TextView mContactWay;
    private HtyUserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historyordersdetail);
        userInfo = (HtyUserInfo) getIntent().getExtras().getSerializable("UserInfo");
        initToolBar();
        initMainViews();
    }

    private void initToolBar(){
        mToolbar = (Toolbar)findViewById(R.id.tl_toolbar);
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
        mName = (TextView) findViewById(R.id.tv_nickName);
        mName.setText(userInfo.nickName);
        mCircleImage = (CircleImageView)findViewById(R.id.civ_touxiang);
        GlideApp.with(this).load(ApiInterface.UserImageUrl+userInfo.imageUrl).dontAnimate().into(mCircleImage);
        mSex = (ImageView)findViewById(R.id.iv_sex);
        if (userInfo.sex.equals("?"))
            mSex.setVisibility(View.INVISIBLE);
        else if (userInfo.sex.equals("男"))
            mSex.setImageResource(R.drawable.male);
        else
            mSex.setImageResource(R.drawable.female);
        mContri = (TextView)findViewById(R.id.tv_contri);
        mContri.setText("贡献书籍:"+userInfo.contri_count);
        mGetBook = (TextView)findViewById(R.id.tv_get);
        mGetBook.setText("交易书籍:"+userInfo.get_count);
        mStallDes = (TextView)findViewById(R.id.tv_sellstalldescri);
        mStallDes.setText(userInfo.sellStallDescri);
        mXueYuan = (TextView)findViewById(R.id.tv_xueyuan);
        mXueYuan.setText(userInfo.xueyuan);
        mContactWay = (TextView)findViewById(R.id.tv_contactway);
        mContactWay.setText(userInfo.contactWay);
    }
}
