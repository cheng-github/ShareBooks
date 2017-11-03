package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.GlideApp;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.event.OpenSellStallDetailEvent;
import usst.edu.cn.sharebooks.model.sellstall.UserSellStallInformation;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class UserSellBookStallAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<UserSellStallInformation> mList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new UserSellStallVH(LayoutInflater.from(context).inflate(R.layout.usersellbooktotalstall_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((UserSellStallVH)holder).bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList != null?mList.size():0;
    }

    private class UserSellStallVH extends RecyclerView.ViewHolder{
        private CircleImageView mCircleImageView;
        private TextView mNickName;
        private ImageView mSex;
        private TextView mContriNumber;
        private TextView mGetNumber;
        private TextView mSellStallNumber;
        private TextView mStallBrife;
        private TextView mBookListNames;

        public UserSellStallVH(View itemView) {
            super(itemView);
            mCircleImageView = (CircleImageView)itemView.findViewById(R.id.civ_touxiang);
            mNickName = (TextView)itemView.findViewById(R.id.tv_nickName);
            mSex = (ImageView)itemView.findViewById(R.id.tv_sex);
            mContriNumber = (TextView)itemView.findViewById(R.id.tv_contri);
            mGetNumber = (TextView)itemView.findViewById(R.id.tv_get);
            mSellStallNumber = (TextView)itemView.findViewById(R.id.tv_bookNumber);
            mStallBrife = (TextView)itemView.findViewById(R.id.tv_sellstalldescri);
            mBookListNames = (TextView)itemView.findViewById(R.id.tv_bookListNames);
        }

        private void bind(final UserSellStallInformation item){
            String s_contri = "贡献书籍:"+item.contri_count;
            String s_get = "交易书籍:"+item.get_count;
            String s_booknumber = "书摊书籍数量:"+item.bookNumber;
            StringBuilder s_bookNames = new StringBuilder();
            s_bookNames.append("所有书籍:");
            for (String one:item.bookNames){
                s_bookNames.append(one+"/");
            }
            s_bookNames.deleteCharAt(s_bookNames.length()-1);
            if (item.imageUrl.equals("?"))
            GlideApp.with(context).load(R.drawable.touxiang).dontAnimate().into(mCircleImageView);
            else {
                String imageURL = ApiInterface.UserImageUrl+item.imageUrl;
                GlideApp.with(context).load(imageURL).dontAnimate().into(mCircleImageView);
            }
            mNickName.setText(item.nickName);
            if (item.sex.equals("?")){
                mSex.setVisibility(View.INVISIBLE);
            }else if (item.sex.equals("男")){
                mSex.setImageResource(R.drawable.male);
            }else {
                mSex.setImageResource(R.drawable.female);
            }
            mContriNumber.setText(s_contri);
            mGetNumber.setText(s_get);
            mSellStallNumber.setText(s_booknumber);
            mBookListNames.setText(s_bookNames.toString());
            mStallBrife.setText(item.sellStalldescription);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RxBus.getInstance().post(new OpenSellStallDetailEvent(item.id));
                }
            });
        }

    }

    public void setList(List<UserSellStallInformation> list){
        this.mList = list;
    }
}
