package usst.edu.cn.sharebooks.util;


import android.content.Context;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import usst.edu.cn.sharebooks.base.BaseActivity;


public class KeyBoardUtils {
    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     */
    public static void openKeyBord(EditText mEditText, BaseActivity activity) {
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager)mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0,InputMethodManager.SHOW_FORCED);
        //我就知道它这里下面写的肯定有问题  因为开启了两次软键盘
        //好了现在问题得到解决了  happy  ending
//        InputMethodManager imm = (InputMethodManager) mContext
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
//                InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * 关闭软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void closeKeyBord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }
}
