package usst.edu.cn.sharebooks.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.ui.activity.SearchActivity;
import usst.edu.cn.sharebooks.ui.activity.Title_Activity;

/**
 * 主界面的viewpager每个fragment的加载
 * Created by Cheng on 2017/7/11.
 */

public class MyFragment extends Fragment implements View.OnClickListener{
    private static String Section_Number="section_number";
    View rootView=null;

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
        rootView = inflater.inflate(
                R.layout.main_acrivity_fragment, container, false);
        switch (section_number){
            case 1:
                rootView = inflater.inflate(R.layout.first_fragment,container,false);
                setupFragment1(rootView);
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

    private void setupFragment1(View rootview){
        ((TextView)rootview.findViewById(R.id.search_begin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                startActivity(intent);
            }
        });
        rootview.findViewById(R.id.one).setOnClickListener(this);
        rootview.findViewById(R.id.two).setOnClickListener(this);
        rootview.findViewById(R.id.three).setOnClickListener(this);
        rootview.findViewById(R.id.four).setOnClickListener(this);
        rootview.findViewById(R.id.five).setOnClickListener(this);
        rootview.findViewById(R.id.six).setOnClickListener(this);
        rootview.findViewById(R.id.seven).setOnClickListener(this);
        rootview.findViewById(R.id.eight).setOnClickListener(this);
        rootview.findViewById(R.id.nine).setOnClickListener(this);
        rootview.findViewById(R.id.ten).setOnClickListener(this);
        rootview.findViewById(R.id.elven).setOnClickListener(this);
        rootview.findViewById(R.id.twelve).setOnClickListener(this);
        rootview.findViewById(R.id.thirteen).setOnClickListener(this);
        rootview.findViewById(R.id.foutteen).setOnClickListener(this);
        rootview.findViewById(R.id.fifteen).setOnClickListener(this);
        rootview.findViewById(R.id.sixteen).setOnClickListener(this);
        rootview.findViewById(R.id.seventeen).setOnClickListener(this);
        rootview.findViewById(R.id.eighteen).setOnClickListener(this);
        rootview.findViewById(R.id.nineteen).setOnClickListener(this);
        rootview.findViewById(R.id.twenty).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.guangdian)));
                break;
            case R.id.two:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.nengdong)));
                break;
            case R.id.three:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.guanli)));
                break;
            case R.id.four:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.jixie)));
                break;
            case R.id.five:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.waiyu)));
                break;
            case R.id.six:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.huanjian)));
                break;
            case R.id.seven:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.yishi)));
                break;
            case R.id.eight:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.chupan)));
                break;
            case R.id.nine:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.li)));
                break;
            case R.id.ten:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.cailiao)));
                break;
            case R.id.elven:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.jichu)));
                break;
            case R.id.twelve:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.jixu)));
                break;
            case R.id.thirteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.gongji)));
                break;
            case R.id.foutteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.zhonyin)));
                break;
            case R.id.fifteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.zhonde)));
                break;
            case R.id.sixteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.sheke)));
                break;
            case R.id.seventeen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.tiyu)));
                break;
            case R.id.eighteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.yinyue)));
                break;
            case R.id.nineteen:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.hujiang)));
                break;
            case R.id.twenty:
                startActivity(Title_Activity.newIntent(getActivity(),getResources().getString(R.string.taikezi)));
                break;
        }
    }
}

