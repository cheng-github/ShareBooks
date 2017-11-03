package usst.edu.cn.sharebooks.ui.holder;


import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.util.AnimationUtils;
import usst.edu.cn.sharebooks.util.KeyBoardUtils;

public class SearchViewHolder implements View.OnClickListener{
    public static final int RESULT_SEARCH_EMPTY_KEYWORD = 0;
    public static final int RESULT_SEARCH_SEARCH = 1;
    public static final int RESULT_SEARCH_SEARCH_CANCEL = 2;

    private Activity mActivity;
    private OnSearchHandlerListener mListener;
    private View mContentView;
    private ImageView mArrowBack;
    public EditText mSearchContent;
    private ImageView mClearView;
    private ImageView mSearchView;

    public SearchViewHolder(Activity activity,OnSearchHandlerListener listener){
        mActivity = activity;
        mListener = listener;
        mContentView = LayoutInflater.from(activity).inflate(R.layout.search_layout,null);
        initView();
        initEvents();
    }

    private void initView(){
        mArrowBack = (ImageView) mContentView.findViewById(R.id.go_back);
        mSearchContent = (EditText)mContentView.findViewById(R.id.ec_search_content);
        mClearView = (ImageView)mContentView.findViewById(R.id.iv_clear);
        mSearchView = (ImageView)mContentView.findViewById(R.id.iv_search);
    }

    private void initEvents(){
        mArrowBack.setOnClickListener(this);
        mSearchContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    mClearView.setVisibility(View.VISIBLE);
                }else {
                    mClearView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ){
                    if (mSearchContent.getText().toString().isEmpty()){
                        if (mListener != null){
                            mSearchContent.startAnimation(AnimationUtils.shakeAnimation(5));
                            mListener.onSearch(SearchViewHolder.RESULT_SEARCH_EMPTY_KEYWORD);
                        }
                    }
                    else {
                        if (mListener != null){
                            KeyBoardUtils.closeKeyBord(mSearchContent,mActivity);
                            mListener.onSearch(SearchViewHolder.RESULT_SEARCH_SEARCH);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        mClearView.setOnClickListener(this);
        mSearchView.setOnClickListener(this);
    }

    public View getContentView(){
        return  mContentView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.go_back:
                KeyBoardUtils.closeKeyBord(mSearchContent,mActivity);
                if (mListener != null){
                    mListener.onSearch(SearchViewHolder.RESULT_SEARCH_SEARCH_CANCEL);
                }
                break;
            case R.id.iv_clear:
                mSearchContent.setText("");
                break;
            case R.id.iv_search:
                if (mSearchContent.getText().toString().isEmpty()){
                    mSearchContent.startAnimation(AnimationUtils.shakeAnimation(5));
                    if (mListener != null){
                        mListener.onSearch(SearchViewHolder.RESULT_SEARCH_EMPTY_KEYWORD);
                    }
                }else {
                    if (mListener != null){
                        KeyBoardUtils.closeKeyBord(mSearchContent,mActivity);
                        mListener.onSearch(SearchViewHolder.RESULT_SEARCH_SEARCH);
                    }
                }
                break;
            default:
                break;
        }
    }

    public interface OnSearchHandlerListener{
        void onSearch(int code);
    }

    public void onDestroy() {
        mContentView = null;
        mActivity = null;
    }
}
