package usst.edu.cn.sharebooks.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

//第一个页面的viewPager的adapter
public class FirstPagerAdapter extends FragmentPagerAdapter{
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    public FirstPagerAdapter(FragmentManager fm){
        super(fm);
    }

    //如果使用这种方式加载布局 那么必须要调用notifyDataChanged()方法
    public void addTab(Fragment fragment,String title){
        fragments.add(fragment);
        titles.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }
}
