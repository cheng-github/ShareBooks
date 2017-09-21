package usst.edu.cn.sharebooks.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import usst.edu.cn.sharebooks.R;

/**
 *
 * Created by Cheng on 2017/5/25.
 */

public class SearchActivity extends AppCompatActivity {
    FloatingSearchView floatingSearchView;
    @Override
    protected void onCreate(Bundle savedIntanced){
        super.onCreate(savedIntanced);
        setContentView(R.layout.activity_search);
        floatingSearchView = (FloatingSearchView)this.findViewById(R.id.floating_search_view);
        setupSearch();
    }

    private void setupSearch(){
        //这是text输入时的监听器的实现,不是搜索按钮执行的时候
        floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {

            }
        });
        //这是菜单点击事件的实现
        floatingSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.setting:
                        Toast.makeText(SearchActivity.this,"setting",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.others:
                        Toast.makeText(SearchActivity.this,"others",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        //搜索按钮点击事件的监听
        floatingSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            //建议按钮的点击
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {

            }
            //搜索按钮的点击
            @Override
            public void onSearchAction(String currentQuery) {
                Toast.makeText(SearchActivity.this,currentQuery,Toast.LENGTH_SHORT).show();
            }
        });
        //提示框的实现
        floatingSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {

            }
        });
    }
}
