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
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import usst.edu.cn.sharebooks.R;

/**
 *
 * Created by Cheng on 2017/5/22.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private static int layoutId;
    private List<Fragment> content;
    LayoutInflater mLayoutInflater;
    Context mContext;
    private int mPageIndex;

    @Override
    protected void onCreate(Bundle savedIntance){
        super.onCreate(savedIntance);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initData();
        setupToolbar();
        setupViewPager();
        //如果是首次启动应用,不是旋转屏幕什么的
        if (savedIntance == null){
            mPageIndex = 0;
        }
    }

    private void initData(){
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

    void  setupViewPager(){
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
                Log.i("position","位置为     "+position);
                mPageIndex = position;
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
        FragmentManager fragmentManager = getSupportFragmentManager();
       switch (v.getId()){
            case R.id.tab_bottom_first:
                if (mPageIndex == 0) return;
                else if (mPageIndex == 1){
                    mViewPager.arrowScroll(1);
                    mPageIndex = 0;
                }
                else {
                    mViewPager.arrowScroll(1);
                    mViewPager.arrowScroll(1);
                    mPageIndex = 0;
                }
                break;
           case R.id.tab_bottom_second:
               if (mPageIndex == 0){
                   mViewPager.arrowScroll(2);
                   mPageIndex = 1;
               }else if (mPageIndex == 1){
                   return;
               }else {
                   mViewPager.arrowScroll(1);
                   mPageIndex = 1;
               }
               break;
           case R.id.tab_bottom_third:
               if (mPageIndex == 0){
                   mViewPager.arrowScroll(2);
                   mViewPager.arrowScroll(2);
                   mPageIndex = 2;
               }else if (mPageIndex == 1){
                   mViewPager.arrowScroll(2);
                   mPageIndex = 2;
               }else {
                   return;
               }
               break;
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

    public static class MyFragment extends Fragment {
        private static String Section_Number="section_number";

      //使用newInstance去有选择的加载我们需要的fragment布局文件
        public static MyFragment newInstance(int sectionNumber){
            MyFragment myFragment = new MyFragment();
            Bundle args = new Bundle();
            args.putInt(Section_Number,sectionNumber);
            myFragment.setArguments(args);
            return myFragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState){
            int section_number = getArguments().getInt(Section_Number);
            View rootView = inflater.inflate(
                    R.layout.main_acrivity_fragment, container, false);
            switch (section_number){
                case 1:
                    rootView = inflater.inflate(R.layout.first_fragment,container,false);
                    ((TextView)rootView.findViewById(R.id.search_begin)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(),SearchActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
                case 2:
                    rootView = inflater.inflate(R.layout.second_fragment,container,false);
                    break;
                case 3:
                    rootView = inflater.inflate(R.layout.third_fragment,container,false);
                    break;
            }
            return rootView;
        }

    }

}
