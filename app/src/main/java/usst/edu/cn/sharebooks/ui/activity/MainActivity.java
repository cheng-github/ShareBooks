package usst.edu.cn.sharebooks.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.fragment.MyFragment;

/**
 *
 * Created by Cheng on 2017/5/22.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private ViewPager mViewPager;
    LayoutInflater mLayoutInflater;
    Context mContext;
    private int mPageIndex;

    @Override
    protected void onCreate(Bundle savedIntance){
        super.onCreate(savedIntance);
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
        mContext = MainActivity.this;
        mLayoutInflater = getLayoutInflater();
        this.findViewById(R.id.tab_bottom_first).setOnClickListener(this);
        this.findViewById(R.id.tab_bottom_second).setOnClickListener(this);
        this.findViewById(R.id.tab_bottom_third).setOnClickListener(this);
    }

    private void setupToolbar(){
        toolbar = (Toolbar)this.findViewById(R.id.toolbar);
        //toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void  setupViewPager(){
        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        FragmentStatePagerAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //    Log.i("position","的之为     "+position);
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
//                if (mPageIndex == 0) return;
//                else if (mPageIndex == 1){
//                    mViewPager.arrowScroll(1);
//                    mPageIndex = 0;
//                }
//                else {
//                    mViewPager.arrowScroll(1);
//                    mViewPager.arrowScroll(1);
//                    mPageIndex = 0;
//                }
                mViewPager.setCurrentItem(0);
                break;
           case R.id.tab_bottom_second:
//               if (mPageIndex == 0){
//                   mViewPager.arrowScroll(2);
//                   mPageIndex = 1;
//               }else if (mPageIndex == 1){
//                   return;
//               }else {
//                   mViewPager.arrowScroll(1);
//                   mPageIndex = 1;
//               }
               mViewPager.setCurrentItem(1);
               break;
           case R.id.tab_bottom_third:
//               if (mPageIndex == 0){
//                   mViewPager.arrowScroll(2);
//                   mViewPager.arrowScroll(2);
//                   mPageIndex = 2;
//               }else if (mPageIndex == 1){
//                   mViewPager.arrowScroll(2);
//                   mPageIndex = 2;
//               }else {
//                   return;
//               }
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

    private  void openNextActivity(Class Sample){
        Intent intent = new Intent(MainActivity.this,Sample);
        startActivity(intent);
    }

    private static class MyFragmentAdapter extends FragmentStatePagerAdapter {
        private MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.newInstance(position+1);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
