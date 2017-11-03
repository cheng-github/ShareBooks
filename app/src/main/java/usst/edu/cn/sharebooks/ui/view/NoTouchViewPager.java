package usst.edu.cn.sharebooks.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class NoTouchViewPager extends ViewPager {
    public NoTouchViewPager(Context context) {
        super(context);
    }

    public NoTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }//这里把这个viewpager的功能废了一半,这也是这三个页面为什么之前不能通过滑动改变的原因了

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }
}
