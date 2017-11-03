package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.model.order.PersonalOrderItem;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class OrderDetailActivity extends BaseActivity {
    private PersonalOrderItem orderItem;
    private Toolbar mToolBar;
    private TextView mBookName;
    private TextView mAuthor;
    private TextView mPressVersion;
    private TextView mOrderDate;
    private TextView mOrderInfo;
    private CircleImageView mCircleImageView;
    private TextView mNickName;
    private TextView mContri;
    private TextView mGetCount;
    private TextView mStallDes;
    private TextView mXeuyuan;
    private TextView mContact;
    private ImageView mImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderdetail_layout);
        initDataFromLastActivity();
        initView();
    }

    private void initDataFromLastActivity(){
        orderItem = (PersonalOrderItem)getIntent().getSerializableExtra("OrderItem");
    }

    private void initView(){
        mToolBar = (Toolbar)findViewById(R.id.tl_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    //    mToolBar.setTitle("订单详情");
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBookName = (TextView)findViewById(R.id.tv_bookName);
        mBookName.setText(orderItem.bookName);
        mAuthor = (TextView)findViewById(R.id.tv_author);
        mAuthor.setText(orderItem.author);
        mPressVersion = (TextView)findViewById(R.id.tv_pressversion);
        mPressVersion.setText(orderItem.press+"/"+orderItem.version);
        mOrderDate = (TextView)findViewById(R.id.tv_ordertime);
        mOrderDate.setText(orderItem.orderDate);
        mOrderInfo = (TextView)findViewById(R.id.tv_orderinfo);
        if (orderItem.isOreder.equals("已预订"))
            mOrderInfo.setText("联系我");
        else
            mOrderInfo.setText("预订者");
        mCircleImageView = (CircleImageView)findViewById(R.id.civ_touxiang);
        String touxiangUrl = ApiInterface.UserImageUrl+orderItem.touXiangImageUrl;
        GlideApp.with(this).load(touxiangUrl).dontAnimate().into(mCircleImageView);//不使用动画加载
        mNickName = (TextView)findViewById(R.id.tv_nickName);
        mNickName.setText(orderItem.nickName);
        mContri = (TextView)findViewById(R.id.tv_contri);
        mContri.setText("贡献书籍:"+orderItem.contri_count);
        mGetCount = (TextView)findViewById(R.id.tv_get);
        mGetCount.setText("交易书籍:"+orderItem.get_count);
        mStallDes = (TextView)findViewById(R.id.tv_sellstalldescri);
        mStallDes.setText(orderItem.sellStallDes);
        mXeuyuan = (TextView)findViewById(R.id.tv_xueyuan);
        mXeuyuan.setText(orderItem.xueYuan);
        mContact = (TextView)findViewById(R.id.tv_contactway);
        mContact.setText(orderItem.contactWay);
        mImageView = (ImageView)findViewById(R.id.iv_sex);
        if (orderItem.sex.equals("?")){
            mImageView.setVisibility(View.INVISIBLE);
        } else if (orderItem.sex.equals("男")) {
            mImageView.setImageResource(R.drawable.male);
        }else if (orderItem.sex.equals("女")){
            mImageView.setImageResource(R.drawable.female);
        }
    }
}
