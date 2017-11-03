package usst.edu.cn.sharebooks.ui.adapter;


import android.content.Context;
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
import usst.edu.cn.sharebooks.model.event.DeleteSellBookEvent;
import usst.edu.cn.sharebooks.model.sellstall.SellBook;
import usst.edu.cn.sharebooks.network.ApiInterface;


//设置书架的adapter
public class PersonalSellBookDetailAdapter extends RecyclerView.Adapter{
    private List<SellBook> list = new ArrayList<>();
    private Context context;
    private DeleteAction deleteAction;

    public PersonalSellBookDetailAdapter(DeleteAction deleteAction) {
        this.deleteAction = deleteAction;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new SellBookStallViewHolder(LayoutInflater.from(context).inflate(R.layout.sellbookstall_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((SellBookStallViewHolder)holder).bind(list.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }

    public void setList(List<SellBook> list){
        this.list = list;
    }

    public class SellBookStallViewHolder extends RecyclerView.ViewHolder{
        private ImageView mBookImage;
        private TextView mBookName;
        private TextView mAuthor;
        private TextView mPressVerison;
        private TextView mBrifeContent;
        private TextView mBookState;
        private Button mDelete;

        public SellBookStallViewHolder(View itemView) {
            super(itemView);
            mBookImage = (ImageView) itemView.findViewById(R.id.iv_bookimage);
            mBookName = (TextView)itemView.findViewById(R.id.tv_bookName);
            mAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            mPressVerison = (TextView)itemView.findViewById(R.id.tv_pressversion);
            mBrifeContent = (TextView)itemView.findViewById(R.id.tv_bookIntro);
            mBookState = (TextView)itemView.findViewById(R.id.tv_state);
            mDelete = (Button)itemView.findViewById(R.id.tv_delete);
        }

        private void bind(final SellBook sellBook){
            String versionPress = sellBook.press+"/"+sellBook.version;
            String imageUrl = "";
            if (sellBook.isJiaoCai == 1){
                 imageUrl= ApiInterface.AllBookImageUrl+sellBook.imageUrl;//如果等于1 那么是教材  需要拼接图片地址
            }else {
                imageUrl = sellBook.imageUrl;
            }
            Glide.with(context).load(imageUrl).into(mBookImage);
            mBookName.setText(sellBook.bookName);
            mAuthor.setText(sellBook.author);
            mPressVerison.setText(versionPress);
            mBrifeContent.setText(sellBook.bookIntro);
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //应该先进行一个弹窗进行判断
                    final MaterialDialog materialDialog = new MaterialDialog(context);
                    materialDialog.setMessage("确认从书摊中移除这本书?")
                            .setPositiveButton("确认", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    materialDialog.dismiss();
                                //    RxBus.getInstance().post(new DeleteSellBookEvent(sellBook.bookName));
                                    //不使用rxbus进行删除操作
                                    deleteAction.deleteBook(sellBook.bookName);
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
                //    RxBus.getInstance().post(new DeleteSellBookEvent(sellBook.bookName));
                    //没有必要使用接口  这样简单多了
                }
            });
            //to do  bookState的状态设置
        }

    }

    public interface DeleteAction{
        void deleteBook(String bookName);
    }
}
