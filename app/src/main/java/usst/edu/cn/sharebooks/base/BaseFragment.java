package usst.edu.cn.sharebooks.base;


import com.trello.rxlifecycle2.components.support.RxFragment;

//用于Rx绑定生命周期
public abstract class BaseFragment extends RxFragment {

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            loadWhenVisible();
            //fragment可见时执行加载数据或者进度条等
        } else {
            //不可见时不执行操作
        }
    }

    protected abstract void loadWhenVisible();
}
