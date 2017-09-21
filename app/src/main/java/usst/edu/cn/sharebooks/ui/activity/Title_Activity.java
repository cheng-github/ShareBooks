package usst.edu.cn.sharebooks.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import usst.edu.cn.sharebooks.R;

/**
 *
 * Created by Cheng on 2017/7/15.
 */

public class Title_Activity extends Activity implements View.OnClickListener {
        private TextView mTitleTextView;
        private Button mBackwardbButton;
        private ListView mContentList;
        private static String EXTRA_INSTITUTE_NAME = "usst.edu.cn.ui.libaray.title.activity.institute_name";

        @Override
        protected void onCreate(Bundle savedIntance){
        super.onCreate(savedIntance);
        setupViews();
    }

    private void setupViews(){
        super.setContentView(R.layout.institute_layout);
        mTitleTextView = (TextView)findViewById(R.id.text_title);
        mContentList = (ListView) findViewById(R.id.list_item);
        mBackwardbButton = (Button) findViewById(R.id.button_backward);
        //设置标题以及列表
        String institute_name = getIntent().getStringExtra(EXTRA_INSTITUTE_NAME);
        setListAdapter(institute_name);
        setTitle(institute_name);
        //设置返回按钮监听器
        mBackwardbButton.setOnClickListener(this);
        //设置item的监听器
        mContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });//这里我应该直接将所有的http请求都用一个代码发送过去，不然一个个监听的实现实在是麻烦
    }

    private void setListAdapter(String s){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.major_view,getData(s));
        mContentList.setAdapter(adapter);
    }

    private List<String> getData(String name){
        List<String> list = new ArrayList<>();
        switch (name){
            case "光电学院":
                list.add("光电信息科学与工程");
                list.add("电气工程及其自动化");
                list.add("电子信息工程");
                list.add("测控技术与仪器");
                list.add("智能科学与技术");
                list.add("自动化");
                list.add("电子科学与技术");
                list.add("通信工程");
                list.add("网络工程");
                list.add("计算机科学与技术");
                break;
            case "能动学院":
                list.add("能源与动力工程");
                list.add("过程装备与控制工程");
                list.add("新能源科学与工程");
                break;
            case "管理学院":
                list.add("系统科学与工程");
                list.add("交通工程");
                list.add("工业工程");
                list.add("工程管理");
                list.add("信息管理与信息系统");
                list.add("管理科学与工程");
                list.add("公共事业管理");
                list.add("工商管理");
                list.add("市场营销");
                list.add("电子商务");
                list.add("金融学");
                list.add("会计学");
                list.add("国际经济与贸易");
                break;
            case "机械学院":
                list.add("车辆工程");
                list.add("车辆设计制造及其自动化");
                list.add("车辆设计制造及其自动化(中德合作)");
                break;
            case "外语学院":
                list.add("英语(科技翻译)");
                list.add("英语(金融与投资)");
                list.add("英语(国际贸易)");
                list.add("德语专业");
                list.add("日语专业");
                break;
            case "环建学院":
                list.add("环境工程");
                list.add("土木工程");
                list.add("建筑环境与能源应用工程");
                break;
            case "医食学院":
                list.add("医用电子仪器");
                list.add("精密医器械");
                list.add("医疗器械质量与安全");
                list.add("医学影像技术");
                list.add("医学信息工程");
                list.add("假肢矫形工程");
                list.add("制药工程");
                list.add("食品质量与安全");
                list.add("食品科学与工程");
                break;
            case "出版学院":
                list.add("编辑出版学");
                list.add("广告学");
                list.add("传播学");
                list.add("印刷工程");
                list.add("包装工程");
                list.add("环境设计");
                list.add("公共艺术");
                list.add("视觉传达设计");
                list.add("印刷美术设计");
                list.add("产品设计");
                list.add("工业设计");
                list.add("动画");
                break;
            case "理学院":
                list.add("数学与应用数学");
                list.add("应用物理学");
                list.add("应用化学");
                break;
            case "材料学院":
                list.add("材料科学与工程");
                list.add("材料成型及控制工程");
                break;
            case "基础学院":
                //这一块先空着
                break;
            case "继教学院":
                //这一块先空着
                break;
            case "工程学院":
                list.add("数控技术");
                list.add("机电一体化技术");
                list.add("模具设计与制造");
                list.add("电气自动化技术");
                list.add("电子商务");
                break;
            case "中英国际学院":
                //这一块先空着
                break;
            case "汉堡国际工程学院":
                list.add("电气工程及其自动化");
                list.add("机械设计制造及其自动化");
                list.add("国际经济与贸易");
                break;
            case "社科学院":
                //这一块先空着
                break;
            case "体育部":
                //这一块先空着
                break;
            case "音乐系":
                //这一块先空着
                break;
            case "沪江学院":
                //这一块先空着
                break;
            case "太赫兹学院":
                //这一块先空着
                break;
        }
        return list;
    }

    protected void onBackward(){
        //返回按钮点击后出发
        finish();
    }

    //设置标题内容
    @Override
    public void setTitle(int titleId) {
        mTitleTextView.setText(titleId);
    }

    //设置标题内容
    @Override
    public void setTitle(CharSequence title) {
        mTitleTextView.setText(title);
    }
    /* (non-Javadoc)
        * @see android.view.View.OnClickListener#onClick(android.view.View)
        * 按钮点击调用的方法
        */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_backward:
                onBackward();
                break;

        }
    }

    //用于其他activity启动该activity的方法
    public static Intent newIntent(Context last, String institute_name){
        Intent i = new Intent(last,Title_Activity.class);
        i.putExtra(EXTRA_INSTITUTE_NAME,institute_name);
        return i;
    }

}
