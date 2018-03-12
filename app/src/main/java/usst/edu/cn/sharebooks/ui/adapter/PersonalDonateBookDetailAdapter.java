package usst.edu.cn.sharebooks.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import usst.edu.cn.sharebooks.R;
import usst.edu.cn.sharebooks.component.RxBus;
import usst.edu.cn.sharebooks.model.donate.UserPersonalDonateStallItem;
import usst.edu.cn.sharebooks.model.event.DeleteDonateBookEvent;
import usst.edu.cn.sharebooks.model.event.DeleteSellBookEvent;
import usst.edu.cn.sharebooks.network.ApiInterface;


public class PersonalDonateBookDetailAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<UserPersonalDonateStallItem> list = new ArrayList<>();
    private DeleteBookAction deleteBookAction;

    public PersonalDonateBookDetailAdapter(DeleteBookAction deleteBookAction) {
        this.deleteBookAction = deleteBookAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PersonalDonateBookDetailViewHolder(LayoutInflater.from(context).inflate(R.layout.sellbookstall_layout,parent,false));
        //使用同一个布局也是可以的吧
        //可以为这个布局加一点margin  待会儿我们再过来测试
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((PersonalDonateBookDetailViewHolder)holder).bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class PersonalDonateBookDetailViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;
        private TextView mBookState;
        private Button mDelete;

        public PersonalDonateBookDetailViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView) itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
            mBookState = (TextView)itemView.findViewById(R.id.tv_state);
            mDelete = (Button)itemView.findViewById(R.id.tv_delete);
        }

        private void bind(final UserPersonalDonateStallItem item){
            String versionPress = item.press+"/"+item.version;
            String imageUrl = "";
            if (item.isJiaoCai == 1){
                imageUrl= ApiInterface.AllBookImageUrl+ Uri.encode(item.imageUrl);//如果等于1 那么是教材  需要拼接图片地址
            }else {
                imageUrl = item.imageUrl;
            }
            Glide.with(context).load(imageUrl).into(mBookImage);
            mBookName.setText(item.bookName);
            mAuthor.setText(item.author);
            mPressVerison.setText(versionPress);
            mBrifeContent.setText(item.bookIntro);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //to do ...
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setMessage("确认取消贡献这本书?")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                              //      RxBus.getInstance().post(new DeleteDonateBookEvent(item.bookName,item.donateUser_id));
                                    //暂时不清楚原因
                                    deleteBookAction.deleteBook(item.bookName,item.donateUser_id);
                                    //使用接口异常的顺利....
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                }
                            })
                            .setCanceledOnTouchOutside(true)
                            .show();
                }
            });
        }
    }

    public void setList(List<UserPersonalDonateStallItem> list){
        this.list = list;
    }

    public interface DeleteBookAction{
        void deleteBook(String bookName,int userId);
    }
}
