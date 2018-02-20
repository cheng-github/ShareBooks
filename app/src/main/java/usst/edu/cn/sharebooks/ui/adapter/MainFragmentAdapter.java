package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import usst.edu.cn.sharebooks.model.articlelist.ArticleIDList;
import usst.edu.cn.sharebooks.model.user.User;
import usst.edu.cn.sharebooks.ui.activity.MainActivity;
import usst.edu.cn.sharebooks.ui.fragment.MainFirstFragment;
import usst.edu.cn.sharebooks.ui.fragment.MainSecondFragment;
import usst.edu.cn.sharebooks.ui.fragment.MainThirdFragment;


public class MainFragmentAdapter extends FragmentStatePagerAdapter {
    private User user;
    private Context mContext;
    private ArticleIDList articleIDLists;

    public MainFragmentAdapter(FragmentManager fragmentManager,Context context,User user,ArticleIDList articleIDLists){
        super(fragmentManager);
        this.user = user;
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                MainFirstFragment firstFragment = MainFirstFragment.newInstance(user);
                return  firstFragment;
            case 1:
                MainSecondFragment secondFragment = MainSecondFragment.newInstance(articleIDLists);
                return  secondFragment;
            case 2:
                //之所以不能刷新数据的原因是  这里 的数据可能没有刷新  我测试一下试一下
                MainThirdFragment thirdFragment = MainThirdFragment.newInstance(user);
                thirdFragment.setInterface(new MainThirdFragment.Test() {
                    @Override
                    public void test() {
                        ((MainActivity)mContext).setToolbarTitle("我");
                    }
                });
                return thirdFragment;
        }
        return null;
    }

    /**
     * Return the number of views available.
            */
    @Override
    public int getCount() {
        return 3;
    }

    public void setUser(User user){
        this.user = user;
    }

    public void setArticleIDLists(ArticleIDList articleIDLists){
        this.articleIDLists = articleIDLists;
    }
}
