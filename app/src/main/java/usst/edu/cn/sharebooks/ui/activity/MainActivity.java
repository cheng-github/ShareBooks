package usst.edu.cn.sharebooks.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.trello.rxlifecycle2.android.ActivityEvent;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.base.BaseActivity;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenNormalBookDetailEventForDonate;
import usst.edu.cn.sharebooks.model.event.OpenSellStallDetailEvent;
import usst.edu.cn.sharebooks.model.event.UpdateUserInfoEvent;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.ui.adapter.MainFragmentAdapter;
import usst.edu.cn.sharebooks.util.RxUtil;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private Context mContext;
    private int mPageIndex;
    private FloatingActionButton floatingActionButton;
    public User mUser;
    private TextView mToolBarTitle;
    private MainFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedIntance){
        //注意rxbus一定放在这里声明
        RxBus.getInstance().tObservable(UpdateUserInfoEvent.class)
                .doOnNext(new Consumer<UpdateUserInfoEvent>() {
                    @Override
                    public void accept(@NonNull UpdateUserInfoEvent updateUserInfoEvent) throws Exception {
                        refreshUserInfo(updateUserInfoEvent.user);
                    }
                })
                .compose(RxUtil.<UpdateUserInfoEvent>io())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        RxBus.getInstance().tObservable(OpenSellStallDetailEvent.class)
                .doOnNext(new Consumer<OpenSellStallDetailEvent>() {
                    @Override
                    public void accept(@NonNull OpenSellStallDetailEvent openSellStallDetailEvent) throws Exception {
                        Log.i("TestNormal",".........rxbus调用了一次.........");
                        openSellStallDetailActivity(openSellStallDetailEvent.getUserId());
                    }
                })
                .compose(RxUtil.<OpenSellStallDetailEvent>io())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        RxBus.getInstance().tObservable(OpenNormalBookDetailEventForDonate.class)
                .doOnNext(new Consumer<OpenNormalBookDetailEventForDonate>() {
                    @Override
                    public void accept(@NonNull OpenNormalBookDetailEventForDonate openNormalBookDetailEventForDonate) throws Exception {
                        openBookDetail(openNormalBookDetailEventForDonate);
                    }
                })
                .compose(RxUtil.<OpenNormalBookDetailEventForDonate>io())
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe();
        super.onCreate(savedIntance);
        Log.i("TestLifeCycle","............onCreate()..............MainActivity");
        setContentView(R.layout.activity_main);
        initData();
        setupToolbar();
        setupViewPager();
        //如果是首次启动应用,不是旋转屏幕什么的
        if (savedIntance == null){
            mPageIndex = 0;
            updateBottomButtons(mPageIndex);
        }
    }

    private void initData(){
        mUser = (User)getIntent().getSerializableExtra("userInfo");
        Log.i("TestData","UserName-"+mUser.getUserName()+"\nPassWord-"+mUser.getPassWord()+"\nNickName-"+mUser.getNickName()
                +"\nContri_count-"+mUser.getContri_count()+"\nGet_count-"+mUser.getGet_count()+"----------from MainActivity");
        mContext = MainActivity.this;
        this.findViewById(R.id.tab_bottom_first).setOnClickListener(this);
        this.findViewById(R.id.tab_bottom_second).setOnClickListener(this);
        this.findViewById(R.id.tab_bottom_third).setOnClickListener(this);
    }

    private void setupToolbar(){
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mToolBarTitle = (TextView) this.findViewById(R.id.toolbar_title);
        //这样我的textview就可以显示标题了
        floatingActionButton = (FloatingActionButton)this.findViewById(R.id.float_button);
    }

    private void  setupViewPager(){
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        adapter  = new MainFragmentAdapter(getSupportFragmentManager(),mUser,MainActivity.this);
        mViewPager.setAdapter(adapter);
        //太坑爹了，只要在这个adapter里面设置了那个重写方法返回了fragment之后  你使用什么
        //adapter.getItem()无论怎么转型都是不起作用的
        //所以以后最好还是不要使用这种在adapter里面写死的方式
        //简直有毒

        adapter.notifyDataSetChanged();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageIndex = position;
                updateBottomButtons(mPageIndex);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
            case R.id.tab_bottom_first:
                mViewPager.setCurrentItem(0);
                break;
           case R.id.tab_bottom_second:
               mViewPager.setCurrentItem(1);
               break;
           case R.id.tab_bottom_third:
               mViewPager.setCurrentItem(2);
               break;
        }
    }
    //这个方法用于为底部的按钮设置颜色
    private void updateBottomButtons(int pos){
        if (pos == 0){
            ((ImageButton)this.findViewById(R.id.tab_bottom_first)).setImageResource(R.drawable.first_page_black);
            ((ImageButton)this.findViewById(R.id.tab_bottom_second)).setImageResource(R.drawable.second_page);
            ((ImageButton)this.findViewById(R.id.tab_bottom_third)).setImageResource(R.drawable.third_page);
        }else if(pos == 1){
            ((ImageButton)this.findViewById(R.id.tab_bottom_first)).setImageResource(R.drawable.first_page);
            ((ImageButton)this.findViewById(R.id.tab_bottom_second)).setImageResource(R.drawable.second_page_black);
            ((ImageButton)this.findViewById(R.id.tab_bottom_third)).setImageResource(R.drawable.third_page);
        }else if (pos == 2){
            ((ImageButton)this.findViewById(R.id.tab_bottom_first)).setImageResource(R.drawable.first_page);
            ((ImageButton)this.findViewById(R.id.tab_bottom_second)).setImageResource(R.drawable.second_page);
            ((ImageButton)this.findViewById(R.id.tab_bottom_third)).setImageResource(R.drawable.third_page_black);
        }
    }

    //设置toolbar文字
    public  void setToolbarTitle(String title){
       getSupportActionBar().setTitle(title);
        mToolBarTitle.setText(title);
    }

    private void refreshUserInfo(User mUser){
        Log.i("TestCamera","updateUserInfo(User user)...........from MainFragment.........");
        this.mUser = mUser;
        adapter.setUser(mUser);
        adapter.notifyDataSetChanged();
        //一定要调用adapter 刷新这个数据 否则  虽然在mainActivity里修改了  但是adapter里还是使用的旧的数据
        //而且要调用两句
        Log.i("TestCamera",this.mUser.getNickName());
    }

    private void openSellStallDetailActivity(int id){
        Intent intent = new Intent(this,UserSellStallListActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("User",mUser);
        intent.putExtra("UserId",id);
        intent.putExtras(args);
        startActivity(intent);
    }

    private void openBookDetail(OpenNormalBookDetailEventForDonate bookData){
        Intent intent = new Intent(MainActivity.this,NormalBookDetailActivity.class);
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

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("TestLifeCycle","...........onStart()...........MainActivity");
        //为了避免rxbus重复订阅  这个rxbus的使用不能丢在 onStart()里面  而要放在onCreate()里面  因为每次都会产生 onStart()与onResume()
        //所以 我一直都是在瓜皮的使用rxbus.....
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("TestLifeCycle","...........onResume()...........MainActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("TestLifeCycle","...........onPause()...........MainActivity");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("TestLifeCycle","...........onStop()...........MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TestLifeCycle","...........onDestroy()...........MainActivity");
    }
}
